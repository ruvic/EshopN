/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MBOGNI RUVIC
 */
@Entity
@Table(name = "lignefacture")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lignefacture.findAll", query = "SELECT l FROM Lignefacture l")
    , @NamedQuery(name = "Lignefacture.findByIdLFac", query = "SELECT l FROM Lignefacture l WHERE l.idLFac = :idLFac")
    , @NamedQuery(name = "Lignefacture.findByCodePro", query = "SELECT l FROM Lignefacture l WHERE l.lignefacturePK.codePro = :codePro")
    , @NamedQuery(name = "Lignefacture.findByIdFac", query = "SELECT l FROM Lignefacture l WHERE l.lignefacturePK.idFac = :idFac")
    , @NamedQuery(name = "Lignefacture.findByPrix", query = "SELECT l FROM Lignefacture l WHERE l.prix = :prix")
    , @NamedQuery(name = "Lignefacture.findByQte", query = "SELECT l FROM Lignefacture l WHERE l.qte = :qte")})
public class Lignefacture implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LignefacturePK lignefacturePK;
    @Basic(optional = false)
    @Column(name = "idLFac")
    private int idLFac;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "prix")
    private BigDecimal prix;
    @Basic(optional = false)
    @Column(name = "qte")
    private short qte;
    @JoinColumn(name = "codePro", referencedColumnName = "codePro", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Produit produit;
    @JoinColumn(name = "idFac", referencedColumnName = "idFac", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Facture facture;

    public Lignefacture() {
    }

    public Lignefacture(LignefacturePK lignefacturePK) {
        this.lignefacturePK = lignefacturePK;
    }

    public Lignefacture(LignefacturePK lignefacturePK, BigDecimal prix, short qte, Produit pr, Facture fact) {
        this.lignefacturePK = lignefacturePK;
        this.prix = prix;
        this.qte = qte;   
        this.produit=pr;
        this.facture=fact;
    }

    public Lignefacture(int codePro, int idFac) {
        this.lignefacturePK = new LignefacturePK(codePro, idFac);
    }

    public LignefacturePK getLignefacturePK() {
        return lignefacturePK;
    }

    public void setLignefacturePK(LignefacturePK lignefacturePK) {
        this.lignefacturePK = lignefacturePK;
    }

    public int getIdLFac() {
        return idLFac;
    }

    public void setIdLFac(int idLFac) {
        this.idLFac = idLFac;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public short getQte() {
        return qte;
    }

    public void setQte(short qte) {
        this.qte = qte;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lignefacturePK != null ? lignefacturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lignefacture)) {
            return false;
        }
        Lignefacture other = (Lignefacture) object;
        if ((this.lignefacturePK == null && other.lignefacturePK != null) || (this.lignefacturePK != null && !this.lignefacturePK.equals(other.lignefacturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.Lignefacture[ lignefacturePK=" + lignefacturePK + " ]";
    }
    
}
