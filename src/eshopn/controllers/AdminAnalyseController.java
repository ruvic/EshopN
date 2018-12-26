/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Facture;
import eshopn.entities.Lignefacture;
import eshopn.entities.controllers.FactureJpaController;
import eshopn.entities.controllers.LignefactureJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.MFact;
import eshopn.models.MFacture;
import eshopn.models.PrintAnalysesTask;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class AdminAnalyseController extends Controllers implements Initializable {

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
    private JFXDatePicker endDayPicker;

    @FXML
    private JFXDatePicker startDayPicker;
    
    
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
    private List<Facture> listFact = new ArrayList<>();
    
    private String recette;
    
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
    }    

    @FXML
    void search(MouseEvent event) {
        
    }

    @FXML
    void searchQte(MouseDragEvent event) {
    
    }
    
    @FXML
    void onPrint(ActionEvent event) throws IOException, DocumentException, FileNotFoundException, BadElementException, PrinterException {
        
        if(listFact.size() != 0){
            PrintAnalysesTask task=new PrintAnalysesTask(allFactures,
                startDayPicker.getEditor().getText(), 
                endDayPicker.getEditor().getText(), 
                calculeRecette(),  loaderImg);
            Thread t=new Thread(task);
            t.start();
        }else{
            Res.not.showNotifications("Echec", 
                "Rien à imprimer."
                , GlobalNotifications.ECHEC_NOT, 2, false);
        }
        
    }
    
    @FXML
    void onSearch(ActionEvent event) {
        String start = startDayPicker.getEditor().getText();
        String end = endDayPicker.getEditor().getText();
        if(!start.isEmpty() && !end.isEmpty()){
            //Clear table items 
            table.getItems().clear();
            allFactures.clear();
            
            Date startDate = stringToDate(start);
            Date endDate = stringToDate(end);
            if(endDate.compareTo(startDate) >= 0){
                
                new Thread(new Runnable() {
                    
                    @Override
                    public void run() {

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                loaderImg.setVisible(true);
                            }
                        });
                        
                        FactureJpaController contFact=new FactureJpaController(Res.emf);
                        List<Facture> listFactures=contFact.findFactureEntities(true);
                        listFact = filterFactures(listFactures, startDate, endDate);
                        
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

                                    showDatasOnTableView(allFactures, pagination, table, Res.itermPerPage);

                                } catch (Exception e) {

                                    Res.not.showNotifications("Echec", 
                                                "Impossible de se connecter au serveur."
                                                , GlobalNotifications.ECHEC_NOT, 2, false);

                                }


                            }
                        });
                    }
                }).start();
            }else{
                loaderImg.setVisible(false);
                Res.not.showNotifications("Echec", 
                            "La date de fin doit être supérieure à la date de départ."
                            , GlobalNotifications.ECHEC_NOT, 2, false);
            }
        }else{
            loaderImg.setVisible(false);
            Res.not.showNotifications("Echec", 
                        "Vous devez spécifiez les deux dates"
                        , GlobalNotifications.ECHEC_NOT, 2, false);
        }
        
    }
    
    private Date stringToDate(String dateString){
        Date date = null;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    private List<Facture> filterFactures(List<Facture> facturesList, Date start, Date end){
        List<Facture> resultList = new ArrayList<>();
        
        for (Facture facture : facturesList) {
            Date factDate = facture.getDateFac();
            String year = year(factDate);
            String month = month(factDate);
            String day = day(factDate);
            String stringDate = day+"/"+month+"/"+year;
            factDate = stringToDate(stringDate);
            
            if(start.compareTo(factDate) <= 0 && end.compareTo(factDate) >= 0){
                resultList.add(facture);
            }
        }
        return resultList;
        
    }

    @FXML
    void onHome(ActionEvent event) {
        getMain().showAdminAcceuilView();
    }
    
    @FXML
    void onRecette(ActionEvent event) {
        BooleanProperty prop=new SimpleBooleanProperty(false);
        stack.setVisible(true);
        
        //calcul de la recette
        String recette=calculeRecette();
        
        Res.not.showDialog(stack, "Recette totale"
                , "La recette totale est de : "+ recette, prop, false);
    }
    
    private String calculeRecette(){
        double recette = 0.0;
        for (Facture facture : listFact) {
            recette += facture.getMontant().doubleValue();
        }
        return Res.formatNumber(recette) +" Fcfa";
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

                showDatasOnTableView(allFactures, pagination, table, Res.itermPerPage);
            }
        });
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
        
        loaderImg.setVisible(false);
        
        
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
