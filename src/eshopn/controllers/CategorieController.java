/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXButton;
import eshopn.entities.Categorie;
import eshopn.entities.Produit;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.models.Res;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 * 
 * @author FRANCINE
 */
public class CategorieController extends Controllers implements Initializable {
    
    @FXML
    private Label categorieTitle;

    @FXML
    private Label vuesLabel;
    
    @FXML
    private JFXButton editBtn;
    

    @FXML
    private Label priceLabel;
    
    private AnchorPane content;
    private FXMLLoader loader;
    private Categorie categorie;
    
    private ListeCategoriesController listCatCont;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(!Res.connected_storekeeper.getTypeGest()) 
            editBtn.setVisible(false);
    }    


    @FXML
    void onEdit(ActionEvent event) {
        getMain().showNouvelleCategorie(false, categorie, listCatCont);
    }

    @FXML
    void onShowProduits(ActionEvent event) {
        
        loader=getMain().replace("ListeProduits.fxml", content);
        ListeProduitsController cat = loader.getController();
        cat.setMain(this.getMain());
        cat.setContent(content);
        cat.setScene(getScene());
        cat.setStage(getStage());
        cat.setCurrent_categorie(categorie);
        cat.init();
        
    }

    @Override
    public void init() {
        categorieTitle.setText(categorie.getNomCat());
        
        ProduitJpaController cont=new ProduitJpaController(Res.emf);
        List<Produit> listProd=cont.findProductsCategorie(categorie);
        priceLabel.setText(listProd.size()+"");

        content=Res.content;
    }

    @Override
    protected void setImagesToImageViews() {
        
    }
    
    public AnchorPane getContent() {
        return content;
    }

    public void setContent(AnchorPane content) {
        this.content = content;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    /**
     * @return the listCatCont
     */
    public ListeCategoriesController getListCatCont() {
        return listCatCont;
    }

    /**
     * @param listCatCont the listCatCont to set
     */
    public void setListCatCont(ListeCategoriesController listCatCont) {
        this.listCatCont = listCatCont;
    }
    
    
}
