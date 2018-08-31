/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.controllers.CategorieJpaController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import eshopn.entities.Categorie;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import eshopn.models.GlobalNotifications;
import eshopn.models.Res;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class NouvelleCategorieController extends Controllers implements Initializable {

    @FXML
    private Label titleLabel;
    
    @FXML
    private JFXButton validBtn;
    
    @FXML
    private JFXTextField categorieField;
    
    public static AnchorPane content;
    private boolean isNew;
    private  Categorie current_categorie;
    private ListeCategoriesController listCatCont;
    
    @FXML
    private StackPane stack;

    @FXML
    private ImageView loader;

    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loader.setImage(new Image("chargement2.gif"));
    }    

    @FXML
    void onAdd(ActionEvent event) {
        
        stack.setVisible(true);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                CategorieJpaController cont=new CategorieJpaController(Res.emf);
        
                if(!categorieField.getText().trim().isEmpty()){

                    if(categorieField.getText().trim().length()<60){

                        if(isNew){
                            Categorie cat=new Categorie(categorieField.getText().trim());
                            try {
                                cont.create(cat);

                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        

                                        getStage().close();

                                        Res.not.showNotifications("Confirmation Création Nouvelle catégorie", 
                                                            "La catégorie "+categorieField.getText().trim()+" a été crée avec succès"
                                                            , GlobalNotifications.SUCCESS_NOT, 3, false);

                                        stack.setVisible(false);
                                        
                                        listCatCont.init();
                                    }
                                });

                            } catch (Exception e) {
                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        stack.setVisible(false);
                                        Res.not.showNotifications("Echec ", 
                                                    "Impossible de se connecter au serveur."
                                                    , GlobalNotifications.ECHEC_NOT, 3, false);
                                    }
                                });
                                
                            }
                        }else{

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    current_categorie.setNomCat(categorieField.getText().trim());

                                    try {
                                        cont.edit(current_categorie);


                                        getStage().close();

                                        Res.not.showNotifications("Confirmation Création Nouvelle catégorie", 
                                                            "La catégorie "+categorieField.getText().trim()+" a été crée avec succès"
                                                            , GlobalNotifications.SUCCESS_NOT, 3, false);
                                        stack.setVisible(false);
                                        listCatCont.init();
                                        
                                    } catch (Exception ex) {
                                        stack.setVisible(false);
                                        Res.not.showNotifications("Echec", 
                                                    "Impossible de se connecter au serveur."
                                                    , GlobalNotifications.ECHEC_NOT, 3, false);
                                    }
                                }
                            });
                            
                        }



                    }else{
                        stack.setVisible(false);
                        Res.not.showNotifications("Echec", 
                                            "Le nom de la catégorie doit tenir sur 60 caractères"
                                            , GlobalNotifications.ECHEC_NOT, 3, false);
                    }

                }else{
                    stack.setVisible(false);
                    Res.not.showNotifications("Echec", 
                                        "Le nom de la catégorie ne peut être vide"
                                        , GlobalNotifications.ECHEC_NOT, 3, false);
                }
            }
        }).start();
        
        
        
    }

    @FXML
    void onClose(ActionEvent event) {
        getStage().close();
    }

    @Override
    public void init() {
        if(isNew){
            titleLabel.setText("Nouvelle catégorie");
            validBtn.setText("Ajouter");
        }else{
            titleLabel.setText("Modifier catégorie");
            validBtn.setText("Modifier");
            categorieField.setText(current_categorie.getNomCat());
        }
    }

    @Override
    protected void setImagesToImageViews() {
   
    }

    

    /**
     * @return the isNew
     */
    public boolean isIsNew() {
        return isNew;
    }

    /**
     * @param isNew the isNew to set
     */
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * @return the current_categorie
     */
    public Categorie getCurrent_categorie() {
        return current_categorie;
    }

    /**
     * @param current_categorie the current_categorie to set
     */
    public void setCurrent_categorie(Categorie current_categorie) {
        this.current_categorie = current_categorie;
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
