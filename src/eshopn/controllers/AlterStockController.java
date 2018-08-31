/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Gestionstock;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import eshopn.entities.controllers.GestionstockJpaController;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import eshopn.models.GlobalNotifications;
import eshopn.models.Res;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class AlterStockController extends Controllers implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private JFXTextField qteField;

    @FXML
    private JFXButton validBtn;

    @FXML
    private Label codeLabel;

    @FXML
    private Label qteMaxLabel;
    
    @FXML
    private ImageView imgView;
    
    
    private boolean operation;
    private Produit current_produit;
    private ListeProduitsController listProdCont;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews(); 
    }

    @FXML
    void onClose(ActionEvent event) {
        getStage().close();
    }

    @FXML
    void onSet(ActionEvent event) {
        
        try {
            int newQte=Integer.valueOf(qteField.getText().trim());
            
            int currentQte=current_produit.getQte();
        
            GestionstockJpaController gestCont=new GestionstockJpaController(Res.emf);
            ProduitJpaController proCont=new ProduitJpaController(Res.emf);
            
            if(isOperation()){
                
                try {
                    Gestionstock stock=new Gestionstock(newQte,
                            (new GregorianCalendar()).getTime(),
                            Res.connected_storekeeper,
                            current_produit
                            ,isOperation());

                    current_produit.setQte(currentQte+newQte);
                    
                    proCont.edit(current_produit);
                    gestCont.create(stock);

                    listProdCont.init();

                    getStage().close();

                    Res.not.showNotifications("Confirmation de l'approvisionnement", 
                            "Le produit "+current_produit.getNomPro()+" a été approvisionné avec succès"
                            , GlobalNotifications.SUCCESS_NOT, 3, false);
                    
                } catch (Exception ex) {
                    Res.not.showNotifications("Echec", 
                            "Impossible de se connecter au serveur."
                            , GlobalNotifications.ECHEC_NOT, 3, false);
                }

            }else{
                if(newQte<=currentQte){
                    
                    try {
                        
                        Gestionstock stock=new Gestionstock(newQte,
                                (new GregorianCalendar()).getTime(),
                                Res.connected_storekeeper,
                                current_produit
                                ,isOperation());

                        current_produit.setQte(currentQte-newQte);

                        proCont.edit(current_produit);
                        gestCont.create(stock);
                        
                        listProdCont.init();
            
                        getStage().close();
                        
                        Res.not.showNotifications("Confirmation du retrait",
                            "Le retrait de la quantité du produit "+current_produit.getNomPro()+"\n"
                                    + "s'est effectué avec succès"
                            , GlobalNotifications.SUCCESS_NOT, 3, false);
                        
                        
                    } catch (Exception ex) {
                        Res.not.showNotifications("Echec",
                            "Impossible de se connecter au serveur."
                            , GlobalNotifications.ECHEC_NOT, 3, false);
                    }
                }else{
                    Res.not.showNotifications("Echec du retrait", 
                            "La quantité à retirer doit être inférieure à la quantité actuelle"
                            , GlobalNotifications.ECHEC_NOT, 3, false);
                }
            }
            
        } catch (Exception e) {
            
            String oper=(isOperation())?"de l'ajout du stock":"du retrait du stock";
            Res.not.showNotifications("Echec "+oper, 
                    "Le prix doit être un réel"
                    , GlobalNotifications.ECHEC_NOT, 3, false);
        }
        
    }

    

    @Override
    public void init() {
        
        if(isOperation()) {
            titleLabel.setText("Ajout de stock");
            validBtn.setText("Ajouter");
        }
        else {
            titleLabel.setText("Reduction de stock");
            validBtn.setText("Retirer");
        }
        
        codeLabel.setText(formatCode(current_produit.getCodePro().toString()));
        qteMaxLabel.setText(current_produit.getQte()+"");
        
        Photo pht=(new ArrayList<>(current_produit.getPhotoCollection())).get(0);
        
        if(Res.config.getModeStockageImage()==1){
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    
                    try {

                        File file=new File(Res.config.getDossierImagesLocal()
                            +current_produit.getCodePro()+"/"+pht.getLienPhoto());
                            
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    imgView.setImage(new Image(file.toURI().toURL().toExternalForm()));
                                } catch (MalformedURLException ex) {
                                    Logger.getLogger(AlterStockController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });

                    } catch (Exception ex) {
                        Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    
                }
            }).start();
            
        }else{
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        
                        URL url = new URL(lienAbsolueImage(pht));
                        InputStream is = url.openStream();
                        
                        imgView.setImage(new Image(is));
                        
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            
            
        }
        
        
    }
    
    
    @Override
    protected void setImagesToImageViews() {
        imgView.setImage(new Image("Produits/default.png"));
    }
    
    public boolean isOperation() {
        return operation;
    }

    public void setOperation(boolean operation) {
        this.operation = operation;
    }

    public Produit getCurrent_produit() {
        return current_produit;
    }

    public void setCurrent_produit(Produit current_produit) {
        this.current_produit = current_produit;
    }

    public ListeProduitsController getListProdCont() {
        return listProdCont;
    }

    public void setListProdCont(ListeProduitsController listProdCont) {
        this.listProdCont = listProdCont;
    }
    
    
    
}
