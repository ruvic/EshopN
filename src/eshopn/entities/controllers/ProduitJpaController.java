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
import eshopn.entities.Categorie;
import eshopn.entities.Gestionnaire;
import eshopn.entities.Photo;
import java.util.ArrayList;
import java.util.Collection;
import eshopn.entities.Gestionstock;
import eshopn.entities.Lignefacture;
import eshopn.entities.Produit;
import eshopn.entities.controllers.exceptions.IllegalOrphanException;
import eshopn.entities.controllers.exceptions.NonexistentEntityException;
import eshopn.entities.controllers.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;

/**
 *
 * @author MBOGNI RUVIC
 */
public class ProduitJpaController implements Serializable {

    public ProduitJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produit produit) throws PreexistingEntityException, Exception {
        if (produit.getPhotoCollection() == null) {
            produit.setPhotoCollection(new ArrayList<Photo>());
        }
        if (produit.getGestionstockCollection() == null) {
            produit.setGestionstockCollection(new ArrayList<Gestionstock>());
        }
        if (produit.getLignefactureCollection() == null) {
            produit.setLignefactureCollection(new ArrayList<Lignefacture>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categorie idCategorie = produit.getIdCategorie();
            if (idCategorie != null) {
                idCategorie = em.getReference(idCategorie.getClass(), idCategorie.getIdCat());
                produit.setIdCategorie(idCategorie);
            }
            Collection<Photo> attachedPhotoCollection = new ArrayList<Photo>();
            for (Photo photoCollectionPhotoToAttach : produit.getPhotoCollection()) {
                photoCollectionPhotoToAttach = em.getReference(photoCollectionPhotoToAttach.getClass(), photoCollectionPhotoToAttach.getIdPhoto());
                attachedPhotoCollection.add(photoCollectionPhotoToAttach);
            }
            produit.setPhotoCollection(attachedPhotoCollection);
            Collection<Gestionstock> attachedGestionstockCollection = new ArrayList<Gestionstock>();
            for (Gestionstock gestionstockCollectionGestionstockToAttach : produit.getGestionstockCollection()) {
                gestionstockCollectionGestionstockToAttach = em.getReference(gestionstockCollectionGestionstockToAttach.getClass(), gestionstockCollectionGestionstockToAttach.getIdStock());
                attachedGestionstockCollection.add(gestionstockCollectionGestionstockToAttach);
            }
            produit.setGestionstockCollection(attachedGestionstockCollection);
            Collection<Lignefacture> attachedLignefactureCollection = new ArrayList<Lignefacture>();
            for (Lignefacture lignefactureCollectionLignefactureToAttach : produit.getLignefactureCollection()) {
                lignefactureCollectionLignefactureToAttach = em.getReference(lignefactureCollectionLignefactureToAttach.getClass(), lignefactureCollectionLignefactureToAttach.getLignefacturePK());
                attachedLignefactureCollection.add(lignefactureCollectionLignefactureToAttach);
            }
            produit.setLignefactureCollection(attachedLignefactureCollection);
            em.persist(produit);
            if (idCategorie != null) {
                idCategorie.getProduitCollection().add(produit);
                idCategorie = em.merge(idCategorie);
            }
            for (Photo photoCollectionPhoto : produit.getPhotoCollection()) {
                Produit oldCodeProOfPhotoCollectionPhoto = photoCollectionPhoto.getCodePro();
                photoCollectionPhoto.setCodePro(produit);
                photoCollectionPhoto = em.merge(photoCollectionPhoto);
                if (oldCodeProOfPhotoCollectionPhoto != null) {
                    oldCodeProOfPhotoCollectionPhoto.getPhotoCollection().remove(photoCollectionPhoto);
                    oldCodeProOfPhotoCollectionPhoto = em.merge(oldCodeProOfPhotoCollectionPhoto);
                }
            }
            for (Gestionstock gestionstockCollectionGestionstock : produit.getGestionstockCollection()) {
                Produit oldCodeProOfGestionstockCollectionGestionstock = gestionstockCollectionGestionstock.getCodePro();
                gestionstockCollectionGestionstock.setCodePro(produit);
                gestionstockCollectionGestionstock = em.merge(gestionstockCollectionGestionstock);
                if (oldCodeProOfGestionstockCollectionGestionstock != null) {
                    oldCodeProOfGestionstockCollectionGestionstock.getGestionstockCollection().remove(gestionstockCollectionGestionstock);
                    oldCodeProOfGestionstockCollectionGestionstock = em.merge(oldCodeProOfGestionstockCollectionGestionstock);
                }
            }
            for (Lignefacture lignefactureCollectionLignefacture : produit.getLignefactureCollection()) {
                Produit oldProduitOfLignefactureCollectionLignefacture = lignefactureCollectionLignefacture.getProduit();
                lignefactureCollectionLignefacture.setProduit(produit);
                lignefactureCollectionLignefacture = em.merge(lignefactureCollectionLignefacture);
                if (oldProduitOfLignefactureCollectionLignefacture != null) {
                    oldProduitOfLignefactureCollectionLignefacture.getLignefactureCollection().remove(lignefactureCollectionLignefacture);
                    oldProduitOfLignefactureCollectionLignefacture = em.merge(oldProduitOfLignefactureCollectionLignefacture);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProduit(produit.getCodePro()) != null) {
                throw new PreexistingEntityException("Produit " + produit + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produit produit) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produit persistentProduit = em.find(Produit.class, produit.getCodePro());
            Categorie idCategorieOld = persistentProduit.getIdCategorie();
            Categorie idCategorieNew = produit.getIdCategorie();
            Collection<Photo> photoCollectionOld = persistentProduit.getPhotoCollection();
            Collection<Photo> photoCollectionNew = produit.getPhotoCollection();
            Collection<Gestionstock> gestionstockCollectionOld = persistentProduit.getGestionstockCollection();
            Collection<Gestionstock> gestionstockCollectionNew = produit.getGestionstockCollection();
            Collection<Lignefacture> lignefactureCollectionOld = persistentProduit.getLignefactureCollection();
            Collection<Lignefacture> lignefactureCollectionNew = produit.getLignefactureCollection();
            List<String> illegalOrphanMessages = null;
            for (Photo photoCollectionOldPhoto : photoCollectionOld) {
                if (!photoCollectionNew.contains(photoCollectionOldPhoto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Photo " + photoCollectionOldPhoto + " since its codePro field is not nullable.");
                }
            }
            for (Gestionstock gestionstockCollectionOldGestionstock : gestionstockCollectionOld) {
                if (!gestionstockCollectionNew.contains(gestionstockCollectionOldGestionstock)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Gestionstock " + gestionstockCollectionOldGestionstock + " since its codePro field is not nullable.");
                }
            }
            for (Lignefacture lignefactureCollectionOldLignefacture : lignefactureCollectionOld) {
                if (!lignefactureCollectionNew.contains(lignefactureCollectionOldLignefacture)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Lignefacture " + lignefactureCollectionOldLignefacture + " since its produit field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCategorieNew != null) {
                idCategorieNew = em.getReference(idCategorieNew.getClass(), idCategorieNew.getIdCat());
                produit.setIdCategorie(idCategorieNew);
            }
            Collection<Photo> attachedPhotoCollectionNew = new ArrayList<Photo>();
            for (Photo photoCollectionNewPhotoToAttach : photoCollectionNew) {
                photoCollectionNewPhotoToAttach = em.getReference(photoCollectionNewPhotoToAttach.getClass(), photoCollectionNewPhotoToAttach.getIdPhoto());
                attachedPhotoCollectionNew.add(photoCollectionNewPhotoToAttach);
            }
            photoCollectionNew = attachedPhotoCollectionNew;
            produit.setPhotoCollection(photoCollectionNew);
            Collection<Gestionstock> attachedGestionstockCollectionNew = new ArrayList<Gestionstock>();
            for (Gestionstock gestionstockCollectionNewGestionstockToAttach : gestionstockCollectionNew) {
                gestionstockCollectionNewGestionstockToAttach = em.getReference(gestionstockCollectionNewGestionstockToAttach.getClass(), gestionstockCollectionNewGestionstockToAttach.getIdStock());
                attachedGestionstockCollectionNew.add(gestionstockCollectionNewGestionstockToAttach);
            }
            gestionstockCollectionNew = attachedGestionstockCollectionNew;
            produit.setGestionstockCollection(gestionstockCollectionNew);
            Collection<Lignefacture> attachedLignefactureCollectionNew = new ArrayList<Lignefacture>();
            for (Lignefacture lignefactureCollectionNewLignefactureToAttach : lignefactureCollectionNew) {
                lignefactureCollectionNewLignefactureToAttach = em.getReference(lignefactureCollectionNewLignefactureToAttach.getClass(), lignefactureCollectionNewLignefactureToAttach.getLignefacturePK());
                attachedLignefactureCollectionNew.add(lignefactureCollectionNewLignefactureToAttach);
            }
            lignefactureCollectionNew = attachedLignefactureCollectionNew;
            produit.setLignefactureCollection(lignefactureCollectionNew);
            produit = em.merge(produit);
            if (idCategorieOld != null && !idCategorieOld.equals(idCategorieNew)) {
                idCategorieOld.getProduitCollection().remove(produit);
                idCategorieOld = em.merge(idCategorieOld);
            }
            if (idCategorieNew != null && !idCategorieNew.equals(idCategorieOld)) {
                idCategorieNew.getProduitCollection().add(produit);
                idCategorieNew = em.merge(idCategorieNew);
            }
            for (Photo photoCollectionNewPhoto : photoCollectionNew) {
                if (!photoCollectionOld.contains(photoCollectionNewPhoto)) {
                    Produit oldCodeProOfPhotoCollectionNewPhoto = photoCollectionNewPhoto.getCodePro();
                    photoCollectionNewPhoto.setCodePro(produit);
                    photoCollectionNewPhoto = em.merge(photoCollectionNewPhoto);
                    if (oldCodeProOfPhotoCollectionNewPhoto != null && !oldCodeProOfPhotoCollectionNewPhoto.equals(produit)) {
                        oldCodeProOfPhotoCollectionNewPhoto.getPhotoCollection().remove(photoCollectionNewPhoto);
                        oldCodeProOfPhotoCollectionNewPhoto = em.merge(oldCodeProOfPhotoCollectionNewPhoto);
                    }
                }
            }
            for (Gestionstock gestionstockCollectionNewGestionstock : gestionstockCollectionNew) {
                if (!gestionstockCollectionOld.contains(gestionstockCollectionNewGestionstock)) {
                    Produit oldCodeProOfGestionstockCollectionNewGestionstock = gestionstockCollectionNewGestionstock.getCodePro();
                    gestionstockCollectionNewGestionstock.setCodePro(produit);
                    gestionstockCollectionNewGestionstock = em.merge(gestionstockCollectionNewGestionstock);
                    if (oldCodeProOfGestionstockCollectionNewGestionstock != null && !oldCodeProOfGestionstockCollectionNewGestionstock.equals(produit)) {
                        oldCodeProOfGestionstockCollectionNewGestionstock.getGestionstockCollection().remove(gestionstockCollectionNewGestionstock);
                        oldCodeProOfGestionstockCollectionNewGestionstock = em.merge(oldCodeProOfGestionstockCollectionNewGestionstock);
                    }
                }
            }
            for (Lignefacture lignefactureCollectionNewLignefacture : lignefactureCollectionNew) {
                if (!lignefactureCollectionOld.contains(lignefactureCollectionNewLignefacture)) {
                    Produit oldProduitOfLignefactureCollectionNewLignefacture = lignefactureCollectionNewLignefacture.getProduit();
                    lignefactureCollectionNewLignefacture.setProduit(produit);
                    lignefactureCollectionNewLignefacture = em.merge(lignefactureCollectionNewLignefacture);
                    if (oldProduitOfLignefactureCollectionNewLignefacture != null && !oldProduitOfLignefactureCollectionNewLignefacture.equals(produit)) {
                        oldProduitOfLignefactureCollectionNewLignefacture.getLignefactureCollection().remove(lignefactureCollectionNewLignefacture);
                        oldProduitOfLignefactureCollectionNewLignefacture = em.merge(oldProduitOfLignefactureCollectionNewLignefacture);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produit.getCodePro();
                if (findProduit(id) == null) {
                    throw new NonexistentEntityException("The produit with id " + id + " no longer exists.");
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
            Produit produit;
            try {
                produit = em.getReference(Produit.class, id);
                produit.getCodePro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produit with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Photo> photoCollectionOrphanCheck = produit.getPhotoCollection();
            for (Photo photoCollectionOrphanCheckPhoto : photoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produit (" + produit + ") cannot be destroyed since the Photo " + photoCollectionOrphanCheckPhoto + " in its photoCollection field has a non-nullable codePro field.");
            }
            Collection<Gestionstock> gestionstockCollectionOrphanCheck = produit.getGestionstockCollection();
            for (Gestionstock gestionstockCollectionOrphanCheckGestionstock : gestionstockCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produit (" + produit + ") cannot be destroyed since the Gestionstock " + gestionstockCollectionOrphanCheckGestionstock + " in its gestionstockCollection field has a non-nullable codePro field.");
            }
            Collection<Lignefacture> lignefactureCollectionOrphanCheck = produit.getLignefactureCollection();
            for (Lignefacture lignefactureCollectionOrphanCheckLignefacture : lignefactureCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produit (" + produit + ") cannot be destroyed since the Lignefacture " + lignefactureCollectionOrphanCheckLignefacture + " in its lignefactureCollection field has a non-nullable produit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categorie idCategorie = produit.getIdCategorie();
            if (idCategorie != null) {
                idCategorie.getProduitCollection().remove(produit);
                idCategorie = em.merge(idCategorie);
            }
            em.remove(produit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produit> findProduitEntities() {
        return findProduitEntities(true, -1, -1);
    }

    public List<Produit> findProduitEntities(int maxResults, int firstResult) {
        return findProduitEntities(false, maxResults, firstResult);
    }
    
    public List<Produit> findProduitEntitiesOrder() {
        EntityManager em = getEntityManager();
        List<Produit> produits=null;
        try {
            CriteriaBuilder cb=em.getCriteriaBuilder();
            CriteriaQuery<Produit> q=cb.createQuery(Produit.class);
            Root<Produit> c = q.from(Produit.class);
            q.select(c);
            q.orderBy(cb.desc(c.get("qte")));
            Query query = em.createQuery(q);
            produits=query.getResultList();
            
        } finally {
            em.close();
        }
        return produits;
    }

    private List<Produit> findProduitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produit.class));
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

    public Produit findProduit(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produit.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Categorie> findCategoriesByDescProductsCount(){
        EntityManager em=getEntityManager();
        List<Categorie> list=null;
        try {
            em.getTransaction().begin();
            list=em.createQuery(
                    "SELECT p.idCategorie FROM Produit p GROUP BY p.idCategorie ORDER BY COUNT(p) DESC")
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    
    public List<Produit> findProductsCategorie(Categorie id){
        EntityManager em=getEntityManager();
        List<Produit> list=null;
        try {
            em.getTransaction().begin();
            list=em.createNamedQuery("Produit.findByIdCat", Produit.class)
                    .setParameter("idCategorie", id)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        
    }
    
    public int getProduitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produit> rt = cq.from(Produit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Produit> findProduitEntitiesByIdCat(Categorie id){
        EntityManager em=getEntityManager();
        List<Produit> list=null;
        try {
            em.getTransaction().begin();
            list=em.createQuery(
                    "SELECT p FROM Produit p JOIN p.idCategorie t WHERE p.idCategorie = :idCat")
                    .setParameter("idCat", id)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Erreur de recupération des produits liés aux catégories");
            e.printStackTrace();
        }
        return list;
    }
}
