/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Categorie;
import eshopn.entities.Produit;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.PDFProduit;
import eshopn.models.PDFProduitPub;
import eshopn.models.PrintProduitPubTask;
import eshopn.models.PrintProduitTask;
import eshopn.models.Res;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class ListeProduitsController extends Controllers implements Initializable {

    
     @FXML
    private ImageView arrowsImg;

    @FXML
    private ImageView prod2Img;

    @FXML
    private Label prod2Text;

    @FXML
    private ImageView categorieImg;

    @FXML
    private Label categorieText;

    @FXML
    private JFXButton newProductBtn;

    @FXML
    private Label nomCat;

    @FXML
    private JFXTextField codeProdField;

    @FXML
    private Pagination pagination;

    @FXML
    private GridPane listProd;
    
    @FXML
    private AnchorPane anchor;

    @FXML
    private JFXTextField codeFourField;

    @FXML
    private JFXTextField qteField;

    @FXML
    private JFXButton printBtn;

    @FXML
    private JFXButton printPubBtn;

    @FXML
    private JFXButton categorieBtn;

    @FXML
    private ImageView prod1Img;
    
    @FXML
    private ImageView loaderImg;
    
    @FXML
    private Label prod1Text;

    
    private AnchorPane content;
    private FXMLLoader loader;
    private Categorie current_categorie;
    private List<Produit> listes;
    private List<Produit> listesGen;
    
    public final double STAGE_MIN_WIDTH=800;
    public final double STAGE_MIN_HEIGHT=700;
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        loaderImg.setVisible(false);
        Res.listeProduit=listProd;
        Res.reset();
        
        if( !Res.connected_storekeeper.getTypeGest()){
            newProductBtn.setVisible(false);
        }
    }    

    @FXML
    void onCategorie(ActionEvent event) {
        
        loader = getMain().replace("ListeCategories.fxml",content);
        ListeCategoriesController cat = loader.getController();
        cat.setMain(this.getMain());
        cat.setContent(content);
        cat.setScene(getScene());
        cat.setStage(getStage());
        cat.init();
    }

    @FXML
    void onHome(ActionEvent event) {
        if(Res.connected_storekeeper.getTypeGest()){
            getMain().replace("MagasinierDashboard.fxml", content);
        }else{
            loader=getMain().replace("Facturation.fxml",content);
            FacturationController cat = loader.getController();
            cat.setMain(this.getMain());
            cat.setContent(content);
            cat.setScene(getScene());
            cat.setStage(getStage());
            cat.init();
        }
    }

    @FXML
    void onNewProduct(ActionEvent event) {
        getMain().showNouveauProduit(current_categorie, this);
    }

    @FXML
    void onSearch(ActionEvent event) {
        
        List<Produit> listePro=new ArrayList<>();
        
        Res.not.showLoader(Res.stackPane);
        
        if(qteField.getText().trim().isEmpty()){
            if(codeFourField.getText().trim().isEmpty()){
                if(codeProdField.getText().trim().isEmpty()){
                    listePro=listes;
                }else{
                    for (Produit pr : listes) {
                        if(formatCode(pr.getCodePro().toString()).contains(codeProdField.getText().trim())){
                            listePro.add(pr);
                        }
                    }
                }
            }else{
               if(codeProdField.getText().trim().isEmpty()){
                    for (Produit pr : listes) {
                        if(pr.getCodeFour().contains(codeFourField.getText().trim())){
                            listePro.add(pr);
                        }
                    }
                }else{
                    for (Produit pr : listes) {
                        if(pr.getCodeFour().contains(codeFourField.getText().trim())
                                && formatCode(pr.getCodePro().toString()).contains(codeProdField.getText().trim())){
                            listePro.add(pr);
                        }
                    }
                } 
            }
            
            setPaginationProperties(Res.NbreEltParLigne*2, listePro);
                
            Res.stackPane.setVisible(false);
            
        }else{
            try {
                int b=Integer.parseInt(qteField.getText().trim());
                if(codeFourField.getText().trim().isEmpty()){
                    if(codeProdField.getText().trim().isEmpty()){
                        for (Produit pr : listes) {
                            if((pr.getQte()+"").equals(qteField.getText().trim())){
                                listePro.add(pr);
                            }
                        }
                    }else{
                        for (Produit pr : listes) {
                            if(formatCode(pr.getCodePro().toString()).contains(codeProdField.getText().trim())
                                    && (pr.getQte()+"").equals(qteField.getText().trim())){
                                listePro.add(pr);
                            }
                        }
                    }
                }else{
                   if(codeProdField.getText().trim().isEmpty()){
                        for (Produit pr : listes) {
                            if(pr.getCodeFour().contains(codeFourField.getText().trim())
                                    && (pr.getQte()+"").equals(qteField.getText().trim())){
                                listePro.add(pr);
                            }
                        }
                    }else{
                        for (Produit pr : listes) {
                            if(pr.getCodeFour().contains(codeFourField.getText().trim())
                                    && formatCode(pr.getCodePro().toString()).contains(codeProdField.getText().trim())
                                    && (pr.getQte()+"").equals(qteField.getText().trim())){
                                listePro.add(pr);
                            }
                        }
                    } 
                }
                
                setPaginationProperties(Res.NbreEltParLigne*2, listePro);
                
                Res.stackPane.setVisible(false);
                
            } catch (NumberFormatException e) {
                Res.not.showNotifications("Données incorrectes", 
                    "La quantité doit être un entier naturelle"
                    , GlobalNotifications.ECHEC_NOT, 2, false);
            }
            
        }
        
        listesGen=listePro;

    }

    @Override
    public void init() {
        ProduitJpaController cont=new ProduitJpaController(Res.emf);
        listes=cont.findProduitEntitiesOrder();
        
        if(current_categorie!=null){
            nomCat.setText(current_categorie.getNomCat());
            List<Produit> list=new ArrayList<>();
            
            if(!Res.connected_storekeeper.getTypeGest()){
                printBtn.setText("Imprimer Pub");
                printPubBtn.setVisible(false);
                AnchorPane.setLeftAnchor(nomCat, 165.0);
                printBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            onPrintPub(event);
                        } catch (IOException ex) {
                            Logger.getLogger(ListeProduitsController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DocumentException ex) {
                            Logger.getLogger(ListeProduitsController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            
            for (Produit pr : listes) {
                if(!pr.getIdCategorie().getIdCat().equals(current_categorie.getIdCat())){
                    list.add(pr);
                }
            }
            
            for (Produit pr : list) {
                listes.remove(pr);
            }
            
        }else{
            nomCat.setText("");
            newProductBtn.setVisible(false);
            if(!Res.connected_storekeeper.getTypeGest()){
                printBtn.setText("Imprimer Pub");
                printPubBtn.setVisible(false);
                printBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            onPrintPub(event);
                        } catch (IOException ex) {
                            Logger.getLogger(ListeProduitsController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DocumentException ex) {
                            Logger.getLogger(ListeProduitsController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }

            prod1Img.setVisible(true);
            prod1Text.setVisible(true);
            prod2Img.setVisible(false);
            prod2Text.setVisible(false);
            categorieBtn.setVisible(false);
            categorieImg.setVisible(false);
            categorieText.setVisible(false);
        }
        
        setPaginationProperties(Res.NbreEltParLigne*2, listes);
        
        listesGen=listes;
        
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setPaginationProperties(Res.NbreEltParLigne*2, listesGen);
                getStage().centerOnScreen();
            }   
        });
        
    }
    
    @FXML
    void onPrint(ActionEvent event) throws IOException, DocumentException {
        PrintProduitTask task=new PrintProduitTask(listesGen, loaderImg);
        Thread t=new Thread(task);
        t.start();
    }
    
    @FXML
    void onPrintPub(ActionEvent event) throws IOException, DocumentException {
        PrintProduitPubTask task=new PrintProduitPubTask(listesGen, loaderImg);
        Thread t=new Thread(task);
        t.start();
    }
    
    public void setPaginationProperties(int itemPerPage, List<Produit> listP){
        Res.reset();
        int pages, nbreElts=listP.size();
        
        if(nbreElts%itemPerPage==0){
            pages=nbreElts/itemPerPage;
        }else{
            pages=(nbreElts/itemPerPage) + 1;
        }
        
        pages=(pages==0)?1:pages;
        pagination.setPageCount(pages);
        
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > listP.size() / itemPerPage + 1) {
                    return null;
                } else {
                    return createPage(pageIndex,listP,itemPerPage);
                }
            }            
        });
    }
    
    private Node createPage(Integer pageIndex, List<Produit> list, int itemPerPage) {
        Res.reset();
        Res.listeProduit.getChildren().clear();
        int nb=(pageIndex+1)*itemPerPage;
        for (int i = nb-itemPerPage; i<list.size() && i < nb; i++) {
            Produit prod=list.get(i);
            Res.listeProduit.add(getMain().getProductView(getContent(), prod, this)
                    , Res.getColumnIndex()
                    , Res.getIndexLine());
        }
        return (new AnchorPane());
    }
    
    @Override
    protected void setImagesToImageViews() {
        Image arrow=new Image("arrow.png");
        Image arrow_selected=new Image("arrow_selected.png");
        
        loaderImg.setImage(new Image("chargement2.gif"));
        arrowsImg.setImage(arrow);
        categorieImg.setImage(arrow);
        prod2Img.setImage(arrow_selected);   
        prod1Img.setImage(arrow_selected);   
    }

    /**
     * @return the content
     */
    public AnchorPane getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(AnchorPane content) {
        this.content = content;
    }

    public Categorie getCurrent_categorie() {
        return current_categorie;
    }

    public void setCurrent_categorie(Categorie current_categorie) {
        this.current_categorie = current_categorie;
    }
    
    
    
    
}
