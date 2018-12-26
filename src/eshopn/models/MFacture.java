/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import eshopn.entities.Facture;
import java.util.Formatter;

/**
 *
 * @author MBOGNI RUVIC
 */
public class MFacture {
    private String numFact;
    private String montant;
    private String montNAP;
    private String date;
    private String remise;
    private String nomCaissier;
    private String tel;
    private String typeFact;
    private Facture facture;
   

    public MFacture(Facture fact) {
        this.numFact=fact.getIdFac().intValue()+"";
        this.montant=Res.formatNumber(fact.getMontant().doubleValue());
        this.montNAP=Res.formatNumber(fact.getMontant().doubleValue()*(1-(fact.getRemise().doubleValue()/100)));
        this.date=Res.sdf.format(fact.getDateFac());
        this.remise=fact.getRemise().toString();
        this.nomCaissier=fact.getIdCaissiere().getNomGest();
        this.tel=fact.getTel();
        this.typeFact=(fact.getTypeFac())?"Cash":"EMoney";
        this.facture=fact;
    }
    
    public String formatNumber(int numb){
        Formatter fmt = new Formatter();
        return fmt.format("%010d", numb).toString();
    }

    /**
     * @return the numFact
     */
    public String getNumFact() {
        return numFact;
    }

    /**
     * @param numFact the numFact to set
     */
    public void setNumFact(String numFact) {
        this.numFact = numFact;
    }

    /**
     * @return the montant
     */
    public String getMontant() {
        return montant;
    }

    /**
     * @param montant the montant to set
     */
    public void setMontant(String montant) {
        this.montant = montant;
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
     * @return the remise
     */
    public String getRemise() {
        return remise;
    }

    /**
     * @param remise the remise to set
     */
    public void setRemise(String remise) {
        this.remise = remise;
    }

    /**
     * @return the nomCaissier
     */
    public String getNomCaissier() {
        return nomCaissier;
    }

    /**
     * @param nomCaissier the nomCaissier to set
     */
    public void setNomCaissier(String nomCaissier) {
        this.nomCaissier = nomCaissier;
    }

    /**
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * @param tel the tel to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * @return the typeFact
     */
    public String getTypeFact() {
        return typeFact;
    }

    /**
     * @param typeFact the typeFact to set
     */
    public void setTypeFact(String typeFact) {
        this.typeFact = typeFact;
    }

    /**
     * @return the facture
     */
    public Facture getFacture() {
        return facture;
    }

    /**
     * @param facture the facture to set
     */
    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    /**
     * @return the montNAP
     */
    public String getMontNAP() {
        return montNAP;
    }

    /**
     * @param montNAP the montNAP to set
     */
    public void setMontNAP(String montNAP) {
        this.montNAP = montNAP;
    }

    
    
    
    
}
