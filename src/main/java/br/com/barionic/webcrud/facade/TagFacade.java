package br.com.barionic.webcrud.facade;

import br.com.barionic.webcrud.dao.TagDAO;
import br.com.barionic.webcrud.entity.Tag;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class TagFacade {

   @EJB
   private TagDAO dao;

    public void salvar(Tag tag){
        validarNomeUnico(tag);
        if(tag.getId() == null){
            dao.create(tag);
        } else{
            dao.update(tag);
        }
    }

    private void validarNomeUnico(Tag tag){
        if(dao.existeOutroComMesmoNome(tag.getTagName(), tag.getId())){
            throw new RegraNegocioException("Já existe uma tag com esse nome.");
        }
    }

    public void remover(Long id){
        Tag tag = dao.find(id);
        if(tag != null){
            dao.remove(tag);
        }
    }

    public List<Tag> buscarPorIds(List<Long> ids){
        return dao.buscarPorIds(ids);
    }

    public List<Tag> listarTodos(){
        return dao.findAll();
    }

}
