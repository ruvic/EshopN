/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Gestionstock;
import eshopn.entities.Photo;
import eshopn.entities.controllers.GestionstockJpaController;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.MFacture;
import eshopn.models.MStocks;
import eshopn.models.PDFStock;
import eshopn.models.Res;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class StocksController extends Controllers implements Initializable {

    @FXML
    private ImageView arrowsImg;
    @FXML
    private ImageView arrowsSelected;
    @FXML
    private TableView<MStocks> table;
    
    @FXML
    private ImageView loaderImg;

    @FXML
    private TableColumn<MStocks, String> nomColumn;

    @FXML
    private TableColumn<MStocks, String> codeProColumn;

    @FXML
    private TableColumn<MStocks, String> qteColumn;

    @FXML
    private TableColumn<MStocks, String> categorieColumn;

    @FXML
    private TableColumn<MStocks, String> ActionsColumn;

    @FXML
    private TableColumn<MStocks, String> gestColumn;
    
    @FXML
    private TableColumn<MStocks, String> dateColumn;
    
    @FXML
    private JFXComboBox<String> moisBox;
    
    @FXML
    private Pagination pagination;

    @FXML
    private JFXComboBox<String> anneeBox;
    
    
    @FXML
    private ImageView ImgView;
    
    @FXML
    private AnchorPane imgPane;
    
    @FXML
    private JFXTextField codeProField;
    
    private AnchorPane content;
    private ArrayList<String> listeMois=new ArrayList<>();
    private ArrayList<String> listeAnee=new ArrayList<>();
    private List<Gestionstock> listStocks;
    private ObservableList<MStocks> allStocks=FXCollections.observableArrayList();
    private ObservableList<MStocks> stocksGen=FXCollections.observableArrayList();
    

    public StocksController() {
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        loaderImg.setVisible(false);
        
        Res.itermPerPage=10;
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        codeProColumn.setCellValueFactory(new PropertyValueFactory<>("codeProduit"));
        gestColumn.setCellValueFactory(new PropertyValueFactory<>("gestionnaire"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        ActionsColumn.setCellValueFactory(new PropertyValueFactory<>("actions"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        
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
        
        moisBox.getItems().add("");
        anneeBox.getItems().add("");
        moisBox.getItems().addAll(listeMois);
        anneeBox.getItems().addAll(listeAnee);
        
        
        imgPane.setVisible(false);
        
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MStocks>(){
           @Override
           public void changed(ObservableValue<? extends MStocks> observable, MStocks oldValue, MStocks newValue) {
                
                if(newValue!=null && newValue!=oldValue){

                    Photo pht=(new ArrayList<>(newValue.getProduit().getPhotoCollection())).get(0);
                    
                    if(Res.config.getModeStockageImage()==1){
                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                
                                File file=new File(Res.config.getDossierImagesLocal()
                                        +newValue.getProduit().getCodePro().toString()
                                        +"/"+pht.getLienPhoto());
                               
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            ImgView.setImage(new Image(file.toURI().toURL().toExternalForm()));
                                        } catch (MalformedURLException ex) {
                                            Logger.getLogger(StocksController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });
                                
                            }
                        }).start();
                        
                        
                    }else{
                        
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                
                                try {
                                    URL url = new URL(lienAbsolueImage(pht));
                                    InputStream is = url.openStream();
                                    
                                    ImgView.setImage(new Image(is));
                                    
                                    is.close();
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                
                                
                            }
                        }).start();
                        
                        
                    }
                }
           }
            
        });
        
        
    }   
    
    @Override
    public void init() {
        
        Res.not.showLoader(Res.stackPane);
        BooleanProperty okProperty=new SimpleBooleanProperty(false);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    
                    GestionstockJpaController cont=new GestionstockJpaController(Res.emf);
                    listStocks=cont.findGestionstockEntities(true);
                    okProperty.setValue(true);
                } catch (Exception e) {
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Res.stackPane.setVisible(false);
                            Res.not.showNotifications("Echec", 
                                "Impossible de se connecter au serveur."
                                , GlobalNotifications.ECHEC_NOT, 2, false);
                        }
                    });
                    
                    
                }
            }
        }).start();
        
        okProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                
                
                if(newValue.booleanValue()){
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Res.stackPane.setVisible(false);
                            for (Gestionstock stock : listStocks) {
                                MStocks stocks=new MStocks(stock);
                                allStocks.add(new MStocks(stock));
                                table.getItems().add(stocks);
                            }

                            stocksGen=allStocks;
                            showDatasOnTableView(allStocks, pagination, table,Res.itermPerPage);
                        }
                    });
                    
                }  
            }
        });
        
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<MStocks> filteredData = new FilteredList<>(allStocks, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        codeProField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mStock -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {

                    if(anneeBox.getValue()==null){
                        if(moisBox.getValue()==null){
                            return true;
                        }else{
                            if(year(new Date()).equals(anneeBox.getValue())
                                    && mStock.getDate().split("-")[1].toLowerCase().equals(moisBox.getValue()) ){
                                System.out.println("passage");
                                return true;
                            }else return false;
                        }
                    }else{
                        if(moisBox.getValue()==null){
                            return true;
                        }else{
                            if(mStock.getDate().split("-")[0].equals(anneeBox.getValue())
                                    && mStock.getDate().split("-")[1].toLowerCase().equals(moisBox.getValue()) ){
                                return true;
                            }else return false;
                        }
                    }

                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (mStock.getCodeProduit().toLowerCase().contains(lowerCaseFilter)) {

                    if(anneeBox.getValue()!=null){
                        if(moisBox.getValue()!=null){
                            if(mStock.getDate().split("-")[1].toLowerCase().equals(moisBox.getValue())
                                  && mStock.getDate().split("-")[0].toLowerCase().equals(anneeBox.getValue())){
                                return true;
                            }else return false;
                        }else{
                            if(mStock.getDate().split("-")[1].toLowerCase().equals(anneeBox.getValue())){
                                return true;
                            }else return false;
                        }
                    }else{
                        if(moisBox.getValue()!=null){
                            if(mStock.getDate().split("-")[1].toLowerCase().equals(moisBox.getValue())
                                    && year(new Date()).equals(anneeBox.getValue())){
                                return true;
                            }else return false;
                        }else return true;
                    }
                } 
                return false; // Does not match.
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<MStocks> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);

            stocksGen=observableFromSortedList(sortedData);
            showDatasOnTableView(observableFromSortedList(sortedData), pagination, table,Res.itermPerPage);
        });

        moisBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mStock -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    if(anneeBox.getValue()==null){
                        if(codeProField.getText().isEmpty()){
                            return true;
                        }else{
                            if(mStock.getCodeProduit().contains(codeProField.getText().trim())){
                                return true;
                            }else{
                                return false;
                            }
                        }
                    }else{
                        if(codeProField.getText().isEmpty()){
                            if(mStock.getDate().split("-")[0].equals(anneeBox.getValue())){
                                return true;
                            }else return false;
                        }else{
                            if(mStock.getDate().split("-")[0].equals(anneeBox.getValue())
                                    && mStock.getCodeProduit().contains(codeProField.getText().trim())){
                                return true;
                            }else return false;
                        }
                    }
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (mStock.getDate().split("-")[1].toLowerCase().contains(lowerCaseFilter)) {

                    if(anneeBox.getValue()==null){
                        if(codeProField.getText().isEmpty()){
                            return true;
                        }else{
                            if(mStock.getCodeProduit().contains(codeProField.getText().trim())){
                                return true;
                            }else return  false;
                        }
                    }else{
                        if(codeProField.getText().isEmpty()){
                            if(mStock.getDate().split("-")[0].equals(anneeBox.getValue())){
                                return true;
                            }else return  false;
                        }else{
                            if(mStock.getDate().split("-")[0].equals(anneeBox.getValue())
                                    && mStock.getCodeProduit().contains(codeProField.getText().trim())){
                                return true;
                            }else return  false;
                        }
                    }
                } 
                return false; // Does not match.
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<MStocks> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);

            stocksGen=observableFromSortedList(sortedData);
            showDatasOnTableView(observableFromSortedList(sortedData), pagination, table,Res.itermPerPage);

        });

        anneeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mStock -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    if(moisBox.getValue()==null){
                        if(codeProField.getText().isEmpty()){
                            return true;
                        }else{
                            if(mStock.getCodeProduit().contains(codeProField.getText().trim())){
                                return true;
                            }else return false;
                        }
                    }else{
                        if(codeProField.getText().isEmpty()){
                            if(mStock.getDate().split("-")[1].equals(moisBox.getValue())
                                    && year(new Date()).equals(mStock.getDate().split("-")[0]))
                                return true;
                            return false;
                        }else{
                            if(mStock.getDate().split("-")[1].equals(moisBox.getValue())
                                    && year(new Date()).equals(anneeBox.getValue())
                                    && mStock.getCodeProduit().contains(codeProField.getText().trim()))
                                return true;
                            return false;
                        }
                    }
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (mStock.getDate().split("-")[0].toLowerCase().contains(lowerCaseFilter)) {

                    if(moisBox.getValue()==null){
                        if(codeProField.getText().isEmpty()){
                            return true;
                        }else {
                            if(mStock.getCodeProduit().contains(codeProField.getText().trim())){
                                return true;
                            }else return false;
                        }
                    }else{
                        if(codeProField.getText().isEmpty()){
                            if(mStock.getDate().split("-")[1].equals(moisBox.getValue())){
                                return true;
                            }else return false;
                        }else {
                            if(mStock.getCodeProduit().contains(codeProField.getText().trim())
                                    && mStock.getDate().split("-")[1].equals(moisBox.getValue())){
                                return true;
                            }else return false;
                        }
                    }

                } 
                return false; // Does not match.
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<MStocks> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);

            stocksGen=observableFromSortedList(sortedData);
            showDatasOnTableView(observableFromSortedList(sortedData), pagination, table,Res.itermPerPage);
        });
        
        
    }

    @FXML
    void onRefresh(ActionEvent event) {
        
        if(Res.connected_storekeeper.getTypeGest()){
            
            FXMLLoader loader = getMain().replace("Stocks.fxml", content);
            StocksController cat = loader.getController();
            cat.setMain(this.getMain());
            cat.setContent(content);
            cat.setScene(getScene());
            cat.setStage(getStage());
            cat.init();
        }
        
    }
    
    @Override
    protected void setImagesToImageViews() {
        Image arrow=new Image("arrow.png");
        Image arrow_selected=new Image("arrow_selected.png");
        
        loaderImg.setImage(new Image("chargement2.gif"));
        arrowsImg.setImage(arrow);
        arrowsSelected.setImage(arrow_selected);
        ImgView.setImage(new Image("Produits/default.png"));
        
    }
    
    
    
    
    @FXML
    void onControlPressed(KeyEvent event) {
        if(event.getCode()==KeyCode.SHIFT){
            imgPane.setMouseTransparent(false);
            imgPane.setVisible(true);
        }
    }
    
    @FXML
    void onControlReleased(KeyEvent event) {
        if(event.getCode()==KeyCode.SHIFT){
            imgPane.setMouseTransparent(true);
            imgPane.setVisible(false);
        }
    }
    
    @FXML
    void onHome(ActionEvent event) {
        if(Res.connected_storekeeper.getTypeGest()){
            getMain().replace("MagasinierDashboard.fxml", content);
        }else{
            FXMLLoader loader=getMain().replace("Facturation.fxml",content);
            FacturationController cat = loader.getController();
            cat.setMain(this.getMain());
            cat.setContent(content);
            cat.setScene(getScene());
            cat.setStage(getStage());
            cat.init();
        }
    }
    
    @FXML
    void search(MouseEvent event) {
        
    }

    @FXML
    void searchQte(MouseDragEvent event) {
    
    }

    @FXML
    void onPrint(ActionEvent event) {
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    
                    loaderImg.setVisible(true);
                    String annee=anneeBox.getValue();
                    String mois=moisBox.getValue();
                    String code=codeProField.getText().trim();

                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    File file=new File(Res.config.getDossierStocksPdf()+sdf.format((new GregorianCalendar()).getTime())+".pdf");
                    if(file != null){
                        PDFStock pdf = new PDFStock(file.getAbsolutePath().replace("\\", "/"));
                        //PhotoJpaController cont=new PhotoJpaController(Res.emf);
                        List<Gestionstock> listes=new ArrayList<>();

                        if(annee==null){
                            if(mois==null){
                                if(codeProField.getText().isEmpty()){
                                    listes=listStocks;
                                }else{
                                    for (Gestionstock stock : listStocks) {
                                        if(stock.getCodePro().getCodePro().toString().contains(code))
                                            listes.add(stock);
                                    }
                                }
                            }else {
                                if(codeProField.getText().isEmpty()){
                                    for (Gestionstock stock : listStocks) {

                                        if(Res.sdf.format(stock.getDateStock()).toString().split("-")[1].toLowerCase().equals(mois))
                                            listes.add(stock);
                                    }
                                }else{
                                    for (Gestionstock stock : listStocks) {
                                        if(stock.getCodePro().getCodePro().toString().contains(code)
                                                && Res.sdf.format(stock.getDateStock()).toString().split("-")[1].toLowerCase().equals(mois))
                                            listes.add(stock);
                                    }
                                }
                            }
                        }else{
                           if(mois==null){
                                if(codeProField.getText().isEmpty()){
                                    for (Gestionstock stock : listStocks) {
                                        if(Res.sdf.format(stock.getDateStock()).toString().split("-")[0].toLowerCase().equals(annee))
                                            listes.add(stock);
                                    }
                                }else{
                                    for (Gestionstock stock : listStocks) {
                                        if(Res.sdf.format(stock.getDateStock()).toString().split("-")[0].toLowerCase().equals(annee)
                                                && stock.getCodePro().getCodePro().toString().contains(code))
                                            listes.add(stock);
                                    }
                                }
                            }else {
                                if(codeProField.getText().isEmpty()){
                                    for (Gestionstock stock : listStocks) {
                                        if(Res.sdf.format(stock.getDateStock()).toString().split("-")[1].toLowerCase().equals(mois)
                                                && Res.sdf.format(stock.getDateStock()).toString().split("-")[0].toLowerCase().equals(annee))
                                            listes.add(stock);
                                    }
                                }else{
                                    for (Gestionstock stock : listStocks) {
                                        if(stock.getCodePro().getCodePro().toString().contains(code)
                                                && Res.sdf.format(stock.getDateStock()).toString().split("-")[1].toLowerCase().equals(mois)
                                                && Res.sdf.format(stock.getDateStock()).toString().split("-")[0].toLowerCase().equals(annee))
                                            listes.add(stock);
                                    }
                                }
                            } 
                        }

                        for (Gestionstock stock : listes) {
                            pdf.addStockRow(stock.getCodePro().getNomPro(), 
                                        formatCode(stock.getCodePro().getCodePro().toString()), 
                                        stock.getQte(), 
                                        stock.getDateStock(), 
                                        stock.getOperation(), 
                                        lienAbsolueImage((new ArrayList<>(stock.getCodePro().getPhotoCollection())).get(0)));
                        }

                        Desktop.getDesktop().open(file);
                        pdf.save();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loaderImg.setVisible(false);        
                    }
                });
                
            }
        }).start();
    }
    
    private ObservableList<MStocks> observableFromSortedList(SortedList<MStocks> slist){
        ObservableList<MStocks> mFactList=FXCollections.observableArrayList();
        for (MStocks mStocks : slist) {
            mFactList.add(mStocks);
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
