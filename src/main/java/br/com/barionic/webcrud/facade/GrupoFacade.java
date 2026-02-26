package br.com.barionic.webcrud.facade;

import br.com.barionic.webcrud.dao.GrupoDAO;
import br.com.barionic.webcrud.entity.Grupo;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class GrupoFacade {

    @EJB
    private GrupoDAO dao;

    public void salvar(Grupo grupo){
        validarNomeUnico(grupo);
        if(grupo.getId() == null){
            dao.create(grupo);
        }else{
            dao.update(grupo);
        }
    }

    private void validarNomeUnico(Grupo grupo){
        if (dao.existeOutroComMesmoNome(grupo.getGrupoName(), grupo.getId())){
            throw new RegraNegocioException("Já existe um grupo com esse nome.");
        }
    }

    public void remover(Long id){
        Grupo grupo = dao.find(id);
        if(grupo != null){
            dao.remove(grupo);
        }
    }

    public Grupo buscarPorId(Long id){
        return dao.find(id);
    }

    public List<Grupo> listarTodos(){
        return dao.findAll();
    }

}
