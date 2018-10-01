/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Facture;
import eshopn.entities.Lignefacture;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import eshopn.entities.controllers.FactureJpaController;
import eshopn.entities.controllers.LignefactureJpaController;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.MFact;
import eshopn.models.MFacture;
import eshopn.models.PrintFacture;
import eshopn.models.Res;
import java.awt.Desktop;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class ListeFacturesController extends Controllers implements Initializable {

    @FXML
    private ImageView arrowsImg;
    @FXML
    private ImageView fondImgView;
    
    @FXML
    private JFXComboBox<String> jourBox;
    @FXML
    private ImageView arrowsSelected;
    
    @FXML
    private ImageView loaderImg;
    
    @FXML
    private JFXTextField numFactField;
    @FXML
    private TableView<MFacture> table;
    
    @FXML
    private StackPane stack;
    @FXML
    private TableColumn<MFacture, String> numFactColumn;
    @FXML
    private TableColumn<MFacture, String> montColumn;
    @FXML
    private TableColumn<MFacture, String> montNAP;
    @FXML
    private TableColumn<MFacture, String> dateColumn;
    @FXML
    private TableColumn<MFacture, String> nomCaissierColumn;
    
    
    @FXML
    private Rectangle rect;
    
    
    @FXML
    private JFXComboBox<String> moisBox;
    @FXML
    private JFXComboBox<String> anneeBox;
    
    @FXML
    private Pagination pagination;
    
    
    public final double WIDTH=589;
    public final double HEIGHT=275;
    public final double STAGE_MIN_WIDTH=744;
    public final double STAGE_MIN_HEIGHT=479;
    
    private AnchorPane content;
    private ArrayList<String> listeMois=new ArrayList<>();
    private ArrayList<String> listeAnee=new ArrayList<>();
    private ObservableList<MFacture> allFactures=FXCollections.observableArrayList();
    private ObservableList<MFacture> listGen=FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        loaderImg.setVisible(false);
        
        numFactColumn.setCellValueFactory(new PropertyValueFactory<>("numFact"));
        montColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        montNAP.setCellValueFactory(new PropertyValueFactory<>("montNAP"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        nomCaissierColumn.setCellValueFactory(new PropertyValueFactory<>("nomCaissier"));
        
        for (int i = 1; i < 13; i++) {
            if(i<10){
                listeMois.add("0"+i);
            }else{
                listeMois.add(i+"");
            }
        }
        
        for (int i = 2018; i < 2050; i++) {
            listeAnee.add(i+"");
        }
        
        anneeBox.getItems().add("");
        moisBox.getItems().add("");
        jourBox.getItems().add("");
        
        moisBox.getItems().addAll(listeMois);
        anneeBox.getItems().addAll(listeAnee);
        
    }    

    @FXML
    void search(MouseEvent event) {
    
    }

    @FXML
    void searchQte(MouseDragEvent event) {
    
    }
    
    @FXML
    void onChercherProduit(ActionEvent event) {
        getMain().showFactureForProduitview();
    }

    @FXML
    void onPrint(ActionEvent event) throws IOException, DocumentException, FileNotFoundException, BadElementException, PrinterException {
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                try {
                    LignefactureJpaController contLignFact=new LignefactureJpaController(Res.emf);
                    MFacture factSel=table.getSelectionModel().getSelectedItem();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            
                            if(factSel!=null){
                                loaderImg.setVisible(true);
                                PrintFacture printer=new PrintFacture();
                                Facture facture=factSel.getFacture();
                                ObservableList<MFact> listeFact=FXCollections.observableArrayList();
                                List<Lignefacture> liste1=contLignFact.findLignefactureEntities(facture);

                                for (Lignefacture lFact : liste1) {

                                    MFact mfact=new MFact(formatCode(""+lFact.getProduit().getCodePro())
                                            , lFact.getPrix().doubleValue()
                                            , (int)lFact.getQte()
                                            , lFact.getQte()*lFact.getPrix().doubleValue()
                                            , lFact.getProduit()
                                    );

                                    listeFact.add(mfact);
                                }

                                File file;
                                try {
                                    file = printer.print(
                                            facture, listeFact,
                                            facture.getMontant().doubleValue(),
                                            facture.getRemise().doubleValue(),
                                            ((facture.getTypeFac())?"Cash":"EMoney"),
                                            facture.getIdFac().toString()
                                    );
                                    Desktop.getDesktop().open(file);
                                    loaderImg.setVisible(false);
                                } catch (Exception ex) {
                                    loaderImg.setVisible(false);
                                    Logger.getLogger(ListeFacturesController.class.getName()).log(Level.SEVERE, null, ex);
                                } 
                            }else{
                                loaderImg.setVisible(false);
                                Res.not.showNotifications("Echec", 
                                            "Veuillez selectionner une facture."
                                            , GlobalNotifications.ECHEC_NOT, 2, false);
                            }

                    
                        }
                    });
                    
                } catch (Exception e) {
                    
                    loaderImg.setVisible(false);
                    Res.not.showNotifications("Echec", 
                                "Impossible de se connecter au serveur."
                                , GlobalNotifications.ECHEC_NOT, 2, false);

                }
            }
        }).start();
        
    }

    @FXML
    void onHome(ActionEvent event) {
        getMain().showAdminAcceuilView();
    }
    
    @FXML
    void onRecette(ActionEvent event) {
        
        BooleanProperty prop=new SimpleBooleanProperty(false);
        stack.setVisible(true);
        
        String annee="";
        String mois="";
        String jour="";
        
        if(anneeBox.getValue()!=null){
            annee=anneeBox.getValue().trim();
        }
        if(moisBox.getValue()!=null){
            mois=moisBox.getValue().trim();
        }
        
        if(jourBox.getValue()!=null){
            jour=jourBox.getValue().trim();
        }
        
        System.out.println("annee : "+annee+" ; mois : "+mois+" ; jour : "+jour);
        double recette=calculRecette(annee,mois,jour);
        
        Res.not.showDialog(stack, "Recette totale"
                , "La recette totale est de : "+Res.formatNumber(recette)+" Fcfa", prop, false);
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
    void onRefresh(ActionEvent event) {
        table.getItems().clear();
        allFactures = FXCollections.observableArrayList();
        listGen = FXCollections.observableArrayList();
        init();
    }
    
    @Override
    public void init() {
        
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
                rect.setHeight(newValue.doubleValue()-73);
                fondImgView.setFitHeight(newValue.doubleValue()-73);

                double new_val=newValue.doubleValue();

                if(new_val<=548) Res.itermPerPage=8;
                else if(new_val<=599) Res.itermPerPage=9;
                else if (new_val<=631) Res.itermPerPage=10;
                else if(new_val<=665) Res.itermPerPage=11;
                else Res.itermPerPage=12;

                showDatasOnTableView(listGen, pagination, table, Res.itermPerPage);
            }
        });
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
        
        loaderImg.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                FactureJpaController contFact=new FactureJpaController(Res.emf);
                List<Facture> listFact=contFact.findFactureEntities(true);
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        
                        try {
                            
                            for (Facture facture : listFact) {
                                MFacture fact=new MFacture(facture);
                                allFactures.add(fact);
                                table.getItems().add(fact);
                            }

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    loaderImg.setVisible(false);
                                }
                            });
                            
                            listGen=allFactures;
                            showDatasOnTableView(allFactures, pagination, table, Res.itermPerPage);

                            // 1. Wrap the ObservableList in a FilteredList (initially display all data).
                            FilteredList<MFacture> filteredData = new FilteredList<>(allFactures, p -> true);

                            // 2. Set the filter Predicate whenever the filter changes.
                            numFactField.textProperty().addListener((observable, oldValue, newValue) -> {
                                filteredData.setPredicate(mFact -> {
                                    // If filter text is empty, display all persons.
                                    if (newValue == null || newValue.isEmpty()) {

                                        return true;
                                    }

                                    // Compare first name and last name of every person with filter text.
                                    String lowerCaseFilter = newValue.toLowerCase();

                                    if (mFact.getNumFact().toLowerCase().contains(lowerCaseFilter)) {

                                        if(anneeBox.getValue()!=null){
                                            if(moisBox.getValue()!=null){
                                                if(jourBox.getValue()!=null){
                                                    if(day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())
                                                          && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                          && year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){
                                                        return true;
                                                    }else return false;
                                                }else{
                                                    if(month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                          && year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){

                                                        return true;
                                                    }else return false;
                                                }
                                            }else{

                                                if(jourBox.getValue()!=null){
                                                    if(day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())
                                                          && year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){

                                                        return true;
                                                    }else return false;
                                                }else{
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){

                                                        return true;
                                                    }else return false;
                                                }
                                            }
                                        }else{
                                            if(moisBox.getValue()!=null){
                                                if(jourBox.getValue()!=null){
                                                    if(month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){

                                                        return true;
                                                    }else return false;
                                                }else{
                                                    if(month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){

                                                        return true;
                                                    }else return false;
                                                }
                                            }else{
                                                if(jourBox.getValue()!=null){
                                                    if(day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){

                                                        return true;
                                                    }else return false;
                                                }else return true;
                                            }
                                        }
                                    } 
                                    return false; // Does not match.
                                });

                                // 3. Wrap the FilteredList in a SortedList.
                                SortedList<MFacture> sortedData = new SortedList<>(filteredData);

                                // 4. Bind the SortedList comparator to the TableView comparator.
                                sortedData.comparatorProperty().bind(table.comparatorProperty());

                                // 5. Add sorted (and filtered) data to the table.
                                table.setItems(sortedData);

                            });

                            moisBox.valueProperty().addListener((observable, oldValue, newValue) -> {

                                /** Reset Days  **/
                                jourBox.getItems().clear();

                                int year=0;
                                int month=0;

                                if(anneeBox.getValue()!=null && !anneeBox.getValue().isEmpty()){
                                    year=Integer.parseInt(anneeBox.getValue());
                                    if(moisBox.getValue()!=null && !moisBox.getValue().isEmpty()){
                                        month=Integer.parseInt(moisBox.getValue());
                                        resetJourBox(year, month);
                                    }
                                }else{
                                    year=Integer.parseInt(year(new Date()));
                                    if(moisBox.getValue()!=null){
                                        month=Integer.parseInt(moisBox.getValue());
                                        resetJourBox(year, month);
                                    }
                                }

                                filteredData.setPredicate(mFact -> {
                                    // If filter text is empty, display all persons.
                                    if (newValue == null || newValue.isEmpty()) {

                                        if(anneeBox.getValue()!=null){
                                            if(!numFactField.getText().trim().isEmpty()){
                                                if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                        && mFact.getNumFact().equals(numFactField.getText().trim())){
                                                    return true;
                                                }else{
                                                    return false;
                                                }   
                                            }else{
                                                if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){

                                                    return true;
                                                }else{

                                                    return false;
                                                }   
                                            }
                                        }else{

                                            return true;
                                        }

                                    }

                                    // Compare first name and last name of every person with filter text.
                                    String lowerCaseFilter = newValue.toLowerCase();

                                    if (month(mFact.getFacture().getDateFac()).contains(lowerCaseFilter)) {

                                        if(anneeBox.getValue()==null){

                                            if(jourBox.getValue()==null){
                                                if(numFactField.getText().isEmpty()){
                                                    return true;
                                                }else{
                                                    if(mFact.getNumFact().contains(numFactField.getText().trim())){

                                                        return true;
                                                    }else return  false;
                                                }
                                            }else{
                                                if(numFactField.getText().isEmpty()){
                                                    if(day(mFact.getFacture().getDateFac()).contains(jourBox.getValue())){
                                                         return true;
                                                    }else return false;
                                                }else{
                                                    if(mFact.getNumFact().contains(numFactField.getText().trim())
                                                            && day(mFact.getFacture().getDateFac()).contains(jourBox.getValue())){

                                                        return true;
                                                    }else return  false;
                                                }
                                            }

                                        }else{
                                            // on a l'année et le mois
                                            if(jourBox.getValue()==null){
                                                if(numFactField.getText().isEmpty()){
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){
                                                        return true;
                                                    }else return  false;
                                                }else{
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                            && mFact.getNumFact().contains(numFactField.getText().trim())){
                                                        return true;
                                                    }else return  false;
                                                }
                                            }else{
                                                //on a le jour, l'année et le mois                            
                                                if(numFactField.getText().isEmpty()){
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){
                                                        return true;
                                                    }else return  false;
                                                }else{
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())
                                                            && mFact.getNumFact().contains(numFactField.getText().trim())){
                                                        return true;
                                                    }else return  false;
                                                }
                                            }

                                        }
                                    } 
                                    return false; // Does not match.
                                });

                                /** Calcul de la recette **/
                                calculRecette(anneeBox.getValue(), moisBox.getValue(), jourBox.getValue());

                                // 3. Wrap the FilteredList in a SortedList.
                                SortedList<MFacture> sortedData = new SortedList<>(filteredData);

                                // 4. Bind the SortedList comparator to the TableView comparator.
                                sortedData.comparatorProperty().bind(table.comparatorProperty());

                                // 5. Add sorted (and filtered) data to the table.
                                table.setItems(sortedData);

                                listGen=observableFromSortedList(sortedData);
                                showDatasOnTableView(observableFromSortedList(sortedData), pagination, table,Res.itermPerPage);

                            });

                            anneeBox.valueProperty().addListener((observable, oldValue, newValue) -> {

                                /** Reset Days  **/
                                if(moisBox.getValue()==null || moisBox.getValue().isEmpty()){
                                    jourBox.getItems().clear();
                                }else{
                                    int year;
                                    try {
                                        year=Integer.parseInt(newValue.toLowerCase());
                                    } catch (NumberFormatException e) {
                                        year=Integer.parseInt(year(new Date()));
                                    }
                                    int month=Integer.parseInt(moisBox.getValue());
                                    resetJourBox(year, month);
                                }


                                filteredData.setPredicate((MFacture mFact) -> {
                                    // If filter text is empty, display all persons.
                                    if (newValue == null || newValue.isEmpty()) {

                                        if(moisBox.getValue()!=null && !moisBox.getValue().isEmpty()){

                                            if(jourBox.getValue()!=null && !jourBox.getValue().isEmpty()){

                                                if(!numFactField.getText().trim().isEmpty()){

                                                    if(year(mFact.getFacture().getDateFac()).equals(year(new Date()))
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())
                                                            && mFact.getNumFact().equals(numFactField.getText().trim())){

                                                        return true;
                                                    }
                                                }else{
                                                    if(year(mFact.getFacture().getDateFac()).equals(year(new Date()))
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){

                                                        return true;
                                                    }
                                                }
                                            }else{
                                                if(!numFactField.getText().isEmpty()){
                                                    if(year(mFact.getFacture().getDateFac()).equals(year(new Date()))
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && numFactField.getText().trim().equals(mFact.getNumFact())){

                                                        return true;
                                                    }
                                                }else{
                                                    if(year(mFact.getFacture().getDateFac()).equals(year(new Date()))
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){

                                                        return true;
                                                    }
                                                }
                                            }
                                        }else{
                                            if(jourBox.getValue()!=null){
                                                if(year(mFact.getFacture().getDateFac()).equals(year(new Date()))
                                                        && month(mFact.getFacture().getDateFac()).equals(new Date())
                                                        && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){

                                                    return true;
                                                }
                                            }else{
                                                return true;
                                            }

                                        }

                                        return false;
                                    }

                                    // Compare first name and last name of every person with filter text.
                                    String lowerCaseFilter = newValue.toLowerCase();

                                    if (year(mFact.getFacture().getDateFac()).contains(lowerCaseFilter)) {

                                        if(moisBox.getValue()==null){

                                            if(jourBox.getValue()==null){

                                                if(numFactField.getText().isEmpty()){

                                                    return true;
                                                }else {
                                                    if(mFact.getNumFact().contains(numFactField.getText().trim())){
                                                        return true;
                                                    }else return false;
                                                }
                                            }else{
                                                if(numFactField.getText().isEmpty()){
                                                    return true;
                                                }else {
                                                    if(mFact.getNumFact().contains(numFactField.getText().trim())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){
                                                        return true;
                                                    }else return false;
                                                }
                                            }
                                        }else{
                                            //on a le mois

                                            if(jourBox.getValue()==null){
                                                if(numFactField.getText().isEmpty()){
                                                    if(month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){
                                                        return true;
                                                    }else return false;
                                                }else {
                                                    if(mFact.getNumFact().contains(numFactField.getText().trim())
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){
                                                        return true;
                                                    }else return false;
                                                }
                                            }else{
                                                // on a le mois et le jour
                                                if(numFactField.getText().isEmpty()){
                                                    if(month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())){
                                                        return true;
                                                    }else return false;
                                                }else {
                                                    if(month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && day(mFact.getFacture().getDateFac()).equals(jourBox.getValue())
                                                            && mFact.getNumFact().contains(numFactField.getText().trim())){
                                                        return true;
                                                    }else return false;
                                                }
                                            }

                                        }

                                    } 

                                    return false; // Does not match.
                                });

                                /** Calcul de la recette **/
                                calculRecette(anneeBox.getValue(), moisBox.getValue(), jourBox.getValue());

                                // 3. Wrap the FilteredList in a SortedList.
                                SortedList<MFacture> sortedData = new SortedList<>(filteredData);

                                // 4. Bind the SortedList comparator to the TableView comparator.
                                sortedData.comparatorProperty().bind(table.comparatorProperty());

                                // 5. Add sorted (and filtered) data to the table.
                                table.setItems(sortedData);
                                listGen=observableFromSortedList(sortedData);
                                showDatasOnTableView(observableFromSortedList(sortedData), pagination, table,Res.itermPerPage);

                            });

                            jourBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                                filteredData.setPredicate(mFact -> {
                                    // If filter text is empty, display all persons.
                                    if (newValue == null || newValue.isEmpty()) {

                                        if(anneeBox.getValue()!=null){
                                            if(moisBox.getValue()!=null){
                                                if(!numFactField.getText().trim().isEmpty()){
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())
                                                            && numFactField.getText().trim().equals(mFact.getNumFact())){
                                                        return true;
                                                    }
                                                }else{
                                                    if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                            && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){
                                                        return true;
                                                    }
                                                }
                                            }else{
                                                if(anneeBox.getValue()!=null){
                                                    if(!numFactField.getText().trim().equals(mFact.getNumFact())){
                                                        if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                                && numFactField.getText().trim().equals(mFact.getNumFact())){
                                                            return true;
                                                        }
                                                    }else{
                                                        if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())){
                                                            return true;
                                                        }
                                                    }
                                                }else{
                                                    if(!numFactField.getText().trim().equals(mFact.getNumFact())){
                                                        if(numFactField.getText().trim().equals(mFact.getNumFact())){
                                                            return true;
                                                        }
                                                    }else{
                                                        return true;
                                                    }

                                                }
                                            }
                                        }

                                        return false;
                                    }

                                    // Compare first name and last name of every person with filter text.
                                    String lowerCaseFilter = newValue.toLowerCase();

                                    if (day(mFact.getFacture().getDateFac()).equals(lowerCaseFilter)) {                    

                                        if(anneeBox.getValue()!=null && !anneeBox.getValue().isEmpty()){
                                            if(moisBox.getValue()!=null && !moisBox.getValue().isEmpty()){
                                                if(year(mFact.getFacture().getDateFac()).equals(anneeBox.getValue())
                                                        && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){
                                                    return true;
                                                }
                                            }
                                        }else{
                                            if(moisBox.getValue()!=null && !moisBox.getValue().isEmpty()){
                                                if(year(new Date()).equals(year(mFact.getFacture().getDateFac()))
                                                        && month(mFact.getFacture().getDateFac()).equals(moisBox.getValue())){
                                                    return true;
                                                }
                                            }else{
                                                if(year(mFact.getFacture().getDateFac()).equals(year(new Date()))
                                                        && month(mFact.getFacture().getDateFac()).equals(month(new Date()) )){
                                                    return true;
                                                }
                                            }
                                        }
                                    } 

                                    return false; // Does not match.
                                });

                                /** Calcul de la recette **/
                                calculRecette(anneeBox.getValue(), moisBox.getValue(), jourBox.getValue());

                                // 3. Wrap the FilteredList in a SortedList.
                                SortedList<MFacture> sortedData = new SortedList<>(filteredData);

                                // 4. Bind the SortedList comparator to the TableView comparator.
                                sortedData.comparatorProperty().bind(table.comparatorProperty());

                                // 5. Add sorted (and filtered) data to the table.
                                table.setItems(sortedData);
                                listGen=observableFromSortedList(sortedData);
                                showDatasOnTableView(observableFromSortedList(sortedData), pagination, table,Res.itermPerPage);

                            });


                        } catch (Exception e) {

                            Res.not.showNotifications("Echec", 
                                        "Impossible de se connecter au serveur."
                                        , GlobalNotifications.ECHEC_NOT, 2, false);

                        }
                        
                        
                    }
                });
            }
        }).start();
        
        
        
    }

    public double calculRecette(String year, String month, String day){
        double recette=0.0;
        
        FactureJpaController cont=new FactureJpaController(Res.emf);
        List<Facture> listFact=cont.findFactureEntities();
        
        if(year!="" && month!="" && day!=""){
            for (Facture fact : listFact) {
                Date date=fact.getDateFac();
                if(year(date).equals(year)
                        && month(date).equals(month)
                        && day(date).equals(day)){
                    double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                    recette+=somme;
                }
            }
            return  recette;
        }
        
        if(year!="" && month!="" && day==""){
            for (Facture fact : listFact) {
                Date date=fact.getDateFac();
                if(year(date).equals(year)
                        && month(date).equals(month)){
                    double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                    recette+=somme;
                }
            }

            return recette;
        }
        
        if(year=="" && month!="" && day!=""){
            year=year(new Date());
            for (Facture fact : listFact) {
                Date date=fact.getDateFac();
                if(year(date).equals(year)
                        && month(date).equals(month)
                        && day(date).equals(day)){
                    double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                    recette+=somme;
                }
            }
            
            return recette;
        }
        
        if(year!="" && month=="" && day==""){
            
            for (Facture fact : listFact) {
                Date date=fact.getDateFac();
                if(year(date).equals(year)){
                    double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                    recette+=somme;
                }
            }
            
            return recette;
        }
        
        
        if(year=="" && month!="" && day==""){
            year=year(new Date());
            for (Facture fact : listFact) {
                Date date=fact.getDateFac();
                if(year(date).equals(year)
                        && month(date).equals(month)){
                    double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                    recette+=somme;
                }
            }
            
            return recette;
        }
        
        if(year=="" && month=="" && day!=""){
            year=year(new Date());
            month=month(new Date());
            for (Facture fact : listFact) {
                Date date=fact.getDateFac();
                if(year(date).equals(year)
                        && month(date).equals(month)
                        && day(date).equals(day)){
                    double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                    recette+=somme;
                }
            }
            return recette;
        }
        
        if(year=="" && month=="" && day==""){
            return recette;
        }
        
        return recette;
    }
    
    
    
    private void resetJourBox(int year, int month){
        int nbre=numberOfDaysForMonth(year, month);
        jourBox.getItems().clear();
        jourBox.getItems().add("");
        for (int i = 1; i <= nbre; i++) {
            if(i<10){
                jourBox.getItems().add("0"+i);
            }else{
                jourBox.getItems().add(""+i);
            }
        }
    }
    
    @Override
    protected void setImagesToImageViews() {
        Image arrow=new Image("arrow.png");
        Image arrow_selected=new Image("arrow_selected.png");
        Image fond=new Image("fond.jpg");
        
        arrowsImg.setImage(arrow);
        arrowsSelected.setImage(arrow_selected);
        fondImgView.setImage(fond);
        loaderImg.setImage(new Image("chargement2.gif"));
    }
    
    private ObservableList<MFacture> observableFromSortedList(SortedList<MFacture> slist){
        ObservableList<MFacture> mFactList=FXCollections.observableArrayList();
        for (MFacture mFacture : slist) {
            mFactList.add(mFacture);
        }
        return mFactList;
    }

    /**
     * @return the content
     */
    public AnchorPane getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(AnchorPane content) {
        this.content = content;
    }
    
}
