/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import eshopn.entities.Categorie;
import eshopn.models.Page;
import eshopn.models.Res;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class AllCategoriesController extends Controllers{
    
    @FXML
    private Label dateLabel;

    @FXML
    private Label adresseLabel;

    @FXML
    private Label telLabel;

    @FXML
    private VBox content;
    
    private List<Categorie> allCategorie;
    
    public static Page page;
    
    /**
     * Initializes the controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void init() {
        dateLabel.setText(" "+dateActuelle());
        adresseLabel.setText(" "+Res.config.getAdresse());
        telLabel.setText(" "+Res.config.getTel());
        
        for (Categorie categorie : allCategorie) {
            content.getChildren().add(getMain().getCategoriePDF(allCategorie, page));
        }
    }
    
    public String dateActuelle(){
        Date actuelle=new Date();
        DateFormat df=new SimpleDateFormat("EEEEEEEE, d MMMMMMMMMMMM yyyy\nHH:mm:ss");
        return df.format(actuelle);
    }

    @Override
    protected void setImagesToImageViews() {
        
    }

    /**
     * @return the allCategorie
     */
    public List<Categorie> getAllCategorie() {
        return allCategorie;
    }

    /**
     * @param allCategorie the allCategorie to set
     */
    public void setAllCategorie(List<Categorie> allCategorie) {
        this.allCategorie = allCategorie;
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Page page) {
        this.page = page;
    }
    
}
