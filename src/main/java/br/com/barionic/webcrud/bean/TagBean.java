package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.entity.Tag;
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
        if(facade.existeOutroComMesmoNome(tag.getTagName(), tag.getId())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Já existe uma tag com esse nome.", null));
            return;
        }

        facade.salvar(tag);
        tag = new Tag();
        lista = facade.listarTodos();
    }

    public void remover(Tag t){
        facade.remover(t.getId());
        lista = facade.listarTodos();
    }

    // ==== Getters & Setters ====
    public TagFacade getFacade() {return facade;}

    public void setFacade(TagFacade facade) {this.facade = facade;}

    public Tag getTag() {return tag;}

    public void setTag(Tag tag) {this.tag = tag;}

    public List<Tag> getLista() {return lista;}

    public void setLista(List<Tag> lista) {this.lista = lista;}
}
