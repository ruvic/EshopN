/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Photo;
import eshopn.entities.Produit;
import eshopn.entities.controllers.PhotoJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class DétailsProduitController extends Controllers implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView photoVue;

    @FXML
    private JFXTextField codeProd;

    @FXML
    private JFXTextField nomProd;

    @FXML
    private JFXTextField prixProd;

    @FXML
    private JFXTextField qteProd;
    
    @FXML
    private JFXTextField codeFourField;
     
    @FXML
    private JFXTextField categorieProd;

    @FXML
    private JFXTextArea descriptionProd;
    
    
    @FXML
    private JFXButton modifBTn;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private JFXButton browsBtn;
    
    @FXML
    private ScrollPane pane;
    
    private ArrayList<String> photos = new ArrayList<>();
    private ArrayList<File> photosFile = new ArrayList<>();
    private ArrayList<File> sourceFiles = new ArrayList<>();
    private ArrayList<String> photosAsupprimer = new ArrayList<>();
    private Produit produit;
    private ListeProduitsController listProCont;
    private HBox imgsBox;
    private ArrayList<Integer> aRetirer = new ArrayList<>();
    private List<Photo> listPhotos;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        imgsBox=new HBox();
        pane.setContent(imgsBox);
        
        if(!Res.connected_storekeeper.getTypeGest()){
            browsBtn.setVisible(false);
            cancelBtn.setVisible(false);
            modifBTn.setText("OK");
            nomProd.setEditable(false);
            prixProd.setEditable(false);
            qteProd.setEditable(false);
            codeFourField.setEditable(false);
            descriptionProd.setEditable(false);
        }
        
    }    

    @Override
    public void init() {
        
        codeProd.setText(formatCode(produit.getCodePro().toString()));
        nomProd.setText(produit.getNomPro());
        prixProd.setText(produit.getPrix().toString());
        qteProd.setText(produit.getQte()+"");
        categorieProd.setText(produit.getIdCategorie().getNomCat());
        descriptionProd.setText(produit.getDescription());
        codeFourField.setText(produit.getCodeFour());
        
        /** Récupération d'une image du produit **/
        PhotoJpaController cont=new PhotoJpaController(Res.emf);
        
        try {
            
            listPhotos=(new ArrayList<>(produit.getPhotoCollection()));
            
            loadImage(produit.getCodePro().toString(), listPhotos.get(0).getLienPhoto(), photoVue);
            
            /*URL url = new URL(lienAbsolueImage(listPhotos.get(0)));
            InputStream is = url.openStream();
            photoVue.setImage(new Image(is));
            is.close();*/
            
            for (int i = 0; i < listPhotos.size(); i++) {
                Photo pht=listPhotos.get(i);
                String photo=lienAbsolueImage(pht);
                photos.add(photo);

                File fil=new File(photo);
                sourceFiles.add(fil);

                try {
                    
                    /*URL url2 = new URL(lienAbsolueImage(pht));
                    InputStream is2 = url2.openStream();
                    final ImageView img = new ImageView(new Image(is2));*/
                    
                    final ImageView img = new ImageView();
                    loadImage(produit.getCodePro().toString(), pht.getLienPhoto(), img);
                    
                    img.setFitWidth(97);
                    img.setFitHeight(84);

                    final ContextMenu contextMenu=new ContextMenu();
                    final MenuItem item=new MenuItem("Supprimer");
                    final int index=i;
                    contextMenu.getItems().addAll(item);

                    if(Res.connected_storekeeper.getTypeGest()){

                        img.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>(){
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
                                photosAsupprimer.add(pht.getLienPhoto());
                            }
                        });
                    }

                    img.setOnMouseClicked(new EventHandler<MouseEvent>(){
                        @Override
                        public void handle(MouseEvent event) {
                            photoVue.setImage(img.getImage());
                        }
                    });

                    ((HBox)(pane.getContent())).setSpacing(5);
                    ((HBox)(pane.getContent())).getChildren().add(img);

                    //is2.close();
                } catch(Exception e){
                    Res.not.showNotifications("Echec de l'ajout", 
                        "Impossible de se connecter au serveur."
                        , GlobalNotifications.ECHEC_NOT, 2, false);
                }
            }
            
            this.getStage().show();
            
        } catch (Exception e) {
            Res.not.showNotifications("Echec de l'ajout", 
                        "Impossible de se connecter au serveur."
                        , GlobalNotifications.ECHEC_NOT, 2, false);
        }
        
        
        
        
        
    }
    
    

    @Override
    protected void setImagesToImageViews() {
        
    }

    @FXML
    void onClose(ActionEvent event) {
        getStage().close();
    }

    @FXML
    void onEdit(ActionEvent event) {
        
        if(!Res.connected_storekeeper.getTypeGest()){
            getStage().close();
        }else{
            
            if(nomProd.getText().trim().isEmpty() 
                 || prixProd.getText().trim().isEmpty()
                 || codeProd.getText().trim().isEmpty()
                 || descriptionProd.getText().trim().isEmpty()
                 || ((HBox)(pane.getContent())).getChildren().size()==0){

                Res.not.showNotifications("Echec de l'ajout", 
                        "Toutes les champs doivent être remplis, \n"
                                + "et le produit doit avoir au moins une image"
                        , GlobalNotifications.ECHEC_NOT, 2, false);
            }else{
                try {
                    BigDecimal bg=BigDecimal.valueOf(Double.valueOf(prixProd.getText().trim()));

                    try {
                        produit.setNomPro(nomProd.getText().trim());
                        produit.setPrix(BigDecimal.valueOf(Double.valueOf(prixProd.getText().trim())));
                        produit.setQte(Integer.valueOf(qteProd.getText()));
                        produit.setDescription(descriptionProd.getText().trim());

                        ProduitJpaController contPro=new ProduitJpaController(Res.emf);
                        PhotoJpaController contPht=new PhotoJpaController(Res.emf);
                        contPro.edit(produit);

                        maj();

                        //Suppression de toutes les photos à supprimer dans la base de donnée
                        int compteur=0;
                        for (Photo ph : listPhotos) {
                            System.out.println("=> "+ph.getLienPhoto());
                            for (String phS : photosAsupprimer) {
                                if(phS.equals(ph.getLienPhoto())){
                                    try {
                                        compteur++;
                                        System.out.println(compteur +" :  => delete : " +ph.getIdPhoto());
                                        contPht.destroy(ph.getIdPhoto());
                                    } catch (NonexistentEntityException ex) {
                                        Logger.getLogger(DétailsProduitController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }

                        //Suppression des images présent sur le serveur et qui ont été supprimer
                        for (String string : photosAsupprimer) {

                            (new HTTPRequest(Res.config.getUploadPhp())).appendParam("id", Res.config.getDossierPhotsRelative()+produit.getCodePro())
                                   .appendParam("file_to_delete", string)
                                   .post();
                        }

                        //Insertion des images dans la base de donnée
                        for (File photo_file : sourceFiles) {

                            if(!photo_file.getAbsolutePath().contains("http:")){
                                Photo ph=new Photo(photo_file.getName(), produit);
                                contPht.create(ph);

                                (new HTTPRequest(Res.config.getUploadPhp())).appendParam("id", Res.config.getDossierPhotsRelative()+produit.getCodePro())
                                   .appendParam("img", photo_file)
                                   .post();

                            }
                        }

                        getStage().close();

                        Res.not.showNotifications("Confirmation Modification", 
                                "Les modifications du produit "+produit.getNomPro()+"\n"
                                        + "ont été pris en compte"
                                , GlobalNotifications.SUCCESS_NOT, 3, false);

                        listProCont.init();

                    } catch (Exception ex) {
                        Res.not.showNotifications("Echec", 
                                "Impossible de se connecter au serveur."
                                , GlobalNotifications.ECHEC_NOT, 3, false);
                    }  
                    
                } catch (Exception e) {
                    Res.not.showNotifications("Echec de l'ajout", 
                        "Le prix doit être un réel"
                        , GlobalNotifications.ECHEC_NOT, 2, false);
                }
            }
        }
        
        
        
        
    }
    
    @FXML
    void onBrowse(ActionEvent event) throws FileNotFoundException, MalformedURLException, IOException {
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
            
            InputStream is=null;
            URL url=null;
            if(photo.contains("http:/")){
                url = new URL(photo);
                is= url.openStream();
            }else{
                is=new FileInputStream(photo);
            }
            
            ImageView img = new ImageView(new Image(is));
            img.setFitWidth(97);
            img.setFitHeight(84);
            
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
            
            img.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    photoVue.setImage(img.getImage());
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
            
            is.close();
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

    public ListeProduitsController getListProCont() {
        return listProCont;
    }

    public void setListProCont(ListeProduitsController listProCont) {
        this.listProCont = listProCont;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    
    
}
