/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTextArea;
import eshopn.entities.Categorie;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.models.GlobalNotifications;
import eshopn.models.HTTPRequest;
import eshopn.models.Res;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class NouveauProduitController extends Controllers implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private JFXTextField nomProduitField;

    @FXML
    private JFXTextField prixUnitField;
    
    @FXML
    private JFXTextField codeFourfield;
    
    
    @FXML
    private ScrollPane pane;
    
    @FXML
    private HBox imgViewBox;

    @FXML
    private JFXTextArea descriptField;
    
    private Categorie categorie;
    
    private HBox imgsBox;
    private ListeProduitsController listProdCont;
    private ArrayList<String> photos = new ArrayList<>();
    private ArrayList<File> photosFile = new ArrayList<>();
    private ArrayList<File> sourceFiles = new ArrayList<>();
    private ArrayList<String> photosTargetFolder = new ArrayList<>();
    private ArrayList<Integer> aRetirer = new ArrayList<>();
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgsBox=new HBox();
        pane.setContent(imgsBox);
    }    

    @FXML
    void onAdd(ActionEvent event) throws Exception {
        
        if(nomProduitField.getText().trim().isEmpty() 
             || prixUnitField.getText().trim().isEmpty()
             || codeFourfield.getText().trim().isEmpty()
             || descriptField.getText().trim().isEmpty()
             || ((HBox)(pane.getContent())).getChildren().size()==0){
            
            Res.not.showNotifications("Echec de l'ajout", 
                    "Toutes les champs doivent être remplis, \n"
                            + "et le produit doit avoir au moins une image"
                    , GlobalNotifications.ECHEC_NOT, 3, false);
        }else {
            try {
                BigDecimal bg=BigDecimal.valueOf(Double.valueOf(prixUnitField.getText().trim()));
                
                if(descriptField.getText().trim().length()>100){
                    Res.not.showNotifications("Echec de l'ajout", 
                    "La description doit avoir 100 caractères au maximum"
                    , GlobalNotifications.ECHEC_NOT, 2, false);
                }else{
                    int codePro=generateCode();
                    Produit pr=new Produit(codePro,
                            nomProduitField.getText().trim()
                            , BigDecimal.valueOf(Double.valueOf(prixUnitField.getText())), 0
                            , descriptField.getText()
                            , codeFourfield.getText().trim(), categorie,true);

                    /** Insertion des photos dans la base de donnée **/
                    maj();
                    
                    /** Insertion proprement dite dans la base de donnée **/
                    ProduitJpaController contPro=new ProduitJpaController(Res.emf);
                    PhotoJpaController contPhoto=new PhotoJpaController(Res.emf);

                    // Insertion du produit dans la base de donnée
                    contPro.create(pr);

                    //Insertion des images dans la base de donnée
                    for (File photo_file : sourceFiles) {
                        Photo ph=new Photo(photo_file.getName(), pr);
                        contPhoto.create(ph);
                        
                        (new HTTPRequest(Res.config.getUploadPhp())).appendParam("id", Res.config.getDossierPhotsRelative()+codePro)
                                   .appendParam("img", photo_file)
                                   .post();
                        
                    }
                    
                    listProdCont.init();

                    getStage().close();
                    
                }
                
            } catch (NumberFormatException e) {
                
                Res.not.showNotifications("Echec de l'ajout", 
                    "Le prix doit être un réel"
                    , GlobalNotifications.ECHEC_NOT, 2, false);
            }
        }
        
    }
    
    private void deleteContainDirectory(String emplacement) {

        File path = new File(emplacement);
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteContainDirectory(path+"/"+files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
    }
    
    
    private void copyFileUsingStream(File source, File dest)  {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            return;
        }	
        finally {
            try {
                    is.close();
                    os.close();
            }catch(Exception ex) {}
        }
    }

    @FXML
    void onBrowse(ActionEvent event) throws FileNotFoundException, IOException {
        FileChooser fileChooser = new FileChooser();
        
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter extFilterjpg = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter extFilterpng = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG, extFilterjpg, extFilterpng);
        
        List<File> listfile = fileChooser.showOpenMultipleDialog(getStage());
        
        maj();
        if(listfile != null){
            for(File file : listfile){
                photos.add(file.getAbsolutePath().replace('\\', '/'));
                sourceFiles.add(file);
            }
        }
        
        ((HBox)(pane.getContent())).getChildren().clear();
        for (int i = 0; i < photos.size(); i++) {
            String photo=photos.get(i);
            
            InputStream is=new FileInputStream(photo);
            ImageView img = new ImageView(new Image(is));
            img.setFitWidth(208);
            img.setFitHeight(132);
            is.close();
            
            ContextMenu contextMenu=new ContextMenu();
            MenuItem item=new MenuItem("Supprimer");
            contextMenu.getItems().addAll(item);
            final int index=i;
            
            img.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent e) {
                    contextMenu.show(img, e.getScreenX(), e.getScreenY());
                }
            });
            
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ((HBox)(pane.getContent())).getChildren().remove(img);
                    aRetirer.add(index);
                }
            });
            
            ((HBox)(pane.getContent())).setSpacing(5);
            ((HBox)(pane.getContent())).getChildren().add(img);
        }
        
    }
    
    private void maj(){
        
        aRetirer.sort(null);
        for (int i = aRetirer.size()-1; i >= 0; i--) {
            photos.remove(aRetirer.get(i).intValue());
            sourceFiles.remove(aRetirer.get(i).intValue());
        }
        aRetirer.clear();
        
    }

    @FXML
    void onCancel(ActionEvent event) {
        getStage().close();
    }


    @Override
    public void init() {
        
    }

    @Override
    protected void setImagesToImageViews() {
    
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public ListeProduitsController getListProdCont() {
        return listProdCont;
    }

    public void setListProdCont(ListeProduitsController listProdCont) {
        this.listProdCont = listProdCont;
    }
    
    private int generateCode(){
        boolean flag=false;
        int codegenerated;
        ProduitJpaController cont=new ProduitJpaController(Res.emf);
        do{
            codegenerated= (int)((899999)*Math.random()+100000);
            Produit pr=cont.findProduit(Integer.valueOf(codegenerated));
            if(pr==null) flag=true;
        }while(!flag);

        return codegenerated;
    }
    
    
    
    
    
}
