package br.com.barionic.webcrud.facade;

import br.com.barionic.webcrud.dao.HiperlinkDAO;
import br.com.barionic.webcrud.entity.Hiperlink;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class HiperlinkFacade {

    @Inject
    private HiperlinkDAO dao;

    @Transactional
    public void salvar(Hiperlink hiperlink){
        if (hiperlink.getId() == null){
            dao.create(hiperlink);
        }else{
            dao.update(hiperlink);
        }
    }

    @Transactional
    public void remover(Hiperlink hiperlink){
        dao.remove(hiperlink);
    }

    public List<Hiperlink> listarTodos(){
        return dao.findAll();
    }

}
