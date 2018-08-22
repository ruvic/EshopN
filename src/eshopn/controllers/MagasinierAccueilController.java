/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;




/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class MagasinierAccueilController extends Controllers implements Initializable {

    @FXML
    private Rectangle rect;

    @FXML
    private ImageView fondImgView;

    @FXML
    private AnchorPane content;

    @FXML
    private HBox menuHBox;

    @FXML
    private JFXButton homeBtn;

    @FXML
    private JFXButton categorieBtn;
    
    @FXML
    private JFXButton produitsBtn;


    @FXML
    private JFXButton stocksBtn;

    @FXML
    private JFXButton aProposBtn;

    @FXML
    private Label profil;
    
    @FXML
    private StackPane stack;


    public AnchorPane getContent() {
        return content;
    }

    public void setContent(AnchorPane content) {
        this.content = content;
    }
    
    public final double WIDTH=589;
    public final double HEIGHT=275;
    public final double MENU_WIDTH_JFXBUTTON=131;
    public final double STAGE_MIN_WIDTH=820;
    public final double STAGE_MIN_HEIGHT=600;
   
    
    private FXMLLoader loader;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        Res.content=content;
        Res.stackPane=stack;
        profil.setText(Res.connected_storekeeper.getNomGest());
        
        if(!Res.connected_storekeeper.getTypeGest()){
            homeBtn.setText("Facturation");
            menuHBox.getChildren().remove(stocksBtn);
            stocksBtn.setPrefWidth(170);
            homeBtn.setPrefWidth(140);
        }
        
    }    
    
    @Override
    public void init(){
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double width_val=(newValue.doubleValue()-(131*4))/6;
                rect.setWidth(newValue.doubleValue());
                fondImgView.setFitWidth(newValue.doubleValue());
                menuHBox.setSpacing(width_val);
                
                if(newValue.doubleValue()<1050){
                    Res.NbreEltParLigne=3;
                }else{
                    Res.NbreEltParLigne=4;
                }
            }
            
        });
        
        getScene().heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rect.setHeight(newValue.doubleValue());
                fondImgView.setFitHeight(newValue.doubleValue());
                
                double new_val=newValue.doubleValue();
                System.out.println(new_val);
                
            }
            
        });
        
        if(Res.connected_storekeeper.getTypeGest()){
            getMain().replace("MagasinierDashboard.fxml", content);
        }else{
            loader=getMain().replace("Facturation.fxml",content);
            FacturationController cat = loader.getController();
            cat.setMain(this.getMain());
            cat.setContent(content);
            cat.setScene(getScene());
            cat.setStage(getStage());
            cat.init();
            
        }
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
        
    }
    
    @Override
    protected void setImagesToImageViews(){
        Image fond=new Image("fond.jpg");
        fondImgView.setImage(fond);
    };
    
    @FXML
    void onHome(ActionEvent event) {
        if(Res.connected_storekeeper.getTypeGest()){
            getMain().replace("MagasinierDashboard.fxml", content);
        }else{
            loader=getMain().replace("Facturation.fxml",content);
            FacturationController cat = loader.getController();
            cat.setMain(this.getMain());
            cat.setContent(content);
            cat.setScene(getScene());
            cat.setStage(getStage());
            cat.init();
        }
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

    @FXML
    void onShowCategories(ActionEvent event) {
        
        Res.not.showLoader(Res.stackPane);

        loader = getMain().replace("ListeCategories.fxml",content);
        ListeCategoriesController cat = loader.getController();
        cat.setMain(this.getMain());
        cat.setContent(content);
        cat.setScene(getScene());
        cat.setStage(getStage());
        cat.init();

        Res.stackPane.setVisible(false);

    }

    @FXML
    void onShowStocks(ActionEvent event) {
        
        if(Res.connected_storekeeper.getTypeGest()){
            loader = getMain().replace("Stocks.fxml", content);
            StocksController cat = loader.getController();
            cat.setMain(this.getMain());
            cat.setContent(content);
            cat.setScene(getScene());
            cat.setStage(getStage());
            cat.init();
        }
        
    }

    @FXML
    void onShowInfos(ActionEvent event) {

    }
    
    @FXML
    void onProduits(ActionEvent event) {
        loader=getMain().replace("ListeProduits.fxml", content);
        ListeProduitsController cat = loader.getController();
        cat.setMain(this.getMain());
        cat.setContent(content);
        cat.setScene(getScene());
        cat.setStage(getStage());
        cat.setCurrent_categorie(null);
        cat.init();
    }

    
    public void getContenu(){

    }
    
    
    private void setSelectedMenuItem(JFXButton btn){
        JFXButton buttons[]={homeBtn,categorieBtn,stocksBtn,aProposBtn};
        
        for (JFXButton button : buttons) {
            if(button.equals(btn)){
                btn.getStyleClass().addAll("onHoverbtn");
            }
            else{
                btn.getStyleClass().remove("onHoverbtn");
            }
                
        }
    }
            
   
}
