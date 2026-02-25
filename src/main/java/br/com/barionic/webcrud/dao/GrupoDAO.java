package br.com.barionic.webcrud.dao;

import br.com.barionic.webcrud.entity.Grupo;
import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class GrupoDAO extends GenericDAO<Grupo>{

    public GrupoDAO(){
        super(Grupo.class);
    }

    @PersistenceContext(unitName = "webcrudPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        String jpql = "SELECT g FROM Grupo g WHERE g.grupoName = :nome";
        if (idAtual != null){
            jpql += " AND g.id <> :idAtual";
        }
        TypedQuery<Grupo> query = getEntityManager().createQuery(jpql, Grupo.class).setParameter("nome", nome);
        if (idAtual != null){
            query.setParameter("idAtual", idAtual);
        }
        return !query.getResultList().isEmpty();
    }
}
