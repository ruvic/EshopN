/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import eshopn.entities.Gestionnaire;
import eshopn.entities.controllers.GestionnaireJpaController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;


/**
 *
 * @author MBOGNI RUVIC
 */
public final class MGestionnaire {
    private StringProperty name;
    private StringProperty username;
    private StringProperty status;
    private ObjectProperty actions;
    
    private Gestionnaire gest;
    private GestionnaireJpaController cont;
    public static TableView<MGestionnaire> table;
    public static StackPane stack;
    public static final String  ACTIVE  ="Activé";
    public static final String DESACTIVE  ="Désactivé";
    private BooleanProperty isValidated;
    

    public MGestionnaire() {
        
    }

    public MGestionnaire(Gestionnaire g) {
        cont=new GestionnaireJpaController(Res.emf);
        this.gest=g;
        this.name = new SimpleStringProperty(g.getNomGest());
        this.username = new SimpleStringProperty(g.getLogin());
        this.status = new SimpleStringProperty((g.getActif())?ACTIVE:DESACTIVE);
        
        
        HBox box=new HBox(10);
        JFXButton editBtn=new JFXButton("Editer");
        JFXToggleButton toggle=new JFXToggleButton();
        ImageView editView=new ImageView(new Image("editer.png"));
        
        editView.setFitWidth(28);
        editView.setFitHeight(30);
        
        editBtn.setGraphic(editView);
        editBtn.setButtonType(JFXButton.ButtonType.RAISED);
        
        editBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        
        toggle.setText((g.getActif())?ACTIVE:DESACTIVE);
        toggle.setSelected((g.getActif())?true:false);
        
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 0, 10));
        box.getChildren().add(editBtn);
        box.getChildren().add(toggle);
         
        
       
        this.actions = new SimpleObjectProperty(box);
        
        MGestionnaire mGestionnaire=this;
        
        editBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getGest().setIdGest(cont.findGestionnaire(gest.getLogin()).getIdGest());
                (new eshopn.EShopN()).showEditGestionnaire(mGestionnaire, getGest().getTypeGest());
            }
        });
        
        toggle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getGest().setActif(!getGest().getActif());
                try {
                    cont.edit(getGest());
                    statusProperty().setValue((getGest().getActif())?ACTIVE:DESACTIVE);
                    toggle.setText((getGest().getActif())?ACTIVE:DESACTIVE);
                } catch (Exception e) {
                    Res.not.showNotifications("Echec", 
                            "Impossible de se connecter au serveur."
                            , GlobalNotifications.ECHEC_NOT, 2, false);
                }
            }
        });
        
        statusProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                toggle.setSelected(
                        (newValue.equals(ACTIVE))?true:false
                );
                toggle.setText((toggle.isSelected())?ACTIVE:DESACTIVE);
            }            
        });
    }
    
    public StringProperty nameProperty(){
        return  name;
    }
    
    public StringProperty usernameProperty(){
        return username;
    }
    
    public StringProperty statusProperty(){
        return status;
    }
    
    public ObjectProperty actionsProperty(){
        return actions;
    }

    public Gestionnaire getGest() {
        return gest;
    }

    public void setGest(Gestionnaire gest) {
        this.gest = gest;
    }
    
    private Integer getId(){
        try {
            if(getGest().getIdGest()==0) 
                return Integer.valueOf(cont.findGestionnaire(getGest().getLogin()).getIdGest());
            else
                return getGest().getIdGest();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
    
    
}
