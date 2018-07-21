/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "photo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Photo.findAll", query = "SELECT p FROM Photo p")
    , @NamedQuery(name = "Photo.findByIdPhoto", query = "SELECT p FROM Photo p WHERE p.idPhoto = :idPhoto")
    , @NamedQuery(name = "Photo.findByCodeProduit", query = "SELECT p FROM Photo p WHERE p.codePro = :codePro")
    , @NamedQuery(name = "Photo.findByLienPhoto", query = "SELECT p FROM Photo p WHERE p.lienPhoto = :lienPhoto")})
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPhoto")
    private Integer idPhoto;
    @Basic(optional = false)
    @Column(name = "lienPhoto")
    private String lienPhoto;
    @JoinColumn(name = "codePro", referencedColumnName = "codePro")
    @ManyToOne(optional = false)
    private Produit codePro;

    public Photo() {
    }

    public Photo(Integer idPhoto) {
        this.idPhoto = idPhoto;
    }

    public Photo(Integer idPhoto, String lienPhoto) {
        this.idPhoto = idPhoto;
        this.lienPhoto = lienPhoto;
    }
    
    public Photo(String lienPhoto, Produit codePro) {
        this.lienPhoto = lienPhoto;
        this.codePro=codePro;
    }

    public Integer getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(Integer idPhoto) {
        this.idPhoto = idPhoto;
    }

    public String getLienPhoto() {
        return lienPhoto;
    }

    public void setLienPhoto(String lienPhoto) {
        this.lienPhoto = lienPhoto;
    }

    public Produit getCodePro() {
        return codePro;
    }

    public void setCodePro(Produit codePro) {
        this.codePro = codePro;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPhoto != null ? idPhoto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Photo)) {
            return false;
        }
        Photo other = (Photo) object;
        if ((this.idPhoto == null && other.idPhoto != null) || (this.idPhoto != null && !this.idPhoto.equals(other.idPhoto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.Photo[ idPhoto=" + idPhoto + " ]";
    }
    
}
