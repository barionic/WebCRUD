package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.entity.Grupo;
import br.com.barionic.webcrud.facade.GrupoFacade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("grupoBean")
@ViewScoped
public class GrupoBean implements Serializable {

    @Inject
    private GrupoFacade facade;

    private Grupo grupo;
    private List<Grupo> lista;

    @PostConstruct
    public void init(){
        grupo = new Grupo();
        lista = facade.listarTodos();
    }

    public void salvar(){
        facade.salvar(grupo);
        grupo = new Grupo();
        lista = facade.listarTodos();
    }

    public void remover(Grupo g){
        facade.remover(g.getId());
        lista = facade.listarTodos();
    }

    //==== Getters ====
    public Grupo getGrupo(){
        return grupo;
    }

    public List<Grupo> getLista(){
        return lista;
    }
}
