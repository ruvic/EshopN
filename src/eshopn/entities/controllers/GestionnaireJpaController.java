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
import eshopn.entities.Facture;
import eshopn.entities.Gestionnaire;
import java.util.ArrayList;
import java.util.Collection;
import eshopn.entities.Gestionstock;
import eshopn.entities.controllers.exceptions.IllegalOrphanException;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class GestionnaireJpaController implements Serializable {

    public GestionnaireJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Gestionnaire gestionnaire) {
        if (gestionnaire.getFactureCollection() == null) {
            gestionnaire.setFactureCollection(new ArrayList<Facture>());
        }
        if (gestionnaire.getGestionstockCollection() == null) {
            gestionnaire.setGestionstockCollection(new ArrayList<Gestionstock>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Facture> attachedFactureCollection = new ArrayList<Facture>();
            for (Facture factureCollectionFactureToAttach : gestionnaire.getFactureCollection()) {
                factureCollectionFactureToAttach = em.getReference(factureCollectionFactureToAttach.getClass(), factureCollectionFactureToAttach.getIdFac());
                attachedFactureCollection.add(factureCollectionFactureToAttach);
            }
            gestionnaire.setFactureCollection(attachedFactureCollection);
            Collection<Gestionstock> attachedGestionstockCollection = new ArrayList<Gestionstock>();
            for (Gestionstock gestionstockCollectionGestionstockToAttach : gestionnaire.getGestionstockCollection()) {
                gestionstockCollectionGestionstockToAttach = em.getReference(gestionstockCollectionGestionstockToAttach.getClass(), gestionstockCollectionGestionstockToAttach.getIdStock());
                attachedGestionstockCollection.add(gestionstockCollectionGestionstockToAttach);
            }
            gestionnaire.setGestionstockCollection(attachedGestionstockCollection);
            em.persist(gestionnaire);
            for (Facture factureCollectionFacture : gestionnaire.getFactureCollection()) {
                Gestionnaire oldIdCaissiereOfFactureCollectionFacture = factureCollectionFacture.getIdCaissiere();
                factureCollectionFacture.setIdCaissiere(gestionnaire);
                factureCollectionFacture = em.merge(factureCollectionFacture);
                if (oldIdCaissiereOfFactureCollectionFacture != null) {
                    oldIdCaissiereOfFactureCollectionFacture.getFactureCollection().remove(factureCollectionFacture);
                    oldIdCaissiereOfFactureCollectionFacture = em.merge(oldIdCaissiereOfFactureCollectionFacture);
                }
            }
            for (Gestionstock gestionstockCollectionGestionstock : gestionnaire.getGestionstockCollection()) {
                Gestionnaire oldIdGestOfGestionstockCollectionGestionstock = gestionstockCollectionGestionstock.getIdGest();
                gestionstockCollectionGestionstock.setIdGest(gestionnaire);
                gestionstockCollectionGestionstock = em.merge(gestionstockCollectionGestionstock);
                if (oldIdGestOfGestionstockCollectionGestionstock != null) {
                    oldIdGestOfGestionstockCollectionGestionstock.getGestionstockCollection().remove(gestionstockCollectionGestionstock);
                    oldIdGestOfGestionstockCollectionGestionstock = em.merge(oldIdGestOfGestionstockCollectionGestionstock);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
    }

    public void edit(Gestionnaire gestionnaire) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Gestionnaire persistentGestionnaire = em.find(Gestionnaire.class, gestionnaire.getIdGest());
            Collection<Facture> factureCollectionOld = persistentGestionnaire.getFactureCollection();
            Collection<Facture> factureCollectionNew = gestionnaire.getFactureCollection();
            Collection<Gestionstock> gestionstockCollectionOld = persistentGestionnaire.getGestionstockCollection();
            Collection<Gestionstock> gestionstockCollectionNew = gestionnaire.getGestionstockCollection();
            List<String> illegalOrphanMessages = null;
            for (Facture factureCollectionOldFacture : factureCollectionOld) {
                if (!factureCollectionNew.contains(factureCollectionOldFacture)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facture " + factureCollectionOldFacture + " since its idCaissiere field is not nullable.");
                }
            }
            for (Gestionstock gestionstockCollectionOldGestionstock : gestionstockCollectionOld) {
                if (!gestionstockCollectionNew.contains(gestionstockCollectionOldGestionstock)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Gestionstock " + gestionstockCollectionOldGestionstock + " since its idGest field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Facture> attachedFactureCollectionNew = new ArrayList<Facture>();
            for (Facture factureCollectionNewFactureToAttach : factureCollectionNew) {
                factureCollectionNewFactureToAttach = em.getReference(factureCollectionNewFactureToAttach.getClass(), factureCollectionNewFactureToAttach.getIdFac());
                attachedFactureCollectionNew.add(factureCollectionNewFactureToAttach);
            }
            factureCollectionNew = attachedFactureCollectionNew;
            gestionnaire.setFactureCollection(factureCollectionNew);
            Collection<Gestionstock> attachedGestionstockCollectionNew = new ArrayList<Gestionstock>();
            for (Gestionstock gestionstockCollectionNewGestionstockToAttach : gestionstockCollectionNew) {
                gestionstockCollectionNewGestionstockToAttach = em.getReference(gestionstockCollectionNewGestionstockToAttach.getClass(), gestionstockCollectionNewGestionstockToAttach.getIdStock());
                attachedGestionstockCollectionNew.add(gestionstockCollectionNewGestionstockToAttach);
            }
            gestionstockCollectionNew = attachedGestionstockCollectionNew;
            gestionnaire.setGestionstockCollection(gestionstockCollectionNew);
            gestionnaire = em.merge(gestionnaire);
            for (Facture factureCollectionNewFacture : factureCollectionNew) {
                if (!factureCollectionOld.contains(factureCollectionNewFacture)) {
                    Gestionnaire oldIdCaissiereOfFactureCollectionNewFacture = factureCollectionNewFacture.getIdCaissiere();
                    factureCollectionNewFacture.setIdCaissiere(gestionnaire);
                    factureCollectionNewFacture = em.merge(factureCollectionNewFacture);
                    if (oldIdCaissiereOfFactureCollectionNewFacture != null && !oldIdCaissiereOfFactureCollectionNewFacture.equals(gestionnaire)) {
                        oldIdCaissiereOfFactureCollectionNewFacture.getFactureCollection().remove(factureCollectionNewFacture);
                        oldIdCaissiereOfFactureCollectionNewFacture = em.merge(oldIdCaissiereOfFactureCollectionNewFacture);
                    }
                }
            }
            for (Gestionstock gestionstockCollectionNewGestionstock : gestionstockCollectionNew) {
                if (!gestionstockCollectionOld.contains(gestionstockCollectionNewGestionstock)) {
                    Gestionnaire oldIdGestOfGestionstockCollectionNewGestionstock = gestionstockCollectionNewGestionstock.getIdGest();
                    gestionstockCollectionNewGestionstock.setIdGest(gestionnaire);
                    gestionstockCollectionNewGestionstock = em.merge(gestionstockCollectionNewGestionstock);
                    if (oldIdGestOfGestionstockCollectionNewGestionstock != null && !oldIdGestOfGestionstockCollectionNewGestionstock.equals(gestionnaire)) {
                        oldIdGestOfGestionstockCollectionNewGestionstock.getGestionstockCollection().remove(gestionstockCollectionNewGestionstock);
                        oldIdGestOfGestionstockCollectionNewGestionstock = em.merge(oldIdGestOfGestionstockCollectionNewGestionstock);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = gestionnaire.getIdGest();
                if (findGestionnaire(id) == null) {
                    throw new NonexistentEntityException("The gestionnaire with id " + id + " no longer exists.");
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
            Gestionnaire gestionnaire;
            try {
                gestionnaire = em.getReference(Gestionnaire.class, id);
                gestionnaire.getIdGest();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gestionnaire with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facture> factureCollectionOrphanCheck = gestionnaire.getFactureCollection();
            for (Facture factureCollectionOrphanCheckFacture : factureCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Gestionnaire (" + gestionnaire + ") cannot be destroyed since the Facture " + factureCollectionOrphanCheckFacture + " in its factureCollection field has a non-nullable idCaissiere field.");
            }
            Collection<Gestionstock> gestionstockCollectionOrphanCheck = gestionnaire.getGestionstockCollection();
            for (Gestionstock gestionstockCollectionOrphanCheckGestionstock : gestionstockCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Gestionnaire (" + gestionnaire + ") cannot be destroyed since the Gestionstock " + gestionstockCollectionOrphanCheckGestionstock + " in its gestionstockCollection field has a non-nullable idGest field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(gestionnaire);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Gestionnaire> findGestionnaireEntities() {
        return findGestionnaireEntities(true, -1, -1);
    }

    public List<Gestionnaire> findGestionnaireEntities(int maxResults, int firstResult) {
        return findGestionnaireEntities(false, maxResults, firstResult);
    }

    private List<Gestionnaire> findGestionnaireEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Gestionnaire.class));
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

    public Gestionnaire findGestionnaire(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Gestionnaire.class, id);
        } finally {
            em.close();
        }
    }
    
    public Gestionnaire findGestionnaire(String login){
        EntityManager em=getEntityManager();
        Gestionnaire gest=null;
        try {
            em.getTransaction().begin();
            gest=em.createNamedQuery("Gestionnaire.findByLogin", Gestionnaire.class)
                    .setParameter("login", login)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            
        }
        return gest;
        
    }
    
    public List<Gestionnaire> findGestionnaire(boolean typeGest){
        EntityManager em=getEntityManager();
        List<Gestionnaire> list_gest=null;
        try {
            em.getTransaction().begin();
            list_gest=em.createNamedQuery("Gestionnaire.findByTypeGest", Gestionnaire.class)
                    .setParameter("typeGest", typeGest)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            
        }
        return list_gest;
        
    }

    public int getGestionnaireCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Gestionnaire> rt = cq.from(Gestionnaire.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
