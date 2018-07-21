/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import eshopn.entities.Photo;
import eshopn.entities.Produit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class ProduitPDFController extends Controllers implements Initializable {

    @FXML
    private ImageView photo;

    @FXML
    private Label codeProduct;

    @FXML
    private Text nomText;

    @FXML
    private Text descriptionText;


    private Produit produit;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
    }    

    @Override
    public void init() {
        codeProduct.setText(formatCode(produit.getCodePro().toString()));
        nomText.setText(produit.getNomPro());
        descriptionText.setText(produit.getDescription());
        
        Photo pht=(new ArrayList<>(produit.getPhotoCollection())).get(0);
        try {
            
            URL url = new URL(lienAbsolueImage(pht));
            InputStream is = url.openStream();
            photo.setImage(new Image(is));
            is.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void setImagesToImageViews() {
        photo.setImage(new Image("Produits/robe.jpg"));
    }

    /**
     * @return the produit
     */
    public Produit getProduit() {
        return produit;
    }

    /**
     * @param produit the produit to set
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
}
