package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.entity.Grupo;
import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.entity.Tag;
import br.com.barionic.webcrud.facade.GrupoFacade;
import br.com.barionic.webcrud.facade.HiperlinkFacade;
import br.com.barionic.webcrud.facade.TagFacade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("hiperlinkBean")
@ViewScoped
public class HiperlinkBean implements Serializable{
    private List<String> colors = List.of("Azul", "Vermelho", "Amarelo", "Laranja", "Roxo", "Verde");
    private List<Grupo> grupos;
    private Long grupoId;
    private List<Tag> tags;
    private List<Long> tagIds;


    @Inject
    private HiperlinkFacade facade;

    @Inject
    private GrupoFacade grupoFacade;

    @Inject
    private TagFacade tagFacade;

    private Hiperlink hiperlink;
    private List<Hiperlink> lista;

    @PostConstruct
    public void init(){
        hiperlink = new Hiperlink();
        lista = facade.listarTodos();

        grupos = grupoFacade.listarTodos();
        tags = tagFacade.listarTodos();
    }

    public void salvar(){
        String url = hiperlink.getUrl();
        if(url != null && !url.isBlank()){
          url = url.trim();
          if (!url.startsWith("http://") && !url.startsWith("https://")){
              url = "https://" + url;
          }
          hiperlink.setUrl(url);
        }

        if(grupoId != null){
            Grupo grupoSelecionado = grupoFacade.buscarPorId(grupoId);
            hiperlink.setGrupo(grupoSelecionado);
        }
        if(tagIds != null && !tagIds.isEmpty()){
          List<Tag> tagsSelecionadas = tagFacade.buscarPorIds(tagIds);
          hiperlink.setTags(tagsSelecionadas);
        }

        facade.salvar(hiperlink);
        hiperlink = new Hiperlink();
        lista = facade.listarTodos();
    }

    public void remover(Hiperlink h){
        facade.remover(h);
        lista = facade.listarTodos();
    }

    // ==== Getters & Setters ====
    public List<String> getColors(){return colors;}

    public Hiperlink getHiperlink() {return hiperlink;}

    public List<Hiperlink> getLista() {return lista;}

    public List<Grupo> getGrupos(){return grupos;}

    public List<Tag> getTags(){return tags;}

    public Long getGrupoId() {return grupoId;}

    public void setGrupoId(Long grupoId) {this.grupoId = grupoId;}

    public List<Long> gettagIds() {return tagIds;}

    public void settagIds(List<Long> tagIds) {this.tagIds = tagIds;}
}
