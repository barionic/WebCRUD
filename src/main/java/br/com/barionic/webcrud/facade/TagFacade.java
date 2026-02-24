package br.com.barionic.webcrud.facade;

import br.com.barionic.webcrud.entity.Tag;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class TagFacade {

    @PersistenceContext
    private EntityManager em;

    public void salvar(Tag tag){
        if(tag.getId() == null){
            em.persist(tag);
        } else{
            em.merge(tag);
        }
    }

    public void remover(Long id){
        Tag tag = em.find(Tag.class, id);
        if(tag != null){
            em.remove(tag);
        }
    }

    public List<Tag> buscarPorIds(List<Long> ids){
        return em.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class).setParameter("ids", ids).getResultList();
    }

    public List<Tag> listarTodos(){
        return em.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
    }

}
