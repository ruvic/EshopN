/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import eshopn.entities.controllers.GestionnaireJpaController;
import eshopn.models.MGestionnaire;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import eshopn.entities.Gestionnaire;
import eshopn.models.GlobalNotifications;
import eshopn.models.Res;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class AdminEditGestionnaireController extends Controllers implements Initializable {
    
    @FXML
    private Label titleLabel;

    @FXML
    private JFXTextField nameField;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField pwdField;

    @FXML
    private JFXPasswordField confirmpwdField;

    @FXML
    private JFXToggleButton actifToogleBtn;

    public static boolean isStorekeeper;
    public static MGestionnaire current_gestionnaire;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        titleLabel.setText("Modifier "+((isStorekeeper)?"Magasinier":"Caissier"));
        
        nameField.setText(current_gestionnaire.getGest().getNomGest());
        usernameField.setText(current_gestionnaire.getGest().getLogin());
        pwdField.setText(current_gestionnaire.getGest().getPwd());
        confirmpwdField.setText(current_gestionnaire.getGest().getPwd());
        actifToogleBtn.setSelected(current_gestionnaire.getGest().getActif());
        actifToogleBtn.setText(((current_gestionnaire.getGest().getActif())?"Activé":"Désactivé"));
        
        actifToogleBtn.selectedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    actifToogleBtn.setText("Activé");
                }else{
                    actifToogleBtn.setText("Désactivé");
                }
            }
            
        });
    }

    @FXML
    void onEdit(ActionEvent event) {

        if(nameField.getText().isEmpty() 
                || usernameField.getText().isEmpty()
                || pwdField.getText().isEmpty()
                || confirmpwdField.getText().isEmpty()){
            
            Res.not.showNotifications("Confirmation modification", 
                            "Tous les champs doivent être remplis", 
                            GlobalNotifications.ECHEC_NOT, 3, false);
            
        }else{
            if(pwdField.getText().equals(confirmpwdField.getText())){
                if(pwdField.getText().length()<=20){
                    try {
                        GestionnaireJpaController cont = new GestionnaireJpaController(Res.emf);

                        Gestionnaire gest=current_gestionnaire.getGest();
                        gest.setNomGest(nameField.getText().trim());
                        gest.setLogin(usernameField.getText().trim());
                        gest.setPwd(pwdField.getText());
                        gest.setActif(actifToogleBtn.isSelected());

                        cont.edit(gest);

                        /**Mise à jour de la table**/
                        current_gestionnaire.nameProperty().setValue(gest.getNomGest());
                        current_gestionnaire.usernameProperty().setValue(gest.getLogin());
                        current_gestionnaire.statusProperty().setValue((gest.getActif())?"Activé":"Désactivé");

                        getStage().close();

                        Res.not.showNotifications("Confirmation modification", 
                                "Les informations ont été modifiées avec succès", 
                                GlobalNotifications.SUCCESS_NOT, 2, false);
                    } catch (Exception e) {

                        Res.not.showNotifications("Confirmation modification", 
                                "L'application a généré une exception lors de la modification", 
                                GlobalNotifications.ECHEC_NOT, 2, false);
                    }
                }else{
                    Res.not.showNotifications("Confirmation Modification", 
                            "Les mots de passe ne doivent pas dépasser 20 caractères", 
                            GlobalNotifications.ECHEC_NOT, 2, false);
                }
            }else{
                
                Res.not.showNotifications("Confirmation modification", 
                            "Les mots de passe ne correspondent pas", 
                            GlobalNotifications.ECHEC_NOT, 2, false);
            }
        }
        
        
        

    }

    @FXML
    void onCancel(ActionEvent event) {
        getStage().close();
    }

    @Override
    public void init() {
        
    }

    @Override
    protected void setImagesToImageViews() {
       
    }

    
    
}
