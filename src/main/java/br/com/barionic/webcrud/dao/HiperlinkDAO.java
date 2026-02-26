package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

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
        if(idAtual != null){
            query.setParameter("idAtual", idAtual);
        }
        return query.getSingleResult()>0;
    }

    public List<Hiperlink> buscarComFiltro(String nome, Long grupoId, Long tagId){
        StringBuilder jpql = new StringBuilder("SELECT h FROM Hiperlink h WHERE 1=1");
        if (nome != null && !nome.isBlank()){
            jpql.append(" AND LOWER(h.name) LIKE LOWER(:nome)");
        }
        if (grupoId != null){
            jpql.append(" AND h.grupo.id = :grupoId");
        }
        if (tagId != null){
            jpql.append(" AND EXISTS (SELECT t FROM h.tags WHERE t.id = :tagId)");
        }
        var query = em.createQuery(jpql.toString(), Hiperlink.class);
        if (nome != null && !nome.isBlank()){
            query.setParameter("nome", "%" + nome + "%");
        }
        if(grupoId != null){
            query.setParameter("grupoId", grupoId);
        }
        if(tagId != null){
            query.setParameter("tagId", tagId);
        }
        return query.getResultList();
    }

}
