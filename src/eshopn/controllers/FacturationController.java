/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPrintPage;
import eshopn.entities.Facture;
import eshopn.entities.Lignefacture;
import eshopn.entities.LignefacturePK;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import eshopn.entities.controllers.CategorieJpaController;
import eshopn.entities.controllers.FactureJpaController;
import eshopn.entities.controllers.LignefactureJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.MFact;
import eshopn.models.PrintFacture;
import eshopn.models.Res;
import eshopn.models.Tasks;
import java.awt.Desktop;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class FacturationController extends Controllers implements Initializable {

    @FXML
    private JFXTextField remiseField;

    @FXML
    private JFXToggleButton typePaiementToggle;
    
    @FXML
    private ImageView loader;
    
    @FXML
    private ScrollPane ImagesPane;

    @FXML
    private JFXTextField qteField;

    @FXML
    private Label qteStock;

    @FXML
    private TableView<MFact> table;

    @FXML
    private TableColumn<MFact, String> codeProColumn;

    @FXML
    private TableColumn<MFact, String> prixUnitColumn;

    @FXML
    private TableColumn<MFact, String> qteColumn;

    @FXML
    private TableColumn<MFact, String> sousTotalColumn;

    @FXML
    private Label totalField;

    @FXML
    private Label netAPayerField;
    
    @FXML
    private ImageView imgView;

    @FXML
    private JFXTextField telField;
    
    @FXML
    private JFXTextField codeProField;
    
    @FXML
    private Label resteApayerLabel;
    
    @FXML
    private AnchorPane imgPane;

    @FXML
    private JFXTextField montantRemisField;
    
    private List<Produit> listesPro;
    
    private AnchorPane content;
    private DoubleProperty totalProperty;
    private DoubleProperty netAPayerProperty;
    private IntegerProperty qteStockProperty;
    private HashMap<String, Integer> stocks;
    private Facture facture;
   
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        loader.setVisible(false);
        typePaiementToggle.selectedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue.booleanValue()){
                    typePaiementToggle.setText("EMoney");
                }else{
                    typePaiementToggle.setText("Cash");
                }
            }
            
        });
        
        remiseField.setPromptText("Remise Max="+Res.config.getRemise());
        
        codeProColumn.setCellValueFactory(new PropertyValueFactory<>("codeProduit"));
        prixUnitColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));
        sousTotalColumn.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));
        
        totalProperty=new SimpleDoubleProperty();
        netAPayerProperty=new SimpleDoubleProperty();
        qteStockProperty=new SimpleIntegerProperty();
        
        totalProperty.addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                totalField.setText(Res.formatNumber(newValue.doubleValue()));
                netAPayerProperty.setValue(totalProperty.getValue()*(1-(Res.config.getRemise()/100)));
            }
            
        });
        
        netAPayerProperty.addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double remise;
                try{
                    remise=Double.parseDouble(remiseField.getText().trim())/100;
                }catch(NumberFormatException e){
                    remise=0.0;
                }
                
                if(remise<=(Res.config.getRemise()/100.0)){
                    netAPayerField.setText(Res.formatNumber(totalProperty.getValue()*(1-remise)));
                }else{
                    netAPayerField.setText("0.0");
                    Res.not.showNotifications("Erreur d'Ajout à la facture"
                    , "La remise ne peut dépasser "+Res.config.getRemise()+"% .",
                    GlobalNotifications.ECHEC_NOT, 3, false);
                }
                
            }
        });
        
        qteStockProperty.addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                qteStock.setText(newValue.toString());
            }
        });
        
        montantRemisField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    
                if(!newValue.isEmpty()){
                    try{
                        double mtRmis=Double.parseDouble(newValue.trim().replace(" ", ""));
                        double nap=Double.parseDouble(netAPayerField.getText().trim().replace(" ", ""));
                        resteApayerLabel.setText((mtRmis-nap)+"");
                    }catch(NumberFormatException e){
                        Res.not.showNotifications("Erreur sur le montant remis"
                                , "Le montant remis est invalide.",
                                GlobalNotifications.ECHEC_NOT, 3, false);
                    }
                }
            }
            
        });
        
        imgPane.setVisible(false);
        
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MFact>(){
           @Override
           public void changed(ObservableValue<? extends MFact> observable, MFact oldValue, MFact newValue) {
                if(newValue!=null && newValue!=oldValue){
//                    PhotoJpaController cont=new PhotoJpaController(Res.emf);
                    Photo pht=(new ArrayList<>(newValue.getProduit().getPhotoCollection())).get(0);
                    try {

                        URL url = new URL(lienAbsolueImage(pht));
                        InputStream is = url.openStream();
                        imgView.setImage(new Image(is));
                        is.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ProduitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
           }   
        });
                
    }    

    @FXML
    void onAdd(ActionEvent event) {
        
        String codePro=codeProField.getText().trim();
        ProduitJpaController cont=new ProduitJpaController(Res.emf);
        Produit pr;
        try {
            pr=cont.findProduit(Integer.valueOf(codePro.replace("-", "")));
        } catch (Exception e) {
            pr=null;
        }
        
        if(pr!=null){
                        
            int qteStock=pr.getQte();
            double prixUnitaire=pr.getPrix().doubleValue();
            int qte;
            try {
                qte=Integer.parseInt(qteField.getText().trim());
            } catch (NumberFormatException e) {
                qte=1;

            }

            if(qte>0 && qte<=qteStock){
                double sousTotal=qte*prixUnitaire;
                totalProperty.setValue(totalProperty.getValue()+sousTotal);
                stocks.replace(pr.getCodePro()+"", stocks.get(pr.getCodePro()+"")-qte);

                boolean flag=false;
                for (MFact item : table.getItems()) {
                    if(item.getCodeProduit().equals(codePro)){
                        qte+=item.getQte();
                        item.setQte(qte);
                        item.setSousTotal(item.getQte()*prixUnitaire);
                        qteStockProperty.setValue(pr.getQte()-qte);
                        flag=true;
                        table.refresh();
                        break;
                    }
                }

                if(!flag){
                    MFact mf=new MFact(codePro, prixUnitaire, qte, sousTotal, pr);
                    qteStockProperty.setValue(pr.getQte()-qte);
                    table.getItems().add(mf);
                }

                netAPayerProperty.setValue(0);
            }else{
                if(qte>0){
                    Res.not.showNotifications("Erreur d'Ajout à la facture"
                        , "La quantité à vendre doit être inférieure à celle en stock.",
                        GlobalNotifications.ECHEC_NOT, 3, false);
                }else{
                    Res.not.showNotifications("Erreur d'Ajout à la facture"
                        , "La quantité doit être supérieure à zero.",
                        GlobalNotifications.ECHEC_NOT, 3, false);
                }
            }
        }else{
            Res.not.showNotifications("Erreur d'Ajout à la facture"
                        , "Aucun produit existant sous ce code.",
                        GlobalNotifications.ECHEC_NOT, 3, false);
        }
        
                
    }
    
    
    
    @FXML
    void onDelete(ActionEvent event) {
        MFact factTodel=table.getSelectionModel().getSelectedItem();
        totalProperty.setValue(totalProperty.getValue()-factTodel.getSousTotal());
        String codePro=factTodel.getCodeProduit().replace("-", "");
        stocks.replace(codePro, stocks.get(codePro)+factTodel.getQte());
        
        if(codeProField.getText().equals(factTodel.getCodeProduit())){
            qteStock.setText(stocks.get(codePro)+"");
        }
        
        table.getItems().remove(factTodel);
        table.refresh();
    }
    
    @FXML
    void onCatalogue(ActionEvent event) throws DocumentException, BadElementException, IOException, IOException {
        
        Tasks task=new Tasks(getMain(), loader);
        Thread t=new Thread(task);
        t.start();
        
    }

    @FXML
    void onCancel(ActionEvent event) {
        clear();
    }
    
    private void clear(){
        telField.clear();
        codeProField.clear();
        remiseField.clear();
        typePaiementToggle.setSelected(false);
        qteField.setText("1");
        qteStock.setText("ND");
        totalField.setText("0.0");
        netAPayerField.setText("0.0");
        montantRemisField.clear();
        resteApayerLabel.setText("0.0");
        
        if(((HBox)ImagesPane.getContent())!=null)
            ((HBox)ImagesPane.getContent()).getChildren().clear();
        
        table.getItems().clear();
        init();
    }

    @FXML
    void onView(ActionEvent event) throws DocumentException, BadElementException, IOException, FileNotFoundException, PrinterException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisible(true);
                        System.out.println(loader.isVisible());
                        File file=null;
                        try {
                            file = print(true, null);
                        } catch (DocumentException ex) {
                            Logger.getLogger(FacturationController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(FacturationController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (PrinterException ex) {
                            Logger.getLogger(FacturationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if(file!=null)
                            try {
                                Desktop.getDesktop().open(file);
                        } catch (IOException ex) {
                            Logger.getLogger(FacturationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        loader.setVisible(false);
                    }
                });
            }
        }).start();
    }
    
    
    private File print(boolean isPreview, Integer id) throws DocumentException, BadElementException, BadElementException, IOException, FileNotFoundException, PrinterException{
        if(table.getItems().size()!=0){
            
            if(telField.getText().trim().isEmpty()){
                Res.not.showNotifications("Erreur d'Ajout à la facture"
                            , "Veuillez entrer le numéro de téléphone du client.",
                            GlobalNotifications.ECHEC_NOT, 3, false);
            }else{

                BigDecimal remise;
                String tel;
                
                if(remiseField.getText().trim().isEmpty()) 
                    remise=BigDecimal.valueOf(0);
                else {
                    try {
                        remise=BigDecimal.valueOf(Double.parseDouble(remiseField.getText().trim()));
                    } catch (NumberFormatException e) {
                        remise=BigDecimal.valueOf(0);
                    }
                }
                
                boolean error=false;
                tel=telField.getText().trim();
                if(isNumberPhone(tel)){
                    if(telField.getText().trim().length()<=15){
                        
                        facture=new Facture((new GregorianCalendar()).getTime(), 
                            remise, 
                            BigDecimal.valueOf(Double.parseDouble(totalField.getText().replace(" ", "").trim())),
                            tel, typePaiementToggle.isSelected(),
                            Res.connected_storekeeper);

                        PrintFacture printer=new PrintFacture();

                        return printer.print(
                                facture, table.getItems(),
                                Double.valueOf(totalField.getText().replace(" ", "").trim()).doubleValue(),
                                remise.doubleValue(),
                                typePaiementToggle.getText(),
                                ((isPreview)?"xxx":id.toString())
                        );
                        
                    }else{
                        Res.not.showNotifications("Erreur d'Ajout à la facture"
                        , "Le numéro de téléphone doit avoir au plus 15 chiffres",
                        GlobalNotifications.ECHEC_NOT, 3, false);
                    }
                }else{
                    Res.not.showNotifications("Erreur d'Ajout à la facture"
                        , "Le numéro de téléphone est incorrect",
                        GlobalNotifications.ECHEC_NOT, 3, false);
                }
                    
            }   
        }else{
            Res.not.showNotifications("Facture vide"
                        , "Aucun contenu à imprimer, veuillez remplir la facture.",
                        GlobalNotifications.ECHEC_NOT, 3, false);
        }
        
        return null;
    }

    @FXML
    void onValider(ActionEvent event) throws DocumentException, BadElementException, IOException, Exception {
        
        loader.setVisible(true);

        try {
        
            FactureJpaController contFact=new FactureJpaController(Res.emf);
            LignefactureJpaController contListFact=new LignefactureJpaController(Res.emf);
            ProduitJpaController contPro=new ProduitJpaController(Res.emf);

            /** Insertion de la facture dans la base de donnée **/
            File file=print(true, null);

            contFact.create(facture);
            Facture facture2=contFact.findFacture(facture.getDateFac());


            /** Insertion des données dans la table ligneFacture **/
            for (MFact item : table.getItems()) {

                Integer codePro=Integer.parseInt(item.getCodeProduit().replace("-", ""));

                LignefacturePK pk=new LignefacturePK(
                        codePro, 
                        facture2.getIdFac().intValue()
                );

                /** Mise à jour de la quantité du produit courant dans la BD **/
                Produit pr=contPro.findProduit(codePro);
                pr.setQte(pr.getQte()-item.getQte());
                contPro.edit(pr);

                Lignefacture lign=new Lignefacture(
                        pk, BigDecimal.valueOf(item.getPrixUnitaire()), 
                        (short)item.getQte(), pr,  
                        facture2
                );

                contListFact.create(lign);

            }

            file.delete();

            File file1=print(false, facture2.getIdFac());

            /** Remise des champs à jour **/
            clear();

            prinTicketPDF(file1.getAbsolutePath(), file1.getName().substring(0, file1.getName().indexOf(".pdf")));
            
        } catch (Exception e) {
            Res.not.showNotifications("Echec", 
                        "Impossible de se connecter au serveur."
                        , GlobalNotifications.ECHEC_NOT, 2, false);
        }
        
        loader.setVisible(false);

    }
    
     @FXML
    void onRecette(ActionEvent event) {
        double recette=0.0;
        
         try {
            
            FactureJpaController cont=new FactureJpaController(Res.emf);
            List<Facture> listFact=cont.findFactureEntities();

            Date today=new Date();


            for (Facture fact : listFact) {
                if(fact.getIdCaissiere().equals(Res.connected_storekeeper)){
                    Date date=fact.getDateFac();

                    if(year(date).equals(year(today))
                            && month(date).equals(month(today))
                            && day(date).equals(day(today))){

                        double somme=(1-(fact.getRemise().doubleValue()/100))*fact.getMontant().doubleValue();
                        recette+=somme;
                    }
                }

            }
            
            recette=(recette/Res.config.getMinoration());

            Res.stackPane.setVisible(true);

            Res.not.showDialog(Res.stackPane, "Recette Journalière"
                    , "Votre recette journalière est de :   "+Res.formatNumber(round(recette, 2))+" Fcfa", null,false);
         } catch (Exception e) {
            Res.not.showNotifications("Echec", 
                       "Impossible de se connecter au serveur."
                       , GlobalNotifications.ECHEC_NOT, 2, false);
         }
    }
    
    

    @Override
    public void init() {
        
        ProduitJpaController cont=new ProduitJpaController(Res.emf);
        listesPro=cont.findProduitEntities();
        HBox imagesBox=new HBox(10);
        stocks=new HashMap<>();
        
        for (Produit produit : listesPro) {
            stocks.put(produit.getCodePro()+"", Integer.valueOf(produit.getQte()));
        }
        
        codeProField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
                if(newValue.trim().length()==7){
                    
                    for (Produit produit : listesPro) {
                        
                        if(produit.getCodePro().toString().equals(newValue.replace("-", ""))){
                            
                            try{
                                
                                ArrayList<Photo> listPhotos=(new ArrayList<>(produit.getPhotoCollection()));
                            
                                qteStockProperty.setValue(stocks.get(produit.getCodePro()+""));

                                try {
                                    ((HBox)ImagesPane.getContent()).getChildren().clear();
                                } catch (Exception e) {}

                                for (int i = 0; i < listPhotos.size(); i++) {
                                    Photo pht=listPhotos.get(i);
                                    try {

                                        /*URL url = new URL(lienAbsolueImage(pht));
                                        InputStream is = url.openStream();
                                        ImageView img = new ImageView(new Image(is));*/
                                        
                                        ImageView img = new ImageView();
                                        loadImage(produit.getCodePro().toString(), pht.getLienPhoto(), img);
                                        
                                        img.setFitWidth(200);
                                        img.setFitHeight(120);

                                        imagesBox.getChildren().add(img);
                                        
                                        //is.close();
                                    } catch (MalformedURLException ex) {
                                        Logger.getLogger(FacturationController.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IOException ex) {
                                        Logger.getLogger(FacturationController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                                ImagesPane.setContent(imagesBox);
                                break;
                            }catch(Exception e){
                                
                                Res.not.showNotifications("Echec", 
                                    "Impossible de se connecter au serveur."
                                    , GlobalNotifications.ECHEC_NOT, 2, false);
                                
                            }
                        }
                    }
                    
                }else{
                    qteStockProperty.setValue(0);
                    qteField.setText("1");
                    if(((HBox)ImagesPane.getContent())!=null)
                        ((HBox)ImagesPane.getContent()).getChildren().clear();
                }
            }
            
        });
        
        remiseField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                double remise;
                try{
                    remise=Double.parseDouble(newValue.toString())/100;
                }catch(NumberFormatException e){
                    remise=0.0;
                }
                
                if(remise<=(Res.config.getRemise()/100.0)){
                    //netAPayerField.setText(""+totalProperty.getValue()*(1-remise));
                    netAPayerField.setText(Res.formatNumber(totalProperty.getValue()*(1-remise)));
                }else{
                    netAPayerField.setText("0.0");
                    Res.not.showNotifications("Erreur d'Ajout à la facture"
                    , "La remise ne peut dépasser "+Res.config.getRemise()+"% .",
                    GlobalNotifications.ECHEC_NOT, 3, false);
                }
                
                
            }
            
        });
        
    }
    
    
    private void prinTicketPDF(String filePath, String jobName)

                throws IOException, PrinterException {

        FileInputStream fileInputStream = new FileInputStream(filePath);

        byte[] pdfContent = new byte[fileInputStream.available()];

        fileInputStream.read(pdfContent, 0, fileInputStream.available());

        ByteBuffer buffer = ByteBuffer.wrap(pdfContent);

        final PDFFile pdfFile = new PDFFile(buffer);

        PDFPrintPage pages = new PDFPrintPage(pdfFile);


        PrinterJob printJob = PrinterJob.getPrinterJob();

        PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();

        printJob.setJobName(jobName);


        Book book = new Book();

        book.append(pages, pageFormat, pdfFile.getNumPages());

        printJob.setPageable(book);

        Paper paper = new Paper();

        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());

        pageFormat.setPaper(paper);
        PrintService Nservices = PrintServiceLookup.lookupDefaultPrintService();
        String name = Nservices.getName();
      //  printJob.setPrintService(Nservices);
        PrintService[] printServices = PrinterJob.lookupPrintServices();

        for (int count = 0; count < printServices.length; ++count) {

              if (name.equalsIgnoreCase(printServices[count].getName())) {

                    printJob.setPrintService(printServices[count]);

                    break;

              } 

        }  

        printJob.print();

    }
    
    public boolean isNumberPhone(String tel){
        String digits="0123456789";
        if(tel.length()<=15){
            for (int i = 0; i < tel.length(); i++) {
                if(digits.indexOf(tel.charAt(i))<0){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void setImagesToImageViews() {
        loader.setImage(new Image("chargement2.gif"));
    }
    
    @FXML
    void onControlPressed(KeyEvent event) {
        
        if(event.getCode()==KeyCode.SHIFT){
            //imgPane.setMouseTransparent(false);
            imgPane.setVisible(true);
        }
    }
    
    @FXML
    void onControlReleased(KeyEvent event) {
        if(event.getCode()==KeyCode.SHIFT){
            
            //imgPane.setMouseTransparent(true);
            imgPane.setVisible(false);
        }
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
