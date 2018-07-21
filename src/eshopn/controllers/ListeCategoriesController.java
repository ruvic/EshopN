/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Categorie;
import eshopn.entities.controllers.CategorieJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import eshopn.models.Res;
import eshopn.models.Tasks;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author FRANCINE
 */
public class ListeCategoriesController extends Controllers implements Initializable {

    @FXML
    private ImageView arrowsImg;

    @FXML
    private ImageView arrowsSelected;

    @FXML
    private GridPane listeCat;
    
    @FXML
    private ImageView loaderImg;
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private JFXTextField categorieField;
    
    @FXML
    private JFXButton newCategorBtn;
    
    private AnchorPane content;
    
   
    private List<Categorie> listes;
    private List<Categorie> listesGen;
    public final double STAGE_MIN_WIDTH=800;
    public final double STAGE_MIN_HEIGHT=700;

    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        loaderImg.setVisible(false);
        Res.listeCat=listeCat;
        
        if(!Res.connected_storekeeper.getTypeGest()){
            newCategorBtn.setVisible(false);
        }
        
    }    

    @Override
    public void init() {
        
        CategorieJpaController cont=new CategorieJpaController(Res.emf);
        ProduitJpaController contPro=new ProduitJpaController(Res.emf);
        listes=contPro.findCategoriesByDescProductsCount();
        List<Categorie> list1=cont.findCategorieEntities();
        
        for (Categorie categorie : list1) {
            if(listes.indexOf(categorie)<0){
                listes.add(categorie);
            }
        }
        
        setPaginationProperties(Res.NbreEltParLigne*2,listes);
        
        listesGen=listes;
        
        getScene().widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setPaginationProperties(Res.NbreEltParLigne*2, listesGen);
            }
            
        });
        
        getStage().setMinWidth(STAGE_MIN_WIDTH);
        getStage().setMinHeight(STAGE_MIN_HEIGHT);
        
    }
    
    public void setPaginationProperties(int itemPerPage, List<Categorie> listC){
        Res.reset();
        int pages, nbreElts=listC.size();
        
        if(nbreElts%itemPerPage==0){
            pages=nbreElts/itemPerPage;
        }else{
            pages=(nbreElts/itemPerPage) + 1;
        }
        pages=(pages==0)?1:pages;
        pagination.setPageCount(pages);
        
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex > listC.size() / itemPerPage + 1) {
                    return null;
                } else {
                    return createPage(pageIndex, listC,itemPerPage);
                }
            }            
        });
        
        categorieField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.isEmpty()){
                    init();
                }
            }
        });
    }
    
    
    private Node createPage(Integer pageIndex, List<Categorie> list, int itemPerPages) {
        Res.reset();
        Res.listeCat.getChildren().clear();
        int nb=(pageIndex+1)*itemPerPages;
        Res.not.showLoader(Res.stackPane);
        for (int i = nb-itemPerPages; i<list.size() && i < nb; i++) {
            Categorie cat=list.get(i);
            Res.listeCat.add(getMain().getCategorieView(content, cat, this)
                , Res.getColumnIndex()
                , Res.getIndexLine());
        }
        Res.stackPane.setVisible(false);
        return (new AnchorPane());
    }
    

    @Override
    protected void setImagesToImageViews() {
        Image arrow=new Image("arrow.png");
        Image arrow_selected=new Image("arrow_selected.png");
        arrowsImg.setImage(arrow);
        arrowsSelected.setImage(arrow_selected);
        loaderImg.setImage(new Image("chargement2.gif"));
    }
    
    @FXML
    void onNewCategorie(ActionEvent event) {
          getMain().showNouvelleCategorie(true,null,this);
    }
    
    @FXML
    void onCatalogue(ActionEvent event) {
        Tasks task=new Tasks(getMain(), loaderImg);
        Thread t=new Thread(task);
        t.start();
    }
    
    @FXML
    void onSearch(ActionEvent event) {
        String categ=categorieField.getText().trim().toLowerCase();
        
        List<Categorie> listesCats=new ArrayList<>();
        
        for (Categorie categorie : listes) {
            if(categorie.getNomCat().toLowerCase().contains(categ)){
                listesCats.add(categorie);
            }
        }
       
        setPaginationProperties(Res.NbreEltParLigne*2, listesCats);
        
        listesGen=listesCats;
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

    
    
    public AnchorPane getContent() {
        return content;
    }    

    /**
     * Initializes the controller class.
     */
    public void setContent(AnchorPane content) {    
        this.content = content;
    }
   
}
