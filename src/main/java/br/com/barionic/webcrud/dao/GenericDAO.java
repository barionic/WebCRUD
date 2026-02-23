package br.com.barionic.webcrud.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public abstract class GenericDAO<T> {

    @PersistenceContext(unitName = "webcrudPU")
    protected EntityManager em;

    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    public void create(T entity){
        em.persist(entity);
    }

    public T update(T entity){
        return em.merge(entity);
    }

    public void remove(T entity){
        em.remove(em.merge(entity));
    }

    public T find(Long id){
        return em.find(entityClass, id);
    }

    public List<T> findAll() {
        return em.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                entityClass
        ).getResultList();
    }
}
