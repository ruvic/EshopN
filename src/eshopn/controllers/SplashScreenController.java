/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class SplashScreenController implements Initializable {

    @FXML
    private ImageView logo;

    @FXML
    private ImageView loader;
    
    @FXML
    private ImageView closeImg;
    
    private Stage stage;

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        logo.setImage(new Image("login2.png"));
        loader.setImage(new Image("chargement2.gif"));
        closeImg.setImage(new Image("incorrect.png"));
        
    }    
    
    @FXML
    void onClose(ActionEvent event) {
        getStage().close();
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
