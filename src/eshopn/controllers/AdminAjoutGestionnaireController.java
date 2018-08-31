/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import eshopn.entities.Gestionnaire;
import eshopn.entities.controllers.GestionnaireJpaController;
import eshopn.models.MGestionnaire;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import eshopn.models.GlobalNotifications;
import static eshopn.models.MGestionnaire.DESACTIVE;
import static eshopn.models.MGestionnaire.ACTIVE;
import eshopn.models.Res;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class AdminAjoutGestionnaireController extends Controllers implements Initializable {
    
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
    
    @FXML
    private StackPane stack;

    @FXML
    private ImageView loader;

    
    public static boolean isStorekeeper;
    private TableView<MGestionnaire> table; 
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String title="Ajouter ";
        title+=(isStorekeeper)?"Maganisier":"Caissier";
        titleLabel.setText(title);
        loader.setImage(new Image("chargement2.gif"));
        actifToogleBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                actifToogleBtn.setText((actifToogleBtn.isSelected())?ACTIVE:DESACTIVE);
            }
        });
    }
    
    @FXML
    void onCancel(ActionEvent event) {
        getStage().close();
    }
    
    @FXML
    void onAdd(ActionEvent event) {
        
        stack.setVisible(true);
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                if(nameField.getText().isEmpty() 
                        || usernameField.getText().isEmpty()
                        || pwdField.getText().isEmpty()
                        || confirmpwdField.getText().isEmpty()){

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stack.setVisible(false);
                            Res.not.showNotifications("Confirmation Ajout", 
                                    "Tous les champs doivent être remplies", 
                                    GlobalNotifications.ECHEC_NOT, 2, false);
                        }
                    });
                    
                }else{
                    
                    if(pwdField.getText().equals(confirmpwdField.getText())){

                        if(pwdField.getText().length()<=20){

                            GestionnaireJpaController cont
                                    =new GestionnaireJpaController(Res.emf);

                            Gestionnaire new_gest
                                    =new Gestionnaire(
                                            nameField.getText().trim(), 
                                            usernameField.getText().trim(), 
                                            pwdField.getText(), 
                                            actifToogleBtn.isSelected(), 
                                            AdminEmployeesController.isStoreKeeper 
                                    );

                            try {
                                
                                cont.create(new_gest);
                                AdminEmployeesController.list_Gestionnaires.add(new MGestionnaire(new_gest));

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isStorekeeper){
                                            getMain().showAdminEmployeeView(true);
                                        }else{
                                            getMain().showAdminEmployeeView(false);
                                        }

                                        getStage().close();
                                        
                                        stack.setVisible(false);
                                        Res.not.showNotifications("Confirmation Ajout", 
                                                    "Le nouvelle employé a été ajouté avec succès", 
                                                    GlobalNotifications.SUCCESS_NOT, 2, false);
                                    }
                                });
                            } catch (Exception e) {
                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        stack.setVisible(false);
                                        Res.not.showNotifications("Echec", 
                                            "Impossible de se connecter au serveur."
                                            , GlobalNotifications.ECHEC_NOT, 2, false);
                                    }
                                });
                                
                            }

                        }else{
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    stack.setVisible(false);
                                    Res.not.showNotifications("Confirmation Ajout", 
                                            "Les mots de passe ne doivent pas dépasser 20 caractères", 
                                            GlobalNotifications.ECHEC_NOT, 2, false);
                                }
                            });
                        }
                    }else{
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stack.setVisible(false);
                                Res.not.showNotifications("Confirmation Ajout", 
                                            "Les mots de passe ne correspondent pas", 
                                            GlobalNotifications.ECHEC_NOT, 2, false);
                            }
                        });
                    }
                }
            }
        }).start();
        
        
        
    }

    @Override
    public void init() {
       
    }

    @Override
    protected void setImagesToImageViews() {
        
    }

    public TableView<MGestionnaire> getTable() {
        return table;
    }

    public void setTable(TableView<MGestionnaire> table) {
        this.table = table;
    }
    
    
}
