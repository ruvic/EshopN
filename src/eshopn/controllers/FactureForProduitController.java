/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import com.jfoenix.controls.JFXTextField;
import eshopn.entities.Facture;
import eshopn.entities.Lignefacture;
import eshopn.entities.controllers.FactureJpaController;
import eshopn.entities.controllers.LignefactureJpaController;
import eshopn.models.MFactProd;
import eshopn.models.MFacture;
import eshopn.models.Res;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class FactureForProduitController extends Controllers implements Initializable {

    @FXML
    private Rectangle rect;

    @FXML
    private ImageView fondImgView;

    @FXML
    private JFXTextField codeProField;

    @FXML
    private Pagination pagination;

    @FXML
    private TableView<MFactProd> table;

    @FXML
    private TableColumn<MFactProd, Integer> numFactColumn;

    @FXML
    private TableColumn<MFactProd, Integer> qteColumn;

    @FXML
    private StackPane stack;
    
    private ObservableList<MFactProd> allLignFactProds=FXCollections.observableArrayList();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setImagesToImageViews();
        
        numFactColumn.setCellValueFactory(new PropertyValueFactory<>("numFact"));
        qteColumn.setCellValueFactory(new PropertyValueFactory<>("qte"));
        
    }    

    @Override
    public void init() {
        
        LignefactureJpaController cont=new LignefactureJpaController(Res.emf);
        List<Lignefacture> all=cont.findLignefactureEntities(true);
        for (Lignefacture lignefacture : all) {
            allLignFactProds.add(new MFactProd(lignefacture));
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
            }
        });
        
    }

    @Override
    protected void setImagesToImageViews() {
        Image fond=new Image("fond.jpg");
        fondImgView.setImage(fond);
    }
    
    @FXML
    void onClose(ActionEvent event) {
        getStage().close();
    }
    
    @FXML
    void onSearch(ActionEvent event) {
        String input=codeProField.getText().trim().replace("-", "");
        ObservableList<MFactProd> newFactProds=FXCollections.observableArrayList();
        
        for (MFactProd factProd : allLignFactProds) {
            if(factProd.getCodePro().toString().equals(input)){
                newFactProds.add(factProd);
            }
        }
        showDatasOnTableView(newFactProds, pagination, table,10);
    }
    
    
}
