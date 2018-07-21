/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eshopn.entities.Produit;
import eshopn.entities.Facture;
import eshopn.entities.Lignefacture;
import eshopn.entities.LignefacturePK;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import eshopn.entities.controllers.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class LignefactureJpaController implements Serializable {

    public LignefactureJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Lignefacture lignefacture) throws PreexistingEntityException, Exception {
        if (lignefacture.getLignefacturePK() == null) {
            lignefacture.setLignefacturePK(new LignefacturePK());
        }
        lignefacture.getLignefacturePK().setIdFac(lignefacture.getFacture().getIdFac());
        lignefacture.getLignefacturePK().setCodePro(lignefacture.getProduit().getCodePro());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit produit = lignefacture.getProduit();
            if (produit != null) {
                produit = em.getReference(produit.getClass(), produit.getCodePro());
                lignefacture.setProduit(produit);
            }
            Facture facture = lignefacture.getFacture();
            if (facture != null) {
                facture = em.getReference(facture.getClass(), facture.getIdFac());
                lignefacture.setFacture(facture);
            }
            em.persist(lignefacture);
            if (produit != null) {
                produit.getLignefactureCollection().add(lignefacture);
                produit = em.merge(produit);
            }
            if (facture != null) {
                facture.getLignefactureCollection().add(lignefacture);
                facture = em.merge(facture);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLignefacture(lignefacture.getLignefacturePK()) != null) {
                throw new PreexistingEntityException("Lignefacture " + lignefacture + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Lignefacture lignefacture) throws NonexistentEntityException, Exception {
        lignefacture.getLignefacturePK().setIdFac(lignefacture.getFacture().getIdFac());
        lignefacture.getLignefacturePK().setCodePro(lignefacture.getProduit().getCodePro());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Lignefacture persistentLignefacture = em.find(Lignefacture.class, lignefacture.getLignefacturePK());
            Produit produitOld = persistentLignefacture.getProduit();
            Produit produitNew = lignefacture.getProduit();
            Facture factureOld = persistentLignefacture.getFacture();
            Facture factureNew = lignefacture.getFacture();
            if (produitNew != null) {
                produitNew = em.getReference(produitNew.getClass(), produitNew.getCodePro());
                lignefacture.setProduit(produitNew);
            }
            if (factureNew != null) {
                factureNew = em.getReference(factureNew.getClass(), factureNew.getIdFac());
                lignefacture.setFacture(factureNew);
            }
            lignefacture = em.merge(lignefacture);
            if (produitOld != null && !produitOld.equals(produitNew)) {
                produitOld.getLignefactureCollection().remove(lignefacture);
                produitOld = em.merge(produitOld);
            }
            if (produitNew != null && !produitNew.equals(produitOld)) {
                produitNew.getLignefactureCollection().add(lignefacture);
                produitNew = em.merge(produitNew);
            }
            if (factureOld != null && !factureOld.equals(factureNew)) {
                factureOld.getLignefactureCollection().remove(lignefacture);
                factureOld = em.merge(factureOld);
            }
            if (factureNew != null && !factureNew.equals(factureOld)) {
                factureNew.getLignefactureCollection().add(lignefacture);
                factureNew = em.merge(factureNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                LignefacturePK id = lignefacture.getLignefacturePK();
                if (findLignefacture(id) == null) {
                    throw new NonexistentEntityException("The lignefacture with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(LignefacturePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Lignefacture lignefacture;
            try {
                lignefacture = em.getReference(Lignefacture.class, id);
                lignefacture.getLignefacturePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lignefacture with id " + id + " no longer exists.", enfe);
            }
            Produit produit = lignefacture.getProduit();
            if (produit != null) {
                produit.getLignefactureCollection().remove(lignefacture);
                produit = em.merge(produit);
            }
            Facture facture = lignefacture.getFacture();
            if (facture != null) {
                facture.getLignefactureCollection().remove(lignefacture);
                facture = em.merge(facture);
            }
            em.remove(lignefacture);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Lignefacture> findLignefactureEntities() {
        return findLignefactureEntities(true, -1, -1);
    }
    
    public List<Lignefacture> findLignefactureEntities(boolean order) {
        if(!order) return  findLignefactureEntities();
        else{
            EntityManager em=getEntityManager();
            List<Lignefacture> list=null;
            try {
                em.getTransaction().begin();
                list=em.createQuery(
                        "SELECT g FROM Lignefacture g ORDER BY g.idLFac DESC")
                        .getResultList();
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    }
    
    
    public List<Lignefacture> findLignefactureEntities(int maxResults, int firstResult) {
        return findLignefactureEntities(false, maxResults, firstResult);
    }
    
    public List<Lignefacture> findLignefactureEntities(Facture fact){
        EntityManager em=getEntityManager();
        List<Lignefacture> listFact;
        try {
            em.getTransaction().begin();
            listFact=em.createNamedQuery("Lignefacture.findByIdFac", Lignefacture.class)
                    .setParameter("idFac", fact.getIdFac())
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            listFact=null;
        }
        return listFact;
        
    }
    
    private List<Lignefacture> findLignefactureEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Lignefacture.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Lignefacture findLignefacture(LignefacturePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Lignefacture.class, id);
        } finally {
            em.close();
        }
    }

    public int getLignefactureCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Lignefacture> rt = cq.from(Lignefacture.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
