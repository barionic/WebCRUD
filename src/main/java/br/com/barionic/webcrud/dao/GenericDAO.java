package br.com.barionic.webcrud.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public abstract class GenericDAO<T> {

    protected abstract EntityManager getEntityManager();

    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    public void create(T entity){
        getEntityManager().persist(entity);
    }

    public T update(T entity){
        return getEntityManager().merge(entity);
    }

    public void remove(T entity){
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Long id){
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        return getEntityManager().createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)

        .getResultList();
    }
}
