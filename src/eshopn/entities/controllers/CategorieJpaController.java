/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities.controllers;

import eshopn.entities.Categorie;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eshopn.entities.Produit;
import eshopn.entities.controllers.exceptions.IllegalOrphanException;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class CategorieJpaController implements Serializable {

    public CategorieJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categorie categorie) {
        if (categorie.getProduitCollection() == null) {
            categorie.setProduitCollection(new ArrayList<Produit>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Produit> attachedProduitCollection = new ArrayList<Produit>();
            for (Produit produitCollectionProduitToAttach : categorie.getProduitCollection()) {
                produitCollectionProduitToAttach = em.getReference(produitCollectionProduitToAttach.getClass(), produitCollectionProduitToAttach.getCodePro());
                attachedProduitCollection.add(produitCollectionProduitToAttach);
            }
            categorie.setProduitCollection(attachedProduitCollection);
            em.persist(categorie);
            for (Produit produitCollectionProduit : categorie.getProduitCollection()) {
                Categorie oldIdCategorieOfProduitCollectionProduit = produitCollectionProduit.getIdCategorie();
                produitCollectionProduit.setIdCategorie(categorie);
                produitCollectionProduit = em.merge(produitCollectionProduit);
                if (oldIdCategorieOfProduitCollectionProduit != null) {
                    oldIdCategorieOfProduitCollectionProduit.getProduitCollection().remove(produitCollectionProduit);
                    oldIdCategorieOfProduitCollectionProduit = em.merge(oldIdCategorieOfProduitCollectionProduit);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categorie categorie) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categorie persistentCategorie = em.find(Categorie.class, categorie.getIdCat());
            Collection<Produit> produitCollectionOld = persistentCategorie.getProduitCollection();
            Collection<Produit> produitCollectionNew = categorie.getProduitCollection();
            List<String> illegalOrphanMessages = null;
            for (Produit produitCollectionOldProduit : produitCollectionOld) {
                if (!produitCollectionNew.contains(produitCollectionOldProduit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Produit " + produitCollectionOldProduit + " since its idCategorie field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Produit> attachedProduitCollectionNew = new ArrayList<Produit>();
            for (Produit produitCollectionNewProduitToAttach : produitCollectionNew) {
                produitCollectionNewProduitToAttach = em.getReference(produitCollectionNewProduitToAttach.getClass(), produitCollectionNewProduitToAttach.getCodePro());
                attachedProduitCollectionNew.add(produitCollectionNewProduitToAttach);
            }
            produitCollectionNew = attachedProduitCollectionNew;
            categorie.setProduitCollection(produitCollectionNew);
            categorie = em.merge(categorie);
            for (Produit produitCollectionNewProduit : produitCollectionNew) {
                if (!produitCollectionOld.contains(produitCollectionNewProduit)) {
                    Categorie oldIdCategorieOfProduitCollectionNewProduit = produitCollectionNewProduit.getIdCategorie();
                    produitCollectionNewProduit.setIdCategorie(categorie);
                    produitCollectionNewProduit = em.merge(produitCollectionNewProduit);
                    if (oldIdCategorieOfProduitCollectionNewProduit != null && !oldIdCategorieOfProduitCollectionNewProduit.equals(categorie)) {
                        oldIdCategorieOfProduitCollectionNewProduit.getProduitCollection().remove(produitCollectionNewProduit);
                        oldIdCategorieOfProduitCollectionNewProduit = em.merge(oldIdCategorieOfProduitCollectionNewProduit);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categorie.getIdCat();
                if (findCategorie(id) == null) {
                    throw new NonexistentEntityException("The categorie with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categorie categorie;
            try {
                categorie = em.getReference(Categorie.class, id);
                categorie.getIdCat();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categorie with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Produit> produitCollectionOrphanCheck = categorie.getProduitCollection();
            for (Produit produitCollectionOrphanCheckProduit : produitCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Categorie (" + categorie + ") cannot be destroyed since the Produit " + produitCollectionOrphanCheckProduit + " in its produitCollection field has a non-nullable idCategorie field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(categorie);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categorie> findCategorieEntities() {
        return findCategorieEntities(true, -1, -1);
    }
   
    public List<Categorie> findCategorieEntities(int maxResults, int firstResult) {
        return findCategorieEntities(false, maxResults, firstResult);
    }

    private List<Categorie> findCategorieEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categorie.class));
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

    public Categorie findCategorie(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categorie.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategorieCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categorie> rt = cq.from(Categorie.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
