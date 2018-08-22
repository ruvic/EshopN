/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.controllers;

import eshopn.EShopN;
import eshopn.entities.Photo;
import eshopn.models.Res;
import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author MBOGNI RUVIC
 */
public abstract class Controllers {
    private Stage stage;
    private Scene scene;
    private EShopN main;

    
    public abstract void init();
    protected abstract void setImagesToImageViews();
    
    public String formatCode(String code){
        String temp=String.valueOf(code);
        return temp.substring(0,3)+"-"+temp.substring(3,6);
    }
    
    public String lienAbsolueImage(Photo ph){
        String str=  Res.config.getDossierPhotos()
                + ph.getCodePro().getCodePro()
                + "/"+ph.getLienPhoto();
        return str;
    }
    
    public void loadImage(String code, String nom , ImageView imageView) throws MalformedURLException{
       
        File file=new File(Res.config.getDossierImagesLocal()+code+"/"+nom);
        imageView.setImage(new Image(file.toURI().toURL().toExternalForm()));
    }
    
    public String year(Date date){
        return Res.sdf.format(date).toString().split("-")[0].toLowerCase().trim();
    }
    
    public String month(Date date){
        return Res.sdf.format(date).toString().split("-")[1].toLowerCase().trim();
    }
    
    public String day(Date date){
        return Res.sdf.format(date).toString().split("-")[2].toLowerCase().substring(0, 2).trim();
    }
    
    public int numberOfDaysForMonth(int year, int month){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.MONTH, month-1);
//        return calendar.getActualMaximum(Calendar.DATE);
        
        LocalDate date = LocalDate.of(year, month, 1);
        return date.lengthOfMonth();
    }
    
    public void showDatasOnTableView(ObservableList list,Pagination pagination, TableView tableV, int itermPerPage){
        int itemPerPage=itermPerPage;
        int pages, nbreElts=list.size();
        
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
                if (pageIndex > list.size() / itemPerPage + 1) {
                    return null;
                } else {
                    return createPage(pageIndex,itemPerPage,tableV,list);
                }
            }            
        });
    }
    
    private Node createPage(Integer pageIndex, int itemPerPage,TableView tableV, ObservableList list) {
        tableV.setItems(FXCollections.observableArrayList(getData(pageIndex,itemPerPage, list)));
        return new AnchorPane();
    }
    
    public ArrayList getData(int pageIndex,int itemPerPage ,ObservableList list){
        int lastIndex = 0;
        int displace = list.size() % itemPerPage;
        if (displace > 0) {
            lastIndex = list.size() / itemPerPage + 1;
        } else {
            lastIndex = (list.size() / itemPerPage) ;
        }

        if (lastIndex == pageIndex) {
            ArrayList tab = new ArrayList<>();
            for(int i = pageIndex * itemPerPage; i < pageIndex * itemPerPage + displace; i++){
                if( i < list.size()) tab.add(list.get(i));
            }
            
            return tab;
        } else {
            ArrayList tab = new ArrayList<>();
            for(int i = pageIndex * itemPerPage; i < pageIndex * itemPerPage + itemPerPage; i++){
                if( i < list.size()) tab.add(list.get(i));
            }
            
            return tab;
        }
    }
    
    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public EShopN getMain() {
        return main;
    }

    public void setMain(EShopN main) {
        this.main = main;
    }
    
    
    
    
    
    
    
    
}
