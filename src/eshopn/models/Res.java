/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import eshopn.entities.Gestionnaire;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class Res {
    public static EshopConfigurations config;
    public static EntityManagerFactory emf;
    public static Gestionnaire connected_storekeeper;
    public static GlobalNotifications not=new GlobalNotifications();
    public static StackPane stackPane;
    public static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd à HH:mm:ss");
    
    public static GridPane listeCat;
    public static GridPane listeProduit;
    public static AnchorPane content;
    private static int row_index=0;
    private static int column_index=0;
    public static int NbreEltParLigne=4;
    public static int itermPerPage=12;
    public static String lastBrowsedFolder="";
    
    
    public static int getIndexLine(){
        int new_val=row_index;
        if(column_index==NbreEltParLigne){
            row_index++;
            column_index=0;
        }
        return new_val;
    }
    
    public static int getColumnIndex(){
        int new_val=column_index;
        if(column_index==NbreEltParLigne){
            column_index=0;
        }else{
            column_index++;
        }
        
        return new_val;
    }
    
    public static void reset(){
        row_index=0;
        column_index=0;
    }
    
    public static String formatNumber(double prix){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(2);
        
        return  df.format(prix);
    }
    
    public static String formatCode(String code){
        String temp=String.valueOf(code);
        return temp.substring(0,3)+"-"+temp.substring(3,6);
    }
}
