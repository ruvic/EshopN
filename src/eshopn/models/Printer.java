/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 *
 * @author MBOGNI RUVIC
 */
public abstract  class Printer {
    
    private String[] tab = new String[]{"a","b","c","d","e","f","g","h","i","j","k"
                             ,"l","m","n","o","p","q","r","s","t","u"
                             ,"v","w","x","y","z","0","1","2","3","4"
                             ,"5","6","7","8","9","-"};
    
    
    public String generateName(){
        String nomPdf = "";
        for(int i = 0; i < 16; i++){
            int rand = (int) (Math.random()*(tab.length));
            nomPdf += tab[rand];
        }
        Date actuelle = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        String dat = dateFormat.format(actuelle);
        nomPdf += dat+".pdf";
        nomPdf = nomPdf.replace('\\', '/');
        return nomPdf;
    }
    
    public String formatNumber(int numb){
        Formatter fmt = new Formatter();
        return fmt.format("%010d", numb).toString();
    }
    
}
