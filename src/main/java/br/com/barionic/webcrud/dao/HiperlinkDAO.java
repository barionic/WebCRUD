package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class HiperlinkDAO extends GenericDAO<Hiperlink>{

    public HiperlinkDAO(){
        super(Hiperlink.class);
    }

    @PersistenceContext(unitName = "webcrudPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        String jpql = "SELECT COUNT(h) FROM Hiperlink h WHERE h.name = :nome";
        if(idAtual != null){
            jpql += " AND h.id <> :idAtual";
        }
        var query = getEntityManager().createQuery(jpql, Long.class).setParameter("nome", nome);
        //TypedQuery<Hiperlink> query = getEntityManager().createQuery(jpql, Hiperlink.class).setParameter("nome", nome);
        if(idAtual != null){
            query.setParameter("idAtual", idAtual);
        }
        return query.getSingleResult()>0;
        //return !query.getResultList().isEmpty();
    }

}
