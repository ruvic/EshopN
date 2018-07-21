/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author MBOGNI RUVIC
 */
@Entity
@Table(name = "gestionnaire")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gestionnaire.findAll", query = "SELECT g FROM Gestionnaire g")
    , @NamedQuery(name = "Gestionnaire.findByIdGest", query = "SELECT g FROM Gestionnaire g WHERE g.idGest = :idGest")
    , @NamedQuery(name = "Gestionnaire.findByNomGest", query = "SELECT g FROM Gestionnaire g WHERE g.nomGest = :nomGest")
    , @NamedQuery(name = "Gestionnaire.findByTypeGest", query = "SELECT g FROM Gestionnaire g WHERE g.typeGest = :typeGest")
    , @NamedQuery(name = "Gestionnaire.findByLogin", query = "SELECT g FROM Gestionnaire g WHERE g.login = :login")
    , @NamedQuery(name = "Gestionnaire.findByPwd", query = "SELECT g FROM Gestionnaire g WHERE g.pwd = :pwd")
    , @NamedQuery(name = "Gestionnaire.findByActif", query = "SELECT g FROM Gestionnaire g WHERE g.actif = :actif")})
public class Gestionnaire implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idGest")
    private Integer idGest;
    @Basic(optional = false)
    @Column(name = "nomGest")
    private String nomGest;
    @Basic(optional = false)
    @Column(name = "typeGest")
    private boolean typeGest;
    @Basic(optional = false)
    @Column(name = "login")
    private String login;
    @Basic(optional = false)
    @Column(name = "pwd")
    private String pwd;
    @Basic(optional = false)
    @Column(name = "actif")
    private boolean actif;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCaissiere")
    private Collection<Facture> factureCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idGest")
    private Collection<Gestionstock> gestionstockCollection;

    public Gestionnaire() {
    }

    public Gestionnaire(Integer idGest) {
        this.idGest = idGest;
    }
    
    public Gestionnaire(String nomGest, boolean typeGest, String login, String pwd, boolean actif) {
        this.nomGest = nomGest;
        this.typeGest = typeGest;
        this.login = login;
        this.pwd = pwd;
        this.actif = actif;
    }
    
    public Gestionnaire(String nomGest, String login, String pwd, boolean actif, boolean typeGest){
        this.nomGest=nomGest;
        this.login=login;
        this.pwd=pwd;
        this.actif=actif;
        this.typeGest=typeGest;
    }

    public Integer getIdGest() {
        return idGest;
    }

    public void setIdGest(Integer idGest) {
        this.idGest = idGest;
    }

    public String getNomGest() {
        return nomGest;
    }

    public void setNomGest(String nomGest) {
        this.nomGest = nomGest;
    }

    public boolean getTypeGest() {
        return typeGest;
    }

    public void setTypeGest(boolean typeGest) {
        this.typeGest = typeGest;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean getActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @XmlTransient
    public Collection<Facture> getFactureCollection() {
        return factureCollection;
    }

    public void setFactureCollection(Collection<Facture> factureCollection) {
        this.factureCollection = factureCollection;
    }

    @XmlTransient
    public Collection<Gestionstock> getGestionstockCollection() {
        return gestionstockCollection;
    }

    public void setGestionstockCollection(Collection<Gestionstock> gestionstockCollection) {
        this.gestionstockCollection = gestionstockCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGest != null ? idGest.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gestionnaire)) {
            return false;
        }
        Gestionnaire other = (Gestionnaire) object;
        if ((this.idGest == null && other.idGest != null) || (this.idGest != null && !this.idGest.equals(other.idGest))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.Gestionnaire[ idGest=" + idGest + " ]";
    }
    
}
