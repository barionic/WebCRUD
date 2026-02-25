package br.com.barionic.webcrud.facade;


import br.com.barionic.webcrud.entity.Grupo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class GrupoFacade {

    @PersistenceContext
    private EntityManager em;

    public void salvar(Grupo grupo){
        if (grupo.getId() == null){
            em.persist(grupo);
        } else{
            em.merge(grupo);
        }
    }

    public void remover(Long id){
        Grupo grupo = em.find(Grupo.class, id);
        if(grupo != null){
            em.remove(grupo);
        }
    }

    public Grupo buscarPorId(Long id){
        return em.find(Grupo.class, id);
    }

    public List<Grupo> listarTodos(){
        return em.createQuery("SELECT g FROM Grupo g", Grupo.class).getResultList();
    }

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        String jpql = "SELECT g FROM Grupo g WHERE g.grupoName = :nome";
        if (idAtual != null){
            jpql += " AND g.id <> :idAtual";
        }
        TypedQuery<Grupo> query = em.createQuery(jpql, Grupo.class).setParameter("nome", nome);
        if (idAtual != null){
            query.setParameter("idAtual", idAtual);
        }
        return !query.getResultList().isEmpty();
    }
}
