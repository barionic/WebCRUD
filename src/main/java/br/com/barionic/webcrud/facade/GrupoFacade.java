package br.com.barionic.webcrud.facade;


import br.com.barionic.webcrud.dao.GrupoDAO;
import br.com.barionic.webcrud.entity.Grupo;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class GrupoFacade {

    @EJB
    private GrupoDAO dao;

    public void salvar(Grupo grupo){
        if (grupo.getId() == null){
            dao.create(grupo);
        } else{
            dao.update(grupo);
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

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        return dao.existeOutroComMesmoNome(nome, idAtual);
    }
}
