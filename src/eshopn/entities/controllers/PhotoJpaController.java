/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshopn.entities.controllers;

import eshopn.entities.Gestionnaire;
import eshopn.entities.Photo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eshopn.entities.Produit;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author MBOGNI RUVIC
 */
public class PhotoJpaController implements Serializable {

    public PhotoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Photo photo) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit codePro = photo.getCodePro();
            if (codePro != null) {
                codePro = em.getReference(codePro.getClass(), codePro.getCodePro());
                photo.setCodePro(codePro);
            }
            em.persist(photo);
            if (codePro != null) {
                codePro.getPhotoCollection().add(photo);
                codePro = em.merge(codePro);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Photo photo) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Photo persistentPhoto = em.find(Photo.class, photo.getIdPhoto());
            Produit codeProOld = persistentPhoto.getCodePro();
            Produit codeProNew = photo.getCodePro();
            if (codeProNew != null) {
                codeProNew = em.getReference(codeProNew.getClass(), codeProNew.getCodePro());
                photo.setCodePro(codeProNew);
            }
            photo = em.merge(photo);
            if (codeProOld != null && !codeProOld.equals(codeProNew)) {
                codeProOld.getPhotoCollection().remove(photo);
                codeProOld = em.merge(codeProOld);
            }
            if (codeProNew != null && !codeProNew.equals(codeProOld)) {
                codeProNew.getPhotoCollection().add(photo);
                codeProNew = em.merge(codeProNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = photo.getIdPhoto();
                if (findPhoto(id) == null) {
                    throw new NonexistentEntityException("The photo with id " + id + " no longer exists.");
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
            Photo photo;
            try {
                photo = em.getReference(Photo.class, id);
                photo.getIdPhoto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The photo with id " + id + " no longer exists.", enfe);
            }
            Produit codePro = photo.getCodePro();
            if (codePro != null) {
                codePro.getPhotoCollection().remove(photo);
                codePro = em.merge(codePro);
            }
            em.remove(photo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    public List<Photo> findPhotosEntities(Produit pr){
        EntityManager em=getEntityManager();
        List<Photo> list=null;
        try {
            em.getTransaction().begin();
            list=em.createNamedQuery("Photo.findByCodeProduit", Photo.class)
                    .setParameter("codePro", pr)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            
        }
        return list;
        
    }

    public List<Photo> findPhotoEntities() {
        return findPhotoEntities(true, -1, -1);
    }

    public List<Photo> findPhotoEntities(int maxResults, int firstResult) {
        return findPhotoEntities(false, maxResults, firstResult);
    }

    private List<Photo> findPhotoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Photo.class));
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

    public Photo findPhoto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Photo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPhotoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Photo> rt = cq.from(Photo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    
}
