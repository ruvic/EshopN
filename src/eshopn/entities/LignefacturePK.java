/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author MBOGNI RUVIC
 */
@Embeddable
public class LignefacturePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "codePro")
    private int codePro;
    @Basic(optional = false)
    @Column(name = "idFac")
    private int idFac;

    public LignefacturePK() {
    }

    public LignefacturePK(int codePro, int idFac) {
        this.codePro = codePro;
        this.idFac = idFac;
    }

    public int getCodePro() {
        return codePro;
    }

    public void setCodePro(int codePro) {
        this.codePro = codePro;
    }

    public int getIdFac() {
        return idFac;
    }

    public void setIdFac(int idFac) {
        this.idFac = idFac;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) codePro;
        hash += (int) idFac;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LignefacturePK)) {
            return false;
        }
        LignefacturePK other = (LignefacturePK) object;
        if (this.codePro != other.codePro) {
            return false;
        }
        if (this.idFac != other.idFac) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.LignefacturePK[ codePro=" + codePro + ", idFac=" + idFac + " ]";
    }
    
}
