package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Tag;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class TagDAO extends GenericDAO<Tag> {

    public TagDAO(){
        super(Tag.class);
    }

    @PersistenceContext(unitName = "webcrudPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Tag> buscarPorIds(List<Long> ids){
        return em.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class).setParameter("ids", ids).getResultList();
    }

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        String jpql = "SELECT t FROM Tag t WHERE t.tagName = :nome";
        if (idAtual != null){
            jpql += " AND t.id <> :idAtual";
        }
        TypedQuery<Tag> query = getEntityManager().createQuery(jpql, Tag.class).setParameter("nome", nome);
        if (idAtual != null){
            query.setParameter("idAtual", idAtual);
        }
        return !query.getResultList().isEmpty();
    }


}
