/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.models;

import eshopn.entities.Lignefacture;
import java.util.List;

/**
 *
 * @author MBOGNI RUVIC
 */
public class MFactProd {
    private Integer numFact;
    private Integer qte;
    private Integer codePro;

    public MFactProd(Lignefacture ligne) {
        this.numFact=ligne.getFacture().getIdFac();
        this.qte=Integer.valueOf(ligne.getQte());
        this.codePro=ligne.getProduit().getCodePro();
    }

    /**
     * @return the numFact
     */
    public Integer getNumFact() {
        return numFact;
    }

    /**
     * @param numFact the numFact to set
     */
    public void setNumFact(Integer numFact) {
        this.numFact = numFact;
    }

    /**
     * @return the qte
     */
    public Integer getQte() {
        return qte;
    }

    /**
     * @param qte the qte to set
     */
    public void setQte(Integer qte) {
        this.qte = qte;
    }

    /**
     * @return the codePro
     */
    public Integer getCodePro() {
        return codePro;
    }

    /**
     * @param codePro the codePro to set
     */
    public void setCodePro(Integer codePro) {
        this.codePro = codePro;
    }
    
    
}
