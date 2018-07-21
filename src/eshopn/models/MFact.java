/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import eshopn.entities.Produit;
import eshopn.entities.controllers.ProduitJpaController;
/**
 *
 * @author MBOGNI RUVIC
 */
public class MFact {
    private String codeProduit;
    private double prixUnitaire;
    private int qte;
    private double sousTotal;
    private String nomProduit;
    private Produit produit;

    public MFact(String codeProduit, double prixUnitaire, int qte, double sousTotal, Produit pr) {
        this.codeProduit = codeProduit;
        this.prixUnitaire = prixUnitaire;
        this.qte = qte;
        this.sousTotal = sousTotal;
        
        ProduitJpaController cont=new ProduitJpaController(Res.emf);
        Integer id=Integer.valueOf(codeProduit.replace("-", ""));
        this.nomProduit=cont.findProduit(id).getNomPro();
        
        this.produit=pr;
        
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
     * @return the prixUnitaire
     */
    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    /**
     * @param prixUnitaire the prixUnitaire to set
     */
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    /**
     * @return the qte
     */
    public int getQte() {
        return qte;
    }

    /**
     * @param qte the qte to set
     */
    public void setQte(int qte) {
        this.qte = qte;
    }

    /**
     * @return the sousTotal
     */
    public double getSousTotal() {
        return sousTotal;
    }

    /**
     * @param sousTotal the sousTotal to set
     */
    public void setSousTotal(double sousTotal) {
        this.sousTotal = sousTotal;
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
