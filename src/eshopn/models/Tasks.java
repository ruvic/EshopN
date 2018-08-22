/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import javafx.print.PrinterJob;
import eshopn.EShopN;
import eshopn.controllers.AllCategoriesController;
import eshopn.entities.Categorie;
import eshopn.entities.Produit;
import eshopn.entities.controllers.CategorieJpaController;
import eshopn.entities.controllers.ProduitJpaController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author MBOGNI RUVIC
 */
public class Tasks implements Runnable {

    
    private EShopN eshop;
    private ImageView loader;
    private List<Categorie> allCategorie;
    private List<Page> pages; 
    private HashMap<Categorie, Integer> nbreProduitsParCat; 
    private int tailleNonZeroMax=0;
    

    public Tasks(EShopN eshop, ImageView loader) {
        this.eshop = eshop;
        this.loader=loader;
        pages=new ArrayList<>();
        nbreProduitsParCat=new HashMap<>();
        
    }
    
    @Override
    public void run() {
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loader.setVisible(true);
            }
        });
        
        CategorieJpaController cont=new CategorieJpaController(Res.emf);
        ProduitJpaController contPro=new ProduitJpaController(Res.emf);
        this.allCategorie=contPro.findCategoriesByDescProductsCount();
        
        removeZeroProduits();
        
        
        for (Categorie categorie : allCategorie) {
            if(categorie.getProduitCollection().size()!=0){
                tailleNonZeroMax++;
            }
            nbreProduitsParCat.put(categorie, Integer.valueOf(categorie.getProduitCollection().size()));
        }
        
        
        
        //Première page
        int size_first_elt=allCategorie.get(0).getProduitCollection().size();
        HashMap<Categorie, ArrayList> hash=new HashMap<>();
        ArrayList<Integer> array=new ArrayList<>();
        if(size_first_elt<=4){
            array.add(0);
            array.add(size_first_elt);
            hash.put(allCategorie.get(0), array);
            pages.add(new Page(TypePage.FIRST, hash));
            getPages(1, null);
        }else{
            array.add(0);
            array.add(4);
            hash.put(allCategorie.get(0), array);
            pages.add(new Page(TypePage.FIRST, hash));
            nbreProduitsParCat.replace(allCategorie.get(0), nbreProduitsParCat.get(allCategorie.get(0))-4);
            getPages(0, null);
        }
        
        /*** Affichage des pages **/
        
        int i=1;
        for (Page page : pages) {
            System.out.println(page);
            page.setPageNumber(i);
            i++;
        }
        
        
        /** Impression de toutes les pages **/
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job !=null) {
            if (job.showPrintDialog(null)) {
                Printer printer=job.getPrinter();
                PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
                print(job,pageLayout);
                job.endJob();
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        (new GlobalNotifications()).showNotifications("Fin de l'impression", "Fin de l'impression du catalogue", GlobalNotifications.SUCCESS_NOT, 1, true);
                    }
                });
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loader.setVisible(false);
            }
        });
    }
    
    private void getPages(int index_categorie, Page page){
        HashMap<Categorie,ArrayList> hash=new HashMap<>();
        if(index_categorie<tailleNonZeroMax){
            Categorie cat=allCategorie.get(index_categorie);
            if(page==null){
                int taille=nbreProduitsParCat.get(cat);
                int r=taille%8;
                int q=(taille/8)+((r>4)?1:0);
                boolean isPass=false;
                for (int i = 1; i <= q; i++) {
                    int k=indexDep(index_categorie);
                    int m=indexFin(index_categorie);
                    ArrayList<Integer> array=new ArrayList<>();
                    array.add(k);
                    array.add(m);
                    hash=new HashMap<>();
                    hash.put(cat, array);
                    
                    if(i==1){
                        pages.add(new Page(TypePage.DEUX, hash));
                    }else{
                        pages.add(new Page(TypePage.ZERO, hash));
                    }
                    nbreProduitsParCat.replace(cat, taille-8);
                    isPass=true;
                }
                
                if(r>=1){
                    int k=indexDep(index_categorie);
                    int m=indexFin(index_categorie);
                    ArrayList<Integer> array=new ArrayList<>();
                    array.add(k);
                    array.add(m);
                    hash=new HashMap<>();
                    hash.put(cat, array);
                    if(isPass){
                        page=new Page(TypePage.UN, hash);
                    }else{
                        page=new Page(TypePage.TROIS, hash);
                    }
                    
                    //Si c'est la dernière page
                    if(index_categorie==tailleNonZeroMax-1){
                        pages.add(page);
                    }
                }
                
                getPages(index_categorie+1, page);
            }else{
                if(nbreProduitsParCat.get(cat)<=4){
                    int k=indexDep(index_categorie);
                    int m=indexFin(index_categorie);
                    ArrayList<Integer> array=new ArrayList<>();
                    array.add(k);
                    array.add(m);         
                    page.getListeCateg().put(cat, array);
                    pages.add(page);
                    getPages(index_categorie+1, null);
                }else{
                    int k=indexDep(index_categorie);
                    int m=k+4;
                    ArrayList<Integer> array=new ArrayList<>();
                    array.add(k);
                    array.add(m);         
                    page.getListeCateg().put(cat, array);
                    pages.add(page);
                    nbreProduitsParCat.replace(cat, nbreProduitsParCat.get(cat)-4);
                    getPages(index_categorie, null);
                }
            }
        }
    }
    
    private void print(PrinterJob job, PageLayout pageLayout){
        for (Page page : pages) {
            job.printPage(pageLayout, getPane(page));
        }
    }
    
    private int indexDep(int indexCate){
        Categorie cat=allCategorie.get(indexCate);
        int temp=cat.getProduitCollection().size()-nbreProduitsParCat.get(cat);
        return temp;
    }
    
    private int indexFin(int indexCate){
        return allCategorie.get(indexCate).getProduitCollection().size();
    }

    private Node getPane(Page page) {
        List<Categorie> catl=new ArrayList<>();
        if(page.getTypePage().equals(TypePage.FIRST)){
            Categorie cat=page.getListeCateg().keySet().iterator().next();
            catl.add(cat);
            AllCategoriesController.page=page;
            System.out.println("pass");
            return eshop.getCatalogue(catl, page);
        }else{
            for (Iterator<Categorie> iterator = page.getListeCateg().keySet().iterator(); iterator.hasNext();) {
                catl.add(iterator.next());
            }
            return eshop.getCategoriePDF(catl, page);
        }
        
    }
    
    public void removeZeroProduits(){
        List<Produit> produitsARetirer=new ArrayList<>();
        List<Categorie> categoriesARetirer=new ArrayList<>();
        for (Categorie categorie : allCategorie) {
            //recupération des produits fini de categorie
            for (Produit produit : categorie.getProduitCollection()) {
                if(produit.getQte()==0){
                    produitsARetirer.add(produit);
                }
            }
            
            //Suppression des produits en question
            for (Produit prod : produitsARetirer) {
                categorie.getProduitCollection().remove(prod);
            }
            
            produitsARetirer.clear();
        }
        
        for (Categorie categorie : allCategorie) {
            if(categorie.getProduitCollection().size()==0){
                categoriesARetirer.add(categorie);
            }
        }
        
        for (Categorie categorie : categoriesARetirer) {
            allCategorie.remove(categorie);
        }
        
    }
    
    
}
