/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXButton;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.models.Res;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class ProduitController extends Controllers implements Initializable {

    @FXML
    private ImageView photo;
    @FXML
    private Label numberProduct;
    @FXML
    private Label codeProduct;
    
    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton reduceBtn;
    
    private AnchorPane content;
    private ListeProduitsController listProdCont;
    private Produit current_product;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        
        if(!Res.connected_storekeeper.getTypeGest()){
            reduceBtn.setVisible(false);
            addBtn.setVisible(false);
        }
        
    }    

    @Override
    public void init() {
        codeProduct.setText(formatCode(current_product.getCodePro()+""));
        numberProduct.setText(current_product.getQte()+"");
        
        /** Récupération d'une image du produit **/
        PhotoJpaController cont=new PhotoJpaController(Res.emf);
        Photo pht=(new ArrayList<>(current_product.getPhotoCollection())).get(0);
        
        
        try {
        
            File file=new File(Res.config.getDossierImagesLocal()
                +current_product.getCodePro()+"/"+pht.getLienPhoto());
        
            photo.setImage(new Image(file.toURI().toURL().toExternalForm()));
            
            /*try {
            
            URL url = new URL(lienAbsolueImage(pht));
            InputStream is = url.openStream();
            photo.setImage(new Image(is));
            is.close();
            } catch (FileNotFoundException ex) {
            Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
            Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    protected void setImagesToImageViews() {
        Image robe = new Image("Produits/robe.jpg");
        
        photo.setImage(robe);
    }
    
     @FXML
    void onAddStock(ActionEvent event) {
        getMain().showGestionStock(true, current_product, listProdCont);
    }

    @FXML
    void onReduceStock(ActionEvent event) {
        getMain().showGestionStock(false, current_product,listProdCont);
    }

    @FXML
    void onShowProductDetails(ActionEvent event) {
        getMain().showDetailsProduit(current_product,listProdCont);
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

    public ListeProduitsController getListProdCont() {
        return listProdCont;
    }

    public void setListProdCont(ListeProduitsController listProdCont) {
        this.listProdCont = listProdCont;
    }

    public Produit getCurrent_product() {
        return current_product;
    }

    public void setCurrent_product(Produit current_produit) {
        this.current_product = current_produit;
    }
    
    
    
    
    
    
}
