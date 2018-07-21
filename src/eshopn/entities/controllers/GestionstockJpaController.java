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
import eshopn.entities.Gestionnaire;
import eshopn.entities.Gestionstock;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class GestionstockJpaController implements Serializable {

    public GestionstockJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Gestionstock gestionstock) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit codePro = gestionstock.getCodePro();
            if (codePro != null) {
                codePro = em.getReference(codePro.getClass(), codePro.getCodePro());
                gestionstock.setCodePro(codePro);
            }
            Gestionnaire idGest = gestionstock.getIdGest();
            if (idGest != null) {
                idGest = em.getReference(idGest.getClass(), idGest.getIdGest());
                gestionstock.setIdGest(idGest);
            }
            em.persist(gestionstock);
            if (codePro != null) {
                codePro.getGestionstockCollection().add(gestionstock);
                codePro = em.merge(codePro);
            }
            if (idGest != null) {
                idGest.getGestionstockCollection().add(gestionstock);
                idGest = em.merge(idGest);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Gestionstock gestionstock) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Gestionstock persistentGestionstock = em.find(Gestionstock.class, gestionstock.getIdStock());
            Produit codeProOld = persistentGestionstock.getCodePro();
            Produit codeProNew = gestionstock.getCodePro();
            Gestionnaire idGestOld = persistentGestionstock.getIdGest();
            Gestionnaire idGestNew = gestionstock.getIdGest();
            if (codeProNew != null) {
                codeProNew = em.getReference(codeProNew.getClass(), codeProNew.getCodePro());
                gestionstock.setCodePro(codeProNew);
            }
            if (idGestNew != null) {
                idGestNew = em.getReference(idGestNew.getClass(), idGestNew.getIdGest());
                gestionstock.setIdGest(idGestNew);
            }
            gestionstock = em.merge(gestionstock);
            if (codeProOld != null && !codeProOld.equals(codeProNew)) {
                codeProOld.getGestionstockCollection().remove(gestionstock);
                codeProOld = em.merge(codeProOld);
            }
            if (codeProNew != null && !codeProNew.equals(codeProOld)) {
                codeProNew.getGestionstockCollection().add(gestionstock);
                codeProNew = em.merge(codeProNew);
            }
            if (idGestOld != null && !idGestOld.equals(idGestNew)) {
                idGestOld.getGestionstockCollection().remove(gestionstock);
                idGestOld = em.merge(idGestOld);
            }
            if (idGestNew != null && !idGestNew.equals(idGestOld)) {
                idGestNew.getGestionstockCollection().add(gestionstock);
                idGestNew = em.merge(idGestNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = gestionstock.getIdStock();
                if (findGestionstock(id) == null) {
                    throw new NonexistentEntityException("The gestionstock with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Gestionstock gestionstock;
            try {
                gestionstock = em.getReference(Gestionstock.class, id);
                gestionstock.getIdStock();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gestionstock with id " + id + " no longer exists.", enfe);
            }
            Produit codePro = gestionstock.getCodePro();
            if (codePro != null) {
                codePro.getGestionstockCollection().remove(gestionstock);
                codePro = em.merge(codePro);
            }
            Gestionnaire idGest = gestionstock.getIdGest();
            if (idGest != null) {
                idGest.getGestionstockCollection().remove(gestionstock);
                idGest = em.merge(idGest);
            }
            em.remove(gestionstock);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Gestionstock> findGestionstocksEntities(Gestionnaire idGest){
        EntityManager em=getEntityManager();
        List<Gestionstock> list=null;
        try {
            em.getTransaction().begin();
            list=em.createNamedQuery("Gestionstock.findByIdGest", Gestionstock.class)
                    .setParameter("idGest", idGest)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            
        }
        return list;
        
    }

    public List<Gestionstock> findGestionstockEntities() {
        return findGestionstockEntities(true, -1, -1);
    }
    
    public List<Gestionstock> findGestionstockEntities(boolean order) {
        if(!order) return  findGestionstockEntities();
        else{
            EntityManager em=getEntityManager();
            List<Gestionstock> list=null;
            try {
                em.getTransaction().begin();
                list=em.createQuery(
                        "SELECT g FROM Gestionstock g ORDER BY g.dateStock DESC")
                        .getResultList();
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    }
    
    public List<Gestionstock> findGestionstockEntities(int maxResults, int firstResult) {
        return findGestionstockEntities(false, maxResults, firstResult);
    }

    private List<Gestionstock> findGestionstockEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Gestionstock.class));
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

    public Gestionstock findGestionstock(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Gestionstock.class, id);
        } finally {
            em.close();
        }
    }

    public int getGestionstockCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Gestionstock> rt = cq.from(Gestionstock.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
