/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn;

import eshopn.controllers.AdminAccueilController;
import eshopn.controllers.AdminAjoutGestionnaireController;
import eshopn.controllers.AdminEditGestionnaireController;
import eshopn.controllers.AdminEmployeesController;
import eshopn.controllers.AllCategoriesController;
import eshopn.controllers.AlterStockController;
import eshopn.controllers.CategorieController;
import eshopn.controllers.CategoriePDFController;
import eshopn.controllers.ConnexionController;
import eshopn.controllers.DétailsProduitController;
import eshopn.controllers.FactureForProduitController;
import eshopn.controllers.ListeCategoriesController;
import eshopn.controllers.ListeFacturesController;
import eshopn.controllers.ListeProduitsController;
import eshopn.controllers.MagasinierAccueilController;
import eshopn.controllers.NouveauProduitController;
import eshopn.controllers.NouvelleCategorieController;
import eshopn.controllers.ProduitController;
import eshopn.controllers.ProduitPDFController;
import eshopn.controllers.SplashScreenController;
import eshopn.entities.Categorie;
import eshopn.entities.Produit;
import eshopn.models.EshopConfigurations;
import eshopn.models.GlobalNotifications;
import eshopn.models.MGestionnaire;
import eshopn.models.Page;
import eshopn.models.Res;
import static eshopn.models.Res.emf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
/**
 *
 * @author MBOGNI RUVIC
 */
public class EShopN extends Application {
    private Scene scene;
    private Stage stage;
    private FXMLLoader loader;
    private BooleanProperty okProperty=new SimpleBooleanProperty(false);
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        setStage(primaryStage);
        
        ShowSplashScreenPage();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                if(getDatabaseConfigurations()){
            
                    String driver=Res.config.getDriver();
                    String url=Res.config.getUrl();
                    String user=Res.config.getUser();
                    String password=Res.config.getPassword();

                    if(getEntityManager(driver, url, user, password)==null){
                        
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stage.close();
                                Alert alert=new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur de connection au serveur");
                                alert.setContentText("Une erreur est survenue lors de la connection"
                                        + " au serveur.");
                                alert.showAndWait();
                            }
                        });
                        
                    }else{

                        if(Res.config.getModeStockageImage()==1){
                            
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    try {

                                        File file=new File(Res.config.getLogoAppLocal());
                                        Res.config.setLogoApp(new Image(file.toURI().toURL().toExternalForm()));
                                        okProperty.setValue(true);
                                        
                                    } catch (Exception ex) {
                                        
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                stage.close();
                                                Alert alert=new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Erreur");
                                                alert.setHeaderText("Logo introuvable");
                                                alert.setContentText(Res.config.getLogoAppLocal() +" Inacessible.");
                                                alert.showAndWait();
                                            }
                                        });
                                    }
                                }
                                
                            }).start();                                    
                            
                            
                        }else{
                            
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //InputStream is;
                                    try {

                                        /*is = Res.config.getLogoAppURL().openStream();
                                        Res.config.setLogoApp(new Image(is));
                                        okProperty.setValue(true);*/
                                        
                                        File file=new File(Res.config.getLogoAppLocal());
                                        Res.config.setLogoApp(new Image(file.toURI().toURL().toExternalForm()));
                                        okProperty.setValue(true);  

                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                stage.close();
                                                Alert alert=new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Erreur");
                                                alert.setHeaderText("Erreur de connection au serveur");
                                                alert.setContentText("Une erreur est survenue lors de l'accès à logo de l'application"
                                                        + ". Veuillez verifier vos configurations.");
                                                alert.showAndWait();
                                            }
                                        });
                                    }
                                }
                                
                            }).start();                                    
                        }
                        
                    }

                }else{
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.close();
                            Alert alert=new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur");
                            alert.setHeaderText("Erreur de chargement des données");
                            alert.setContentText("Une erreur est survenue lors du chargement des données. "
                                    + "Verifier vos configurations, verifier également si votre serveur"
                                    + "est accessible. ");
                            alert.showAndWait();
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
                            ShowConnexionPage();
                        }
                    });
                }
            }
        });
        
    }
    
    public EntityManager getEntityManager(String driver, String url, String username, String password) {
        EntityManager em = null;
        Map properties = new HashMap();
        properties.put("javax.persistence.jdbc.driver", driver);
        properties.put("javax.persistence.jdbc.url", url);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        
        try {
            emf = Persistence.createEntityManagerFactory("EShopNPU", properties);
            em=(EntityManager) emf.createEntityManager();
            return em;
        } catch (Exception e) {
            return null;
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dir=new File("temp");
        dir.mkdir();
        launch(args);
        
    }
    
    /*****************************************************************
     * Méthode pour afficher les vues de connection et les vues du root
     ******************************************************************/
    public void ShowSplashScreenPage(){
        try {
            
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/SplashScreen.fxml"));
            AnchorPane root=loader.load();
            setScene(new Scene(root, 502,265));

            SplashScreenController controller=loader.getController();
            controller.setStage(stage);
            
            stage.setScene(getScene());
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.centerOnScreen();
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void ShowConnexionPage(){
        
        try {
            
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/connexion.fxml"));
            BorderPane root=loader.load();
            setScene(new Scene(root, 685,529));

            ConnexionController controller=loader.getController();
            controller.setScene(getScene());
            controller.setStage(stage);
            controller.setMain(this);
            controller.init();
            
            stage.close();
            stage=new Stage();
            stage.setScene(getScene());
            stage.getIcons().add(Res.config.getLogoApp());
            stage.centerOnScreen();
            stage.setMaximized(true);
            stage.setTitle("EShop");
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    public void showAdminAcceuilView(){
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/AdminAccueil.fxml"));
            BorderPane root=loader.load();
            setScene(new Scene(root));

            AdminAccueilController controller=loader.getController();
            controller.setScene(getScene());
            controller.setMain(this);
            controller.setStage(stage);
            controller.init();  
            
            getScene().getStylesheets().add(
                getClass().getResource("views/css/style.css").toExternalForm()
            );
            
            setMaximized(stage);
            stage.setScene(getScene());
            stage.centerOnScreen();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showAdminEmployeeView(boolean isStorekeeper){
        
        AdminEmployeesController.isStoreKeeper=isStorekeeper;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/AdminEmployee.fxml"));
            BorderPane root=loader.load();
            setScene(new Scene(root, 900,479));

            AdminEmployeesController controller=loader.getController();
            controller.setScene(getScene());
            controller.setMain(this);
            controller.setStage(stage);
            controller.init();
            getScene().getStylesheets().add(
                getClass().getResource("views/css/style.css").toExternalForm()
            );

            setMaximized(stage);
            stage.setScene(getScene());
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showListeFactures(){
        
        try {
            
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/ListeFactures.fxml"));
            BorderPane root=loader.load();
            setScene(new Scene(root, 900,479));

            ListeFacturesController controller=loader.getController();
            controller.setScene(getScene());
            controller.setMain(this);
            controller.setStage(stage);
            controller.init();
            getScene().getStylesheets().add(
                getClass().getResource("views/css/style.css").toExternalForm()
            );

            setMaximized(stage);
            stage.setScene(getScene());
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public AnchorPane getCatalogue(List<Categorie> allCategorie, Page page){
        
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/allCategories.fxml"));
            AnchorPane root=loader.load();
            AllCategoriesController cont=loader.getController();
            cont.setAllCategorie(allCategorie);
            cont.setPage(page);
            cont.setMain(this);
            cont.init();
            return root;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AnchorPane getCategoriePDF(List<Categorie> allCategories, Page page){
        
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/categoriePDF.fxml"));
            AnchorPane root=loader.load();
            CategoriePDFController cont=loader.getController();
            cont.setAllCategorie(allCategories);
            cont.setPage(page);
            cont.setMain(this);
            cont.init();
            return root;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public AnchorPane getProduitPDF(Produit produit){
        
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/produitPDF.fxml"));
            AnchorPane root=loader.load();
            ProduitPDFController cont=loader.getController();
            cont.setProduit(produit);
            cont.setMain(this);
            cont.init();
            return root;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
   
    public void showAddGestionnaire(TableView<MGestionnaire> table, boolean isStorekeeper){
        Stage new_stage=new Stage();
        Scene new_scene;
        AdminAjoutGestionnaireController.isStorekeeper=isStorekeeper;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/AdminAjoutGestionnaire.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 455,593);
            
            AdminAjoutGestionnaireController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setTable(table);
            controller.setStage(new_stage);
            controller.init();
            
            new_stage.setScene(new_scene);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.setResizable(false);
            new_stage.centerOnScreen();
            new_stage.setTitle("EShop - Ajout "+((isStorekeeper)?"Magasinier":"Caissier"));
            new_stage.initModality(Modality.APPLICATION_MODAL);
            new_stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showFactureForProduitview(){
        Stage new_stage=new Stage();
        Scene new_scene;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/FactureForProduit.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 497,511);
            
            FactureForProduitController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setStage(new_stage);
            controller.init();
            
            new_stage.setScene(new_scene);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.setResizable(false);
            new_stage.centerOnScreen();
            new_stage.setTitle("Factures pour produit");
            new_stage.initModality(Modality.APPLICATION_MODAL);
            new_stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showEditGestionnaire(MGestionnaire gest, boolean isStorekeeper){
        Stage new_stage=new Stage();
        Scene new_scene;
        AdminEditGestionnaireController.isStorekeeper=isStorekeeper;
        AdminEditGestionnaireController.current_gestionnaire=gest;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/AdminEditGestionnaire.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 455,593);
            
            AdminEditGestionnaireController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setStage(new_stage);
            controller.init();
            
            new_stage.setScene(new_scene);
            new_stage.setResizable(false);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.centerOnScreen();
            new_stage.setTitle("EShop - Modifier "+((isStorekeeper)?"Magasinier":"Caissier"));
            new_stage.initModality(Modality.APPLICATION_MODAL);
            new_stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /***************************************************************************
     * Chargement des vues du magasinier
     ***************************************************************************/
    public void showMagasinierAccueilView(){
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/MagasinierAccueil.fxml"));
            BorderPane root=loader.load();
            setScene(new Scene(root));

            MagasinierAccueilController controller=loader.getController();
            controller.setScene(getScene());
            controller.setMain(this);
            controller.setStage(stage);
            controller.init();
            
            getScene().getStylesheets().add(
                getClass().getResource("views/css/style.css").toExternalForm()
            );
            
            setMaximized(stage);
            stage.setScene(getScene());
            stage.centerOnScreen();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showNouveauProduit(Categorie newCat, ListeProduitsController cont){
         Stage new_stage=new Stage();
         Scene new_scene;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/NouveauProduit.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 655,615);
            
            NouveauProduitController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setStage(new_stage);
            controller.setCategorie(newCat);
            controller.setListProdCont(cont);
            controller.init();
            
            new_stage.setScene(new_scene);
            new_stage.setResizable(false);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.centerOnScreen();
            new_stage.setTitle("EShop - Ajouter un nouveau produit");
            new_stage.initModality(Modality.APPLICATION_MODAL);
            new_stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    //Cette méthode doit pendre en paramètre un objet Produit
    public void showDetailsProduit(Produit prod, ListeProduitsController cont){
        Stage new_stage=new Stage();
        Scene new_scene;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/DétailsProduit.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 645,600);
            
            DétailsProduitController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setStage(new_stage);
            controller.setListProCont(cont);
            controller.setProduit(prod);
            
            new_stage.setScene(new_scene);
            new_stage.setResizable(false);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.centerOnScreen();
            new_stage.setTitle("EShop - Ajouter un nouveau produit");
            new_stage.initModality(Modality.APPLICATION_MODAL);
            
            controller.init();

            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    public void showNouvelleCategorie(boolean isNew, Categorie categorie, ListeCategoriesController list){
        Stage new_stage=new Stage();
        Scene new_scene;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/NouvelleCategorie.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 441,348);
            
            NouvelleCategorieController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setStage(new_stage);
            controller.setIsNew(isNew);
            controller.setCurrent_categorie(categorie);
            controller.setListCatCont(list);
            controller.init();
            
            new_stage.setScene(new_scene);
            new_stage.setResizable(false);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.centerOnScreen();
            new_stage.setTitle("EShop - Ajouter une nouvelle catégorie");
            new_stage.initModality(Modality.APPLICATION_MODAL);
            new_stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
     
     public void showGestionStock(boolean flag, Produit prod, ListeProduitsController cont){
         Stage new_stage=new Stage();
         Scene new_scene;
        try {
            loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("views/AlterStock.fxml"));
            AnchorPane root=loader.load();
            new_scene=new Scene(root, 441,348);
            
            AlterStockController controller=loader.getController();
            controller.setScene(new_scene);
            controller.setMain(this);
            controller.setStage(new_stage);
            controller.setOperation(flag);
            controller.setCurrent_produit(prod);
            controller.setListProdCont(cont);
            controller.init();
            
            new_stage.setScene(new_scene);
            new_stage.setResizable(false);
            new_stage.getIcons().add(Res.config.getLogoApp());
            new_stage.centerOnScreen();
            new_stage.setTitle("EShop - Gestion Stock");
            new_stage.initModality(Modality.APPLICATION_MODAL);
            new_stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }  
     }
    
    public FXMLLoader replace(String view, AnchorPane content){
        FXMLLoader loader = new FXMLLoader();
        try{
            loader.setLocation(getClass().getResource("views/"+view));
            AnchorPane contenu = (AnchorPane) loader.load();
            content.getChildren().clear();
            content.getChildren().setAll(contenu);
            
            
            AnchorPane.setTopAnchor(contenu, 0.0);
            AnchorPane.setBottomAnchor(contenu, 0.0);
            AnchorPane.setRightAnchor(contenu, 0.0);
            AnchorPane.setLeftAnchor(contenu, 0.0);            
            
        }catch (Exception e){
            System.out.println("Erreur de chargement de la vue : "+view);
            e.printStackTrace();
        }
        return loader;
     }
    
    
    public Node getCategorieView(AnchorPane content, Categorie categ, ListeCategoriesController list){
        Node contenu = null;
        try{
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/Categorie.fxml"));
            contenu = (AnchorPane) loader.load();
            CategorieController cat = loader.getController();
            cat.setContent(content);
            cat.setMain(this);
            cat.setScene(list.getScene());
            cat.setStage(list.getStage());
            cat.setCategorie(categ);
            cat.setListCatCont(list);
            cat.init();

        }catch (Exception e){
            e.printStackTrace();
        }
         return contenu;
    }
    
    public Node getProductView(AnchorPane content, Produit prod, ListeProduitsController cont){
        Node contenu = null;
        try{
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/Produit.fxml"));
            contenu = (AnchorPane) loader.load();
            ProduitController cat = loader.getController();
            cat.setContent(content);
            cat.setMain(this);
            cat.setListProdCont(cont);
            cat.setCurrent_product(prod);
            cat.init();

        }catch (Exception e){
            e.printStackTrace();
        }
         return contenu;
    }
    
    
    /********************************************************************
     *   Méthode 
     * ******************************************************************
     */
    
    private void setMaximized(Stage stage){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }
    
    public boolean getDatabaseConfigurations(){
        
	try {
            
            Properties prop = new Properties();
            InputStream input = null;
            
            try {
                input = new FileInputStream("eshop/config.properties");
                prop.load(input);
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            String admin=prop.getProperty("admin");
            String adminpwd=prop.getProperty("adminpwd");
            String adresse=prop.getProperty("adresse");
            String tel=prop.getProperty("tel");
            
            String bd=prop.getProperty("bd");
            String user=prop.getProperty("user");
            String password=prop.getProperty("password");
            String server=prop.getProperty("server");
            String serverBD=prop.getProperty("serverBD");
            String port=prop.getProperty("port");
            String portBD=prop.getProperty("portBD");
            
            int modeStockageImage=Integer.parseInt(prop.getProperty("ModeStockageImage"));
            String dossierImagesLocal=prop.getProperty("dossierImagesLocal");
            String dossierLogosLocal=prop.getProperty("dossierLogosLocal");
            String logoAppLocal=prop.getProperty("logoAppLocal");
            String logoPdfLocal=prop.getProperty("logoPdfLocal");
            String logoConnexionLocal=prop.getProperty("logoConnexionLocal");
            
            String dossierApp=prop.getProperty("dossierApp");
            String dossierphotos=prop.getProperty("dossierphotos");
            String dossierLogos=prop.getProperty("dossierLogos");
            String requestController=prop.getProperty("requestController");
            String logoApp=prop.getProperty("logoApp");
            String logoPdf=prop.getProperty("logoPdf");
            String logoCon=prop.getProperty("logoConnexion");
            
            double remise=Double.parseDouble(prop.getProperty("remise"));
            String nro=prop.getProperty("nro");
            String rc=prop.getProperty("rc");
            double minoration=Double.parseDouble(prop.getProperty("minoration"));
            
            String dossierFacturesPdf=prop.getProperty("dossierFactures");
            String dossierProduitsPdf=prop.getProperty("dossierProduits");
            String dossierStocksPdf=prop.getProperty("dossierStocks");
            
            Res.config=new EshopConfigurations(
                    admin, adminpwd, adresse, tel,
                    bd, server, serverBD, port,portBD, user, password,
                    dossierApp, dossierphotos,dossierLogos,requestController
                    ,logoApp, logoPdf,logoCon ,remise,nro,rc,
                    dossierFacturesPdf, dossierProduitsPdf, dossierStocksPdf,
                    modeStockageImage, dossierImagesLocal, dossierLogosLocal,
                    logoAppLocal,logoPdfLocal ,logoConnexionLocal, minoration);
            
            return true;
            
        } catch (Exception ex) {
            return false;
	}
        
    }
    
    
    /******************************************************
     * Getters and Setters
     ******************************************************/
    /**
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * @param scene the scene to set
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    
    
}
