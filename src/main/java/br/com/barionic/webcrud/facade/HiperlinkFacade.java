package br.com.barionic.webcrud.facade;

import br.com.barionic.webcrud.dao.HiperlinkDAO;
import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class HiperlinkFacade {

    @EJB
    private HiperlinkDAO dao;

    public void salvar(Hiperlink hiperlink){
        if (hiperlink.getId() == null){
            dao.create(hiperlink);
        }else{
            dao.update(hiperlink);
        }
    }

    public void remover(Long id){
        Hiperlink hiperlink = dao.find(id);
        if(hiperlink != null){
            dao.remove(hiperlink);
        }
    }

    public Hiperlink buscarPorId(Long id){ return dao.find(id); }

    public List<Hiperlink> listarTodos(){
        return dao.findAll();
    }

    public boolean existeOutroComMesmoNome(String nome, Long idAtual){
        return dao.existeOutroComMesmoNome(nome, idAtual);
    }

}
