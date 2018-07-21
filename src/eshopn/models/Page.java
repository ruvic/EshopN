/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import eshopn.entities.Categorie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author MBOGNI RUVIC
 */
public class Page {
    private TypePage typePage;
    private HashMap<Categorie,ArrayList> listeCateg;
    private int pageNumber;

    public Page(TypePage typePage, HashMap<Categorie, ArrayList> listeCateg) {
        this.typePage = typePage;
        this.listeCateg = listeCateg;
        this.pageNumber=pageNumber;
    }
    
    public String toString(){
        String chaine=typePage+" => ";
        Iterator<Categorie> all=listeCateg.keySet().iterator();
        Categorie cat1=all.next();
        chaine+=cat1.getNomCat()+" ("+listeCateg.get(cat1).get(0)+","+listeCateg.get(cat1).get(1)+") \n";
        if(all.hasNext()){
            Categorie cat2=all.next();
            chaine+=cat2.getNomCat()+" ("+listeCateg.get(cat2).get(0)+","+listeCateg.get(cat2).get(1)+") \n";
        }
        return chaine;
    }

    /**
     * @return the typePage
     */
    public TypePage getTypePage() {
        return typePage;
    }

    /**
     * @param typePage the typePage to set
     */
    public void setTypePage(TypePage typePage) {
        this.typePage = typePage;
    }

    /**
     * @return the listeCateg
     */
    public HashMap<Categorie,ArrayList> getListeCateg() {
        return listeCateg;
    }

    /**
     * @param listeCateg the listeCateg to set
     */
    public void setListeCateg(HashMap<Categorie,ArrayList> listeCateg) {
        this.listeCateg = listeCateg;
    }

    /**
     * @return the pageNumber
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * @param pageNumber the pageNumber to set
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    
    
    
}
