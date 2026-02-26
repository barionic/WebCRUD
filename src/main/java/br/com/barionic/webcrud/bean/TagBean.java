package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.entity.Tag;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import br.com.barionic.webcrud.facade.TagFacade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("tagBean")
@ViewScoped
public class TagBean implements Serializable {

    @Inject
    private TagFacade facade;

    private Tag tag;
    private List<Tag> lista;

    @PostConstruct
    public void init(){
        tag = new Tag();
        lista = facade.listarTodos();
    }

    public void salvar(){
        try{
            facade.salvar(tag);
            tag = new Tag();
            lista = facade.listarTodos();
        } catch (RegraNegocioException e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            e.getMessage(), null));
        }
    }

    public void remover(Tag t){
        facade.remover(t.getId());
        lista = facade.listarTodos();
    }

    // ==== Getters & Setters ====
    public Tag getTag() {return tag;}

    public List<Tag> getLista() {return lista;}

}
