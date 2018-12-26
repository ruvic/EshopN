/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import eshopn.models.Res;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class AdminAccueilController extends Controllers implements Initializable {
    

    @FXML
    private Rectangle rect;

    @FXML
    private ImageView fondImgView;

    @FXML
    private AnchorPane btnAnchorPane;

    @FXML
    private ImageView storeImg;

    @FXML
    private ImageView payImg;

    @FXML
    private ImageView arrowsImg;
    
    @FXML
    private ImageView factImg;
    
    @FXML
    private ImageView analyseImg;
    
    @FXML
    private StackPane stack;

    public final double WIDTH=852;
    public final double HEIGHT=275;
    public final double STAGE_MIN_WIDTH=744;
    public final double STAGE_MIN_HEIGHT=479;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
    }
    
    
    @FXML
    void onPaymaster(ActionEvent event) {
        getMain().showAdminEmployeeView(false);
    }

    @FXML
    void onStorekeeper(ActionEvent event) {
        getMain().showAdminEmployeeView(true);
    }
    
    @FXML
    void onFactures(ActionEvent event) {
        getMain().showListeFactures();
    }
    
    @FXML
    void onAnalyse(ActionEvent event) {
        getMain().showAnalyses();
    }
    
    @FXML
    void onLogOut(ActionEvent event) {
        BooleanProperty prop=new SimpleBooleanProperty(false);
        stack.setVisible(true);

        Res.not.showDialog(stack, "Confirmation de la déconnexion"
                , "Etes-vous sûr de vouloir vous déconnectez ?", prop, true);
        
        prop.addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue!=oldValue){
                    if(newValue){
                        getMain().ShowConnexionPage();
                    }
                }
            }

        });
        
    }
    
    
    @Override
    public void init(){
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double width_val=(newValue.doubleValue()-WIDTH)/2;
               
                AnchorPane.setLeftAnchor(btnAnchorPane, width_val);
                rect.setWidth(newValue.doubleValue());
                fondImgView.setFitWidth(newValue.doubleValue());
            }
            
        });
        
        getScene().heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double height_val=(newValue.doubleValue()-HEIGHT)/2.0;
                AnchorPane.setTopAnchor(btnAnchorPane, height_val);
                AnchorPane.setBottomAnchor(btnAnchorPane, height_val);
                rect.setHeight(newValue.doubleValue());
                fondImgView.setFitHeight(newValue.doubleValue());
            }
            
        });
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
    }
    
    @Override
    protected void setImagesToImageViews(){
        
        Image fond=new Image("fond.jpg");
        Image store=new Image("AdminAccueil/magasinier.png");
        Image pay=new Image("AdminAccueil/caissier.png");
        Image fact=new Image("AdminAccueil/facture.png");
        Image analyse=new Image("AdminAccueil/analyse.png");
        Image arrow=new Image("arrow.png");
        
        fondImgView.setImage(fond);
        storeImg.setImage(store);
        payImg.setImage(pay);
        factImg.setImage(fact);
        analyseImg.setImage(analyse);
        arrowsImg.setImage(arrow);
    }
    
}
