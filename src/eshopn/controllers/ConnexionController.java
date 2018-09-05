/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Gestionnaire;
import eshopn.entities.Photo;
import eshopn.entities.controllers.GestionnaireJpaController;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.Res;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class ConnexionController extends Controllers implements Initializable {

    @FXML
    private AnchorPane greenAnchorPane;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXCheckBox adminCheckBox;
    
    @FXML
    private BorderPane mainPane;
    
    @FXML
    private Rectangle rectbg;
    
    @FXML
    private ImageView logoImg;
    
    public final double WIDTH=321;
    public final double HEIGHT=398;
    public final double STAGE_MIN_WIDTH=685;
    public final double STAGE_MIN_HEIGHT=529;
   
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        /** Définition du fond d'écran (Image du magasin) **/ 
        Image fond=new Image("connexion/fond.jpg");
        BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO
                                                  , BackgroundSize.AUTO
                                                  , false, false, true, true);
        
        mainPane.setBackground(new Background(new BackgroundImage(fond,
            BackgroundRepeat.SPACE,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            bSize)));
        
        /** Définition du Logo **/
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Res.config.getModeStockageImage()==1){

                    File file=new File(Res.config.getLogoConnexionLocal());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logoImg.setImage(new Image(file.toURI().toURL().toExternalForm()));
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(ConnexionController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                 }else{

                    /*try {
                        InputStream is = Res.config.getLogoConnexion().openStream();
                    
                        logoImg.setImage(new Image(is));


                        is.close();
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    
                    File file=new File(Res.config.getLogoConnexionLocal());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logoImg.setImage(new Image(file.toURI().toURL().toExternalForm()));
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(ConnexionController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                 }
            }
        }).start();
        
    }
    
    @Override
    public void init(){
       
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double width_val=(newValue.doubleValue()-WIDTH)/2;
                
                AnchorPane.setRightAnchor(greenAnchorPane, width_val);
                AnchorPane.setLeftAnchor(greenAnchorPane, width_val);
                rectbg.setWidth(newValue.doubleValue());
            }
            
        });
        
        getScene().heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double height_val=(newValue.doubleValue()-HEIGHT)/2;
                AnchorPane.setTopAnchor(greenAnchorPane, height_val);
                AnchorPane.setBottomAnchor(greenAnchorPane, height_val);
                rectbg.setHeight(newValue.doubleValue());
            }
            
        });
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
        
    }
    
    @FXML
    void onSignIn(ActionEvent event) {
        String username=usernameField.getText().trim();
        String pwd=passwordField.getText();
        
        if(checkConnectInformation(username, pwd)){
            
            if(adminCheckBox.isSelected()){
                getMain().showAdminAcceuilView();
            }else{
                getMain().showMagasinierAccueilView();
            }
            
        }
        
    }

    @Override
    protected void setImagesToImageViews() {
        
    }
    
    private boolean checkConnectInformation(String username, String pwd){
        if(adminCheckBox.isSelected()){
            return checkAdminInformation(username, pwd);
        }else{
            GestionnaireJpaController gestCont
                    =new GestionnaireJpaController(Res.emf);
            Gestionnaire gest=gestCont.findGestionnaire(username);
            Res.connected_storekeeper=gest;
            if (gest!=null) {
                if(gest.getPwd().equals(pwd)){
                    if(gest.getActif())return true;
                    else{
                        Res.not.showNotifications("Erreur de connexion"
                                , "Utilisateur non activé"
                                , GlobalNotifications.ECHEC_NOT, 2, false);
                        return false;
                    }
                }else{
                    Res.not.showNotifications("Erreur de connexion"
                                , "Nom d'utilisateur ou mot de passe incorrect"
                                , GlobalNotifications.ECHEC_NOT, 2, false);
                    return false;
                }
            } else {
                Res.not.showNotifications("Erreur de connexion"
                                , "Utilisateur inexistant"
                                , GlobalNotifications.ECHEC_NOT, 2, false);
                return false;
            }
        }
    }
    
    private boolean checkAdminInformation(String user_name,String admin_pwd){
        
        String user_waiting=Res.config.getAdmin();
        String pwd_waiting=Res.config.getAdminPwd();
        if(admin_pwd.equals(pwd_waiting) && user_name.equals(user_waiting)){
            return true;
        }else{
            Res.not.showNotifications("Erreur de connexion"
                                , "nom d'utilisateur ou mot de passe incorrect(s)."
                                , GlobalNotifications.ECHEC_NOT, 2, false);
            return false;
        }
        
    }
    
    
    
}
