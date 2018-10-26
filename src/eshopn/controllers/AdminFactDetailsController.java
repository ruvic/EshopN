/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import eshopn.entities.Facture;
import eshopn.entities.Lignefacture;
import eshopn.entities.Photo;
import eshopn.entities.controllers.LignefactureJpaController;
import eshopn.models.MFact;
import eshopn.models.MFacture;
import eshopn.models.Res;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class AdminFactDetailsController extends Controllers implements Initializable {

    @FXML
    private Rectangle rect;

    @FXML
    private ImageView fondImgView;

    @FXML
    private TableView<MFact> table;

    @FXML
    private TableColumn<MFact, String> codeProColumn;

    @FXML
    private TableColumn<MFact, String> prixUnitColumn;

    @FXML
    private TableColumn<MFact, String> qteColumn;

    @FXML
    private TableColumn<MFact, String> sousTotalColumn;

    @FXML
    private Label dateLabel;

    @FXML
    private Label netAPayerLabel;

    @FXML
    private Label montantTotalLabel;

    @FXML
    private Label remiseLabel;

    @FXML
    private Label modePaiementLabel;

    @FXML
    private Label TelClientLabel;

    @FXML
    private Label numFactLabel;

    @FXML
    private Label caissierLabel;

    @FXML
    private AnchorPane imgPane;

    @FXML
    private ImageView imgView;
    
    private Facture facture;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        
        codeProColumn.setCellValueFactory(new PropertyValueFactory<>("codeProduit"));
        prixUnitColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));
        sousTotalColumn.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));
        
        
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MFact>(){
           @Override
           public void changed(ObservableValue<? extends MFact> observable, MFact oldValue, MFact newValue) {
                if(newValue!=null && newValue!=oldValue){
                    Photo pht=(new ArrayList<>(newValue.getProduit().getPhotoCollection())).get(0);
                    
                    if(Res.config.getModeStockageImage()==1){
                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                File file=new File(Res.config.getDossierImagesLocal()
                                        +newValue.getProduit().getCodePro().toString()
                                        +"/"+pht.getLienPhoto());

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            imgView.setImage(new Image(file.toURI().toURL().toExternalForm()));
                                        } catch (MalformedURLException ex) {
                                            Logger.getLogger(StocksController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });

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
           }   
        });
        
    }    

    @Override
    public void init() {
        
        //Récupération des réponses d'en-tête
        MFacture mFacture = new MFacture(facture);
        
        //Récupération des ligne de facture (données du tableau du bas)
        ObservableList<MFact> listeFact=FXCollections.observableArrayList();
        LignefactureJpaController contLignFact=new LignefactureJpaController(Res.emf);
        List<Lignefacture> liste1=contLignFact.findLignefactureEntities(facture);

        for (Lignefacture lFact : liste1) {

            MFact mfact=new MFact(formatCode(""+lFact.getProduit().getCodePro())
                    , lFact.getPrix().doubleValue()
                    , (int)lFact.getQte()
                    , lFact.getQte()*lFact.getPrix().doubleValue()
                    , lFact.getProduit()
            );

            listeFact.add(mfact);
        }
        
        //Mise à jour des interfaces
        dateLabel.setText(mFacture.getDate());
        caissierLabel.setText(mFacture.getNomCaissier());
        numFactLabel.setText(mFacture.getNumFact());
        TelClientLabel.setText(mFacture.getTel());
        modePaiementLabel.setText(mFacture.getTypeFact());
        remiseLabel.setText(mFacture.getRemise());
        montantTotalLabel.setText(mFacture.getMontant());
        netAPayerLabel.setText(mFacture.getMontNAP());
        
        table.setItems(listeFact);
        
        
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rect.setWidth(newValue.doubleValue());
                fondImgView.setFitWidth(newValue.doubleValue());
            } 
        });
        
        getScene().heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rect.setHeight(newValue.doubleValue());
                fondImgView.setFitHeight(newValue.doubleValue());
            }
        });
    }
    
    
    @FXML
    void onControlReleased(KeyEvent event) {
        if(event.getCode()==KeyCode.SHIFT){
            imgPane.setVisible(false);
        }
    }
    
    @FXML
    void onControlPressed(KeyEvent event) {
        
        if(event.getCode()==KeyCode.SHIFT){
            imgPane.setVisible(true);
        }
    }

    @Override
    protected void setImagesToImageViews() {
        Image fond=new Image("fond.jpg");
        fondImgView.setImage(fond);
        imgView.setImage(new Image("Produits/default.png"));
    }

    /**
     * @return the facture
     */
    public Facture getFacture() {
        return facture;
    }

    /**
     * @param facture the facture to set
     */
    public void setFacture(Facture facture) {
        this.facture = facture;
    }
    
}
