/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Gestionnaire;
import eshopn.entities.controllers.GestionnaireJpaController;
import eshopn.models.MGestionnaire;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import eshopn.models.GlobalNotifications;
import eshopn.models.Res;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class AdminEmployeesController extends Controllers implements Initializable {
    
    @FXML
    private Rectangle rect;

    @FXML
    private ImageView fondImgView;

    @FXML
    private ImageView arrowsImg;

    @FXML
    private ImageView arrowsSelected;
    
    @FXML
    private Pagination pagination;

    @FXML
    private TableColumn<MGestionnaire, String> usernameColumn;

    @FXML
    private TableColumn<MGestionnaire, String> nameColumn;

    @FXML
    private TableColumn<MGestionnaire, String> statusColumn;

    @FXML
    private TableColumn<MGestionnaire, HBox> ActionsColumn;
    
    @FXML
    private TableView<MGestionnaire> table;
    
    @FXML
    private JFXTextField searchField;
    
    @FXML
    private Label employeeLabel;

    @FXML
    private Label listEmployeeLabel;
    
    @FXML
    private StackPane stack;
   
    public final double WIDTH=589;
    public final double HEIGHT=275;
    public final double STAGE_MIN_WIDTH=900;
    public final double STAGE_MIN_HEIGHT=581;
    private final String MAGASINIER="Magasinier";
    private final String CAISSIER="Caissier";    
    public static boolean  isStoreKeeper;
    public static ObservableList<MGestionnaire> list_Gestionnaires;
    private ObservableList<MGestionnaire> allGest=FXCollections.observableArrayList();
    private ObservableList<MGestionnaire> genGest=FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        setImagesToImageViews();
        
        employeeLabel.setText((isStoreKeeper)?MAGASINIER:CAISSIER);
        listEmployeeLabel.setText(
                "Liste des " 
              + ((isStoreKeeper)?MAGASINIER:CAISSIER)
              + "s"
        );
        
        /** Initialisation de la table des employées**/
        usernameColumn.setCellValueFactory(cellData->cellData.getValue().usernameProperty());
        nameColumn.setCellValueFactory(cellData->cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData->cellData.getValue().statusProperty());
        ActionsColumn.setCellValueFactory(cellData->cellData.getValue().actionsProperty());
        
        
        
    }    

    @Override
    public void init() {
        
        try {
            
            list_Gestionnaires=getElements();
            table.setItems(list_Gestionnaires);

            genGest=list_Gestionnaires;
            allGest=list_Gestionnaires;
            showDatasOnTableView(list_Gestionnaires, pagination, table,Res.itermPerPage);


            /** Trie suivant la colonne des noms par ordre alphabétique **/
            table.getSortOrder().add(nameColumn);

            search();

            MGestionnaire.stack=stack;
            
        } catch (Exception e) {
            
            Res.not.showNotifications("Echec", 
                        "Impossible de se connecter au serveur."
                        , GlobalNotifications.ECHEC_NOT, 2, false);
            
        }
        
        
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rect.setWidth(newValue.doubleValue());
                fondImgView.setFitWidth(newValue.doubleValue());
            }
            
        });
        
        getScene().heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                rect.setHeight(newValue.doubleValue());
                fondImgView.setFitHeight(newValue.doubleValue());
                
                double new_val=newValue.doubleValue();
                System.out.println(new_val);
                if(new_val<=542) Res.itermPerPage=7;
                else if(new_val<=580) Res.itermPerPage=8;
                else if (new_val<=621) Res.itermPerPage=9;
                else if(new_val<=662) Res.itermPerPage=10;
                else Res.itermPerPage=11;
                
                showDatasOnTableView(genGest, pagination, table,Res.itermPerPage);
            }
            
        });
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
        
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
    void onAdd(ActionEvent event) {
        getMain().showAddGestionnaire(table,isStoreKeeper);
    }
    
     @FXML
    void onSearch(ActionEvent event) {

    }

    @FXML
    void onSearchEnter(KeyEvent event) {

    }
    
    @FXML
    void onHome(ActionEvent event) {
        getMain().showAdminAcceuilView();
    }
    
    @Override
    protected void setImagesToImageViews() {
        
        Image fond=new Image("fond.jpg");
        Image arrow=new Image("arrow.png");
        Image arrow_selected=new Image("arrow_selected.png");
        
        fondImgView.setImage(fond);
        arrowsImg.setImage(arrow);
        arrowsSelected.setImage(arrow_selected);
    }
    
    public ObservableList<MGestionnaire> getElements(){
        
        GestionnaireJpaController cont = new GestionnaireJpaController(Res.emf);
        ObservableList<Gestionnaire> liste1
                =FXCollections.observableList(cont.findGestionnaire(isStoreKeeper));
        ObservableList<MGestionnaire> liste2=FXCollections.observableArrayList();
        
        for (Gestionnaire g : liste1) {
            liste2.add(new MGestionnaire(g));
        }
        
        MGestionnaire.table=table;
        return liste2;
    }
    
    public void search(){
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<MGestionnaire> filteredData = new FilteredList<>(allGest, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mGest -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (mGest.getGest().getNomGest().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (mGest.getGest().getLogin().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
            
            // 3. Wrap the FilteredList in a SortedList.
            SortedList<MGestionnaire> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);
            
            genGest=observableFromSortedList(sortedData);
            showDatasOnTableView(allGest, pagination, table,Res.itermPerPage);
            
        });

        
    }
    
    private ObservableList<MGestionnaire> observableFromSortedList(SortedList<MGestionnaire> slist){
        ObservableList<MGestionnaire> mFactList=FXCollections.observableArrayList();
        for (MGestionnaire mFacture : slist) {
            mFactList.add(mFacture);
        }
        return mFactList;
    }
    
}
