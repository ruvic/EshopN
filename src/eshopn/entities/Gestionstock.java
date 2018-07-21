/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MBOGNI RUVIC
 */
@Entity
@Table(name = "gestionstock")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gestionstock.findAll", query = "SELECT g FROM Gestionstock g")
    , @NamedQuery(name = "Gestionstock.findByIdStock", query = "SELECT g FROM Gestionstock g WHERE g.idStock = :idStock")
    , @NamedQuery(name = "Gestionstock.findByQte", query = "SELECT g FROM Gestionstock g WHERE g.qte = :qte")
    , @NamedQuery(name = "Gestionstock.findByDateStock", query = "SELECT g FROM Gestionstock g WHERE g.dateStock = :dateStock")
    , @NamedQuery(name = "Gestionstock.findByIdGest", query = "SELECT g FROM Gestionstock g WHERE g.idGest = :idGest")
    , @NamedQuery(name = "Gestionstock.findByOperation", query = "SELECT g FROM Gestionstock g WHERE g.operation = :operation")})
public class Gestionstock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idStock")
    private Integer idStock;
    @Basic(optional = false)
    @Column(name = "qte")
    private int qte;
    @Basic(optional = false)
    @Column(name = "dateStock")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStock;
    @Basic(optional = false)
    @Column(name = "operation")
    private boolean operation;
    @JoinColumn(name = "codePro", referencedColumnName = "codePro")
    @ManyToOne(optional = false)
    private Produit codePro;
    @JoinColumn(name = "idGest", referencedColumnName = "idGest")
    @ManyToOne(optional = false)
    private Gestionnaire idGest;

    public Gestionstock() {
    }

    public Gestionstock(Integer idStock) {
        this.idStock = idStock;
    }

    public Gestionstock(Integer idStock, int qte, Date dateStock, boolean operation) {
        this.idStock = idStock;
        this.qte = qte;
        this.dateStock = dateStock;
        this.operation = operation;
    }
    
    public Gestionstock(int qte, Date dateStock,Gestionnaire idGest,Produit pro ,boolean operation) {
        this.qte = qte;
        this.dateStock = dateStock;
        this.operation = operation;
        this.idGest=idGest;
        this.codePro=pro;
    }

    public Integer getIdStock() {
        return idStock;
    }

    public void setIdStock(Integer idStock) {
        this.idStock = idStock;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public Date getDateStock() {
        return dateStock;
    }

    public void setDateStock(Date dateStock) {
        this.dateStock = dateStock;
    }

    public boolean getOperation() {
        return operation;
    }

    public void setOperation(boolean operation) {
        this.operation = operation;
    }

    public Produit getCodePro() {
        return codePro;
    }

    public void setCodePro(Produit codePro) {
        this.codePro = codePro;
    }

    public Gestionnaire getIdGest() {
        return idGest;
    }

    public void setIdGest(Gestionnaire idGest) {
        this.idGest = idGest;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idStock != null ? idStock.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gestionstock)) {
            return false;
        }
        Gestionstock other = (Gestionstock) object;
        if ((this.idStock == null && other.idStock != null) || (this.idStock != null && !this.idStock.equals(other.idStock))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshopn.entities.Gestionstock[ idStock=" + idStock + " ]";
    }
    
}
