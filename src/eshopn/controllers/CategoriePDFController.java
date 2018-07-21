/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import eshopn.entities.Categorie;
import eshopn.entities.Produit;
import eshopn.models.Page;
import eshopn.models.TypePage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author MBOGNI RUVIC
 */
public class CategoriePDFController extends Controllers implements Initializable {

    @FXML
    private VBox vboxPane;

    @FXML
    private Label categorieLabel;

    @FXML
    private HBox content;

    @FXML
    private Label categorieLabel2;

    @FXML
    private HBox content2;

    
    
    private List<Categorie> allCategorie;
    private Page page;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void init() {
        
        if(page!=null){
            if(page.getTypePage().equals(TypePage.FIRST)){
                categorieLabel.setText(allCategorie.get(0).getNomCat());
                vboxPane.getChildren().remove(categorieLabel2);
                vboxPane.getChildren().remove(content2);
                int debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                int fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(0).getProduitCollection());
                for (int i = debut; i < fin; i++) {
                    content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                }
            }
            if(page.getTypePage().equals(TypePage.ZERO)){
                vboxPane.getChildren().remove(categorieLabel);
                vboxPane.getChildren().remove(categorieLabel2);
                int debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                int fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(0).getProduitCollection());
                for (int i = debut; i < fin; i++) {
                    if(i<debut+4){
                        content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }else{
                        content2.getChildren().add(getMain().getProduitPDF(produits.get(i)));     
                    }
                }
            }
            if(page.getTypePage().equals(TypePage.DEUX)){
                vboxPane.getChildren().remove(categorieLabel2);
                categorieLabel.setText(allCategorie.get(0).getNomCat());
                int debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                int fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(0).getProduitCollection());
                for (int i = debut; i < fin; i++) {
                    if(i<debut+4){
                        content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }else{
                        content2.getChildren().add(getMain().getProduitPDF(produits.get(i)));     
                    }
                }
            }
            if(page.getTypePage().equals(TypePage.TROIS)){
                System.out.println(allCategorie.size());
                if(allCategorie.size()>1){
                    System.out.println(allCategorie.get(0).getNomCat()+" AND "+allCategorie.get(1).getNomCat());
                    categorieLabel.setText(allCategorie.get(1).getNomCat());
                    categorieLabel2.setText(allCategorie.get(0).getNomCat());

                    int debut=(Integer)page.getListeCateg().get(allCategorie.get(1)).get(0);
                    int fin=(Integer)page.getListeCateg().get(allCategorie.get(1)).get(1);
                    ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(1).getProduitCollection());
                    for (int i = debut; i < fin; i++) {
                        content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }

                    debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                    fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                    produits=new ArrayList<>(allCategorie.get(0).getProduitCollection());
                    for (int i = debut; i < fin; i++) {
                        content2.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }
                }else{
                    categorieLabel.setText(allCategorie.get(0).getNomCat());
                    vboxPane.getChildren().remove(categorieLabel2);

                    int debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                    int fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                    ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(0).getProduitCollection());
                    for (int i = debut; i < fin; i++) {
                        content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }
                }

            }
            
            if(page.getTypePage().equals(TypePage.UN)){
                
                if(allCategorie.size()>1){
                    vboxPane.getChildren().remove(categorieLabel);
                    int debut=(Integer)page.getListeCateg().get(allCategorie.get(1)).get(0);
                    int fin=(Integer)page.getListeCateg().get(allCategorie.get(1)).get(1);
                    ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(1).getProduitCollection());
                    for (int i = debut; i < fin; i++) {
                        content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }

                    categorieLabel2.setText(allCategorie.get(0).getNomCat());
                    debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                    fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                    produits=new ArrayList<>(allCategorie.get(1).getProduitCollection());
                    for (int i = debut; i < fin; i++) {
                        content2.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }
                }else{
                    vboxPane.getChildren().remove(categorieLabel);
                    int debut=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(0);
                    int fin=(Integer)page.getListeCateg().get(allCategorie.get(0)).get(1);
                    ArrayList<Produit> produits=new ArrayList<>(allCategorie.get(0).getProduitCollection());
                    for (int i = debut; i < fin; i++) {
                        content.getChildren().add(getMain().getProduitPDF(produits.get(i)));
                    }
                }
                
            }
        }
    }

    @Override
    protected void setImagesToImageViews() {
        
    }

    /**
     * @return the allCategorie
     */
    public List<Categorie> getAllCategorie() {
        return allCategorie;
    }

    /**
     * @param allCategorie the allCategorie to set
     */
    public void setAllCategorie(List<Categorie> allCategorie) {
        this.allCategorie = allCategorie;
    }

  
    /**
     * @param page the page to set
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }
    
}
