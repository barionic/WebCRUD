package br.com.barionic.webcrud.facade;


import br.com.barionic.webcrud.entity.Grupo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
}
