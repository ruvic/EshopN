/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "produit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Produit.findAll", query = "SELECT p FROM Produit p")
    , @NamedQuery(name = "Produit.findByCodePro", query = "SELECT p FROM Produit p WHERE p.codePro = :codePro")
    , @NamedQuery(name = "Produit.findByNomPro", query = "SELECT p FROM Produit p WHERE p.nomPro = :nomPro")
    , @NamedQuery(name = "Produit.findByPrix", query = "SELECT p FROM Produit p WHERE p.prix = :prix")
    , @NamedQuery(name = "Produit.findByQte", query = "SELECT p FROM Produit p WHERE p.qte = :qte")
    , @NamedQuery(name = "Produit.findByDescription", query = "SELECT p FROM Produit p WHERE p.description = :description")
    , @NamedQuery(name = "Produit.findByCodeFour", query = "SELECT p FROM Produit p WHERE p.codeFour = :codeFour")
    , @NamedQuery(name = "Produit.findByIdCat", query = "SELECT p FROM Produit p WHERE p.idCategorie = :idCategorie")
    , @NamedQuery(name = "Produit.findByActif", query = "SELECT p FROM Produit p WHERE p.actif = :actif")})
public class Produit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codePro")
    private Integer codePro;
    @Basic(optional = false)
    @Column(name = "nomPro")
    private String nomPro;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "prix")
    private BigDecimal prix;
    @Basic(optional = false)
    @Column(name = "qte")
    private int qte;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "codeFour")
    private String codeFour;
    @Basic(optional = false)
    @Column(name = "actif")
    private boolean actif;
    @JoinColumn(name = "idCategorie", referencedColumnName = "idCat")
    @ManyToOne(optional = false)
    private Categorie idCategorie;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codePro")
    private Collection<Photo> photoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codePro")
    private Collection<Gestionstock> gestionstockCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produit")
    private Collection<Lignefacture> lignefactureCollection;

    public Produit() {
    }

    public Produit(Integer codePro) {
        this.codePro = codePro;
    }

    public Produit(Integer codePro, String nomPro, BigDecimal prix, int qte, String description, String codeFour,Categorie cate ,  boolean actif) {
        this.codePro = codePro;
        this.nomPro = nomPro;
        this.prix = prix;
        this.qte = qte;
        this.description = description;
        this.codeFour = codeFour;
        this.actif = actif;
        this.idCategorie=cate;
    }
    public Produit(String nomPro, BigDecimal prix, int qte, String description, String codeFour,Categorie cate ,  boolean actif) {
        this.nomPro = nomPro;
        this.prix = prix;
        this.qte = qte;
        this.description = description;
        this.codeFour = codeFour;
        this.actif = actif;
        this.idCategorie=cate;
    }


    public Integer getCodePro() {
        return codePro;
    }

    public void setCodePro(Integer codePro) {
        this.codePro = codePro;
    }

    public String getNomPro() {
        return nomPro;
    }

    public void setNomPro(String nomPro) {
        this.nomPro = nomPro;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCodeFour() {
        return codeFour;
    }

    public void setCodeFour(String codeFour) {
        this.codeFour = codeFour;
    }

    public boolean getActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Categorie getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(Categorie idCategorie) {
        this.idCategorie = idCategorie;
    }

    @XmlTransient
    public Collection<Photo> getPhotoCollection() {
        return photoCollection;
    }

    public void setPhotoCollection(Collection<Photo> photoCollection) {
        this.photoCollection = photoCollection;
    }

    @XmlTransient
    public Collection<Gestionstock> getGestionstockCollection() {
        return gestionstockCollection;
    }

    public void setGestionstockCollection(Collection<Gestionstock> gestionstockCollection) {
        this.gestionstockCollection = gestionstockCollection;
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
        hash += (codePro != null ? codePro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Produit)) {
            return false;
        }
        Produit other = (Produit) object;
        if ((this.codePro == null && other.codePro != null) || (this.codePro != null && !this.codePro.equals(other.codePro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.Produit[ codePro=" + codePro + " ]";
    }
    
}
