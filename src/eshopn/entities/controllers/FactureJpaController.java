/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities.controllers;

import eshopn.entities.Facture;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eshopn.entities.Gestionnaire;
import eshopn.entities.Lignefacture;
import eshopn.entities.controllers.exceptions.IllegalOrphanException;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class FactureJpaController implements Serializable {

    public FactureJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facture facture) {
        if(facture==null) System.out.println("passage---++");
        if (facture.getLignefactureCollection() == null) {
            facture.setLignefactureCollection(new ArrayList<Lignefacture>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Gestionnaire idCaissiere = facture.getIdCaissiere();
            if (idCaissiere != null) {
                idCaissiere = em.getReference(idCaissiere.getClass(), idCaissiere.getIdGest());
                facture.setIdCaissiere(idCaissiere);
            }
            Collection<Lignefacture> attachedLignefactureCollection = new ArrayList<Lignefacture>();
            for (Lignefacture lignefactureCollectionLignefactureToAttach : facture.getLignefactureCollection()) {
                lignefactureCollectionLignefactureToAttach = em.getReference(lignefactureCollectionLignefactureToAttach.getClass(), lignefactureCollectionLignefactureToAttach.getLignefacturePK());
                attachedLignefactureCollection.add(lignefactureCollectionLignefactureToAttach);
            }
            facture.setLignefactureCollection(attachedLignefactureCollection);
            em.persist(facture);
            if (idCaissiere != null) {
                idCaissiere.getFactureCollection().add(facture);
                idCaissiere = em.merge(idCaissiere);
            }
            for (Lignefacture lignefactureCollectionLignefacture : facture.getLignefactureCollection()) {
                Facture oldFactureOfLignefactureCollectionLignefacture = lignefactureCollectionLignefacture.getFacture();
                lignefactureCollectionLignefacture.setFacture(facture);
                lignefactureCollectionLignefacture = em.merge(lignefactureCollectionLignefacture);
                if (oldFactureOfLignefactureCollectionLignefacture != null) {
                    oldFactureOfLignefactureCollectionLignefacture.getLignefactureCollection().remove(lignefactureCollectionLignefacture);
                    oldFactureOfLignefactureCollectionLignefacture = em.merge(oldFactureOfLignefactureCollectionLignefacture);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facture facture) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facture persistentFacture = em.find(Facture.class, facture.getIdFac());
            Gestionnaire idCaissiereOld = persistentFacture.getIdCaissiere();
            Gestionnaire idCaissiereNew = facture.getIdCaissiere();
            Collection<Lignefacture> lignefactureCollectionOld = persistentFacture.getLignefactureCollection();
            Collection<Lignefacture> lignefactureCollectionNew = facture.getLignefactureCollection();
            List<String> illegalOrphanMessages = null;
            for (Lignefacture lignefactureCollectionOldLignefacture : lignefactureCollectionOld) {
                if (!lignefactureCollectionNew.contains(lignefactureCollectionOldLignefacture)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Lignefacture " + lignefactureCollectionOldLignefacture + " since its facture field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCaissiereNew != null) {
                idCaissiereNew = em.getReference(idCaissiereNew.getClass(), idCaissiereNew.getIdGest());
                facture.setIdCaissiere(idCaissiereNew);
            }
            Collection<Lignefacture> attachedLignefactureCollectionNew = new ArrayList<Lignefacture>();
            for (Lignefacture lignefactureCollectionNewLignefactureToAttach : lignefactureCollectionNew) {
                lignefactureCollectionNewLignefactureToAttach = em.getReference(lignefactureCollectionNewLignefactureToAttach.getClass(), lignefactureCollectionNewLignefactureToAttach.getLignefacturePK());
                attachedLignefactureCollectionNew.add(lignefactureCollectionNewLignefactureToAttach);
            }
            lignefactureCollectionNew = attachedLignefactureCollectionNew;
            facture.setLignefactureCollection(lignefactureCollectionNew);
            facture = em.merge(facture);
            if (idCaissiereOld != null && !idCaissiereOld.equals(idCaissiereNew)) {
                idCaissiereOld.getFactureCollection().remove(facture);
                idCaissiereOld = em.merge(idCaissiereOld);
            }
            if (idCaissiereNew != null && !idCaissiereNew.equals(idCaissiereOld)) {
                idCaissiereNew.getFactureCollection().add(facture);
                idCaissiereNew = em.merge(idCaissiereNew);
            }
            for (Lignefacture lignefactureCollectionNewLignefacture : lignefactureCollectionNew) {
                if (!lignefactureCollectionOld.contains(lignefactureCollectionNewLignefacture)) {
                    Facture oldFactureOfLignefactureCollectionNewLignefacture = lignefactureCollectionNewLignefacture.getFacture();
                    lignefactureCollectionNewLignefacture.setFacture(facture);
                    lignefactureCollectionNewLignefacture = em.merge(lignefactureCollectionNewLignefacture);
                    if (oldFactureOfLignefactureCollectionNewLignefacture != null && !oldFactureOfLignefactureCollectionNewLignefacture.equals(facture)) {
                        oldFactureOfLignefactureCollectionNewLignefacture.getLignefactureCollection().remove(lignefactureCollectionNewLignefacture);
                        oldFactureOfLignefactureCollectionNewLignefacture = em.merge(oldFactureOfLignefactureCollectionNewLignefacture);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = facture.getIdFac();
                if (findFacture(id) == null) {
                    throw new NonexistentEntityException("The facture with id " + id + " no longer exists.");
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
            Facture facture;
            try {
                facture = em.getReference(Facture.class, id);
                facture.getIdFac();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facture with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Lignefacture> lignefactureCollectionOrphanCheck = facture.getLignefactureCollection();
            for (Lignefacture lignefactureCollectionOrphanCheckLignefacture : lignefactureCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facture (" + facture + ") cannot be destroyed since the Lignefacture " + lignefactureCollectionOrphanCheckLignefacture + " in its lignefactureCollection field has a non-nullable facture field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Gestionnaire idCaissiere = facture.getIdCaissiere();
            if (idCaissiere != null) {
                idCaissiere.getFactureCollection().remove(facture);
                idCaissiere = em.merge(idCaissiere);
            }
            em.remove(facture);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Facture> findFactureEntities() {
        return findFactureEntities(true, -1, -1);
    }
    
    public List<Facture> findFactureEntities(boolean order) {
        if(!order) return findFactureEntities();
        else{
            EntityManager em=getEntityManager();
            List<Facture> list=null;
            try {
                em.getTransaction().begin();
                list=em.createQuery(
                        "SELECT f FROM Facture f ORDER BY f.dateFac DESC")
                        .getResultList();
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    }
    

    public List<Facture> findFactureEntities(int maxResults, int firstResult) {
        return findFactureEntities(false, maxResults, firstResult);
    }

    private List<Facture> findFactureEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facture.class));
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

    public Facture findFacture(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facture.class, id);
        } finally {
            em.close();
        }
    }
    
    public Facture findFacture(Date date){
        EntityManager em=getEntityManager();
        Facture fact=null;
        try {
            em.getTransaction().begin();
            fact=em.createNamedQuery("Facture.findByDateFac", Facture.class)
                    .setParameter("dateFac", date)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            
        }
        return fact;
        
    }

    public int getFactureCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facture> rt = cq.from(Facture.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
