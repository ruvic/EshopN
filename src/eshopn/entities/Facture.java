/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author MBOGNI RUVIC
 */
@Entity
@Table(name = "facture")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Facture.findAll", query = "SELECT f FROM Facture f")
    , @NamedQuery(name = "Facture.findByIdFac", query = "SELECT f FROM Facture f WHERE f.idFac = :idFac")
    , @NamedQuery(name = "Facture.findByDateFac", query = "SELECT f FROM Facture f WHERE f.dateFac = :dateFac")
    , @NamedQuery(name = "Facture.findByRemise", query = "SELECT f FROM Facture f WHERE f.remise = :remise")
    , @NamedQuery(name = "Facture.findByMontant", query = "SELECT f FROM Facture f WHERE f.montant = :montant")
    , @NamedQuery(name = "Facture.findByTel", query = "SELECT f FROM Facture f WHERE f.tel = :tel")
    , @NamedQuery(name = "Facture.findByTypeFac", query = "SELECT f FROM Facture f WHERE f.typeFac = :typeFac")})
public class Facture implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idFac")
    private Integer idFac;
    @Basic(optional = false)
    @Column(name = "dateFac")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFac;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "remise")
    private BigDecimal remise;
    @Basic(optional = false)
    @Column(name = "montant")
    private BigDecimal montant;
    @Column(name = "tel")
    private String tel;
    @Basic(optional = false)
    @Column(name = "typeFac")
    private boolean typeFac;
    @JoinColumn(name = "idCaissiere", referencedColumnName = "idGest")
    @ManyToOne(optional = false)
    private Gestionnaire idCaissiere;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facture")
    private Collection<Lignefacture> lignefactureCollection;

    public Facture() {
    }

    public Facture(Integer idFac) {
        this.idFac = idFac;
    }

    public Facture(Date dateFac, 
            BigDecimal remise, BigDecimal montant,
            String tel ,boolean typeFac, Gestionnaire idCaiss) {
        this.dateFac = dateFac;
        this.remise = remise;
        this.montant = montant;
        this.typeFac = typeFac;
        this.tel=tel;
        this.idCaissiere=idCaiss;
    }
    
    public Facture(Integer idFac, Date dateFac, BigDecimal remise, BigDecimal montant, boolean typeFac) {
        this.idFac = idFac;
        this.dateFac = dateFac;
        this.remise = remise;
        this.montant = montant;
        this.typeFac = typeFac;
    }

    public Integer getIdFac() {
        return idFac;
    }

    public void setIdFac(Integer idFac) {
        this.idFac = idFac;
    }

    public Date getDateFac() {
        return dateFac;
    }

    public void setDateFac(Date dateFac) {
        this.dateFac = dateFac;
    }

    public BigDecimal getRemise() {
        return remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean getTypeFac() {
        return typeFac;
    }

    public void setTypeFac(boolean typeFac) {
        this.typeFac = typeFac;
    }

    public Gestionnaire getIdCaissiere() {
        return idCaissiere;
    }

    public void setIdCaissiere(Gestionnaire idCaissiere) {
        this.idCaissiere = idCaissiere;
    }

    @XmlTransient
    public Collection<Lignefacture> getLignefactureCollection() {
        return lignefactureCollection;
    }

    public void setLignefactureCollection(Collection<Lignefacture> lignefactureCollection) {
        this.lignefactureCollection = lignefactureCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFac != null ? idFac.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Facture)) {
            return false;
        }
        Facture other = (Facture) object;
        if ((this.idFac == null && other.idFac != null) || (this.idFac != null && !this.idFac.equals(other.idFac))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.Facture[ idFac=" + idFac + " ]";
    }
    
}
