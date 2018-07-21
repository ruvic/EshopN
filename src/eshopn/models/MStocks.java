/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import eshopn.entities.Gestionstock;
import eshopn.entities.Produit;


/**
 *
 * @author MBOGNI RUVIC
 */
public class MStocks {
    private String nomProduit;
    private String codeProduit;
    private String gestionnaire;
    private String quantite;
    private String categorie;
    private String actions;
    private String date;
    private Produit produit;
    
    
    public MStocks(Gestionstock stock){
        
        nomProduit=stock.getCodePro().getNomPro();
        codeProduit=stock.getCodePro().getCodePro()+"";
        gestionnaire=stock.getIdGest().getNomGest();
        codeProduit=codeProduit.substring(0,3)+"-"+codeProduit.substring(3, 6);
        quantite=stock.getQte()+"";
        categorie=stock.getCodePro().getIdCategorie().getNomCat();
        actions=((stock.getOperation())?"Ajout":"Retrait");
        date=Res.sdf.format(stock.getDateStock());
        produit=stock.getCodePro();
    }
   
    

    /**
     * @return the nomProduit
     */
    public String getNomProduit() {
        return nomProduit;
    }

    /**
     * @param nomProduit the nomProduit to set
     */
    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    /**
     * @return the codeProduit
     */
    public String getCodeProduit() {
        return codeProduit;
    }

    /**
     * @param codeProduit the codeProduit to set
     */
    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }

    /**
     * @return the quantite
     */
    public String getQuantite() {
        return quantite;
    }

    /**
     * @param quantite the quantite to set
     */
    public void setQuantite(String quantite) {
        this.quantite = quantite;
    }

    /**
     * @return the categorie
     */
    public String getCategorie() {
        return categorie;
    }

    /**
     * @param categorie the categorie to set
     */
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    /**
     * @return the actions
     */
    public String getActions() {
        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(String actions) {
        this.actions = actions;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the gestionnaire
     */
    public String getGestionnaire() {
        return gestionnaire;
    }

    /**
     * @param gestionnaire the gestionnaire to set
     */
    public void setGestionnaire(String gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    /**
     * @return the produit
     */
    public Produit getProduit() {
        return produit;
    }

    /**
     * @param produit the produit to set
     */
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    
}
