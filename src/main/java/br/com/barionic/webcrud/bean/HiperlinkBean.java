package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.entity.Cor;
import br.com.barionic.webcrud.entity.Grupo;
import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.entity.Tag;
import br.com.barionic.webcrud.exception.RegraNegocioException;
import br.com.barionic.webcrud.facade.GrupoFacade;
import br.com.barionic.webcrud.facade.HiperlinkFacade;
import br.com.barionic.webcrud.facade.TagFacade;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("hiperlinkBean")
@ViewScoped
public class HiperlinkBean implements Serializable{
    private List<Grupo> grupos;
    private Long grupoId;
    private List<Tag> tags;
    private List<Long> tagIds;

    private String filtroNome;
    private Long filtroGrupoId;
    private Long filtroTagId;
    private Cor filtroCor;

    private static final Long SEM_GRUPO = -1L;
    private Long grupoSelecionado;

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
        carregarLista();
    }

    public void salvar(){
        try{
            facade.salvar(hiperlink, grupoId, tagIds);
            hiperlink = new Hiperlink();
            lista = facade.listarTodos();
        } catch (RegraNegocioException e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            e.getMessage(), null));
        }
    }

    public void editar(Hiperlink hiperlink){
        this.hiperlink = hiperlink;
        this.grupoId = hiperlink.getGrupo() != null ? hiperlink.getGrupo().getId() : null;
        if (hiperlink.getTags() != null){
            this.tagIds = hiperlink.getTags().stream().map(Tag::getId).collect(Collectors.toList());
        }
    }

    public void remover(Hiperlink h){
        facade.remover(h.getId());
        lista = facade.listarTodos();
    }

    public void buscar(){
        lista = facade.buscarComFiltro(filtroNome, filtroGrupoId, filtroTagId, filtroCor);
    }

    public void carregarLista(){
        if (grupoSelecionado == null){
            lista = facade.listarTodos();
        }
        else if(SEM_GRUPO.equals(grupoSelecionado)){
            lista = facade.listarSemGrupoOrdenado();
        }
        else{
            lista = facade.listarPorGrupoOrdenado(grupoSelecionado);
        }
    }

    public void subir(Hiperlink atual){
        boolean semGrupo = SEM_GRUPO.equals(grupoSelecionado);
        Long grupoId = (!semGrupo ? grupoSelecionado : null);
        facade.mover(atual.getId(), grupoId, semGrupo, true);
        carregarLista();
    }

    public void descer(Hiperlink atual){
        boolean semGrupo = SEM_GRUPO.equals(grupoSelecionado);
        Long grupoId = (!semGrupo ? grupoSelecionado : null);
        facade.mover(atual.getId(), grupoId, semGrupo, false);
        carregarLista();
    }

    // ==== Getters & Setters ====
    public Hiperlink getHiperlink() {return hiperlink;}

    public List<Hiperlink> getLista() {return lista;}

    public List<Grupo> getGrupos() {return grupos;}

    public Long getGrupoId() {return grupoId;}

    public void setGrupoId(Long grupoId) {this.grupoId = grupoId;}

    public List<Tag> getTags() {return tags;}

    public List<Long> getTagIds() {return tagIds;}

    public void setTagIds(List<Long> tagIds) {this.tagIds = tagIds;}

    public Cor[] getColors(){return Cor.values();}


    public String getFiltroNome() {return filtroNome;}

    public Long getFiltroGrupoId() {return filtroGrupoId;}

    public Long getFiltroTagId() {return filtroTagId;}

    public Cor getFiltroCor() {return filtroCor;}

    public void setFiltroCor(Cor filtroCor) {this.filtroCor = filtroCor;}

    public void setFiltroNome(String filtroNome) {this.filtroNome = filtroNome;}

    public void setFiltroTagId(Long filtroTagId) {this.filtroTagId = filtroTagId;}

    public void setFiltroGrupoId(Long filtroGrupoId) {this.filtroGrupoId = filtroGrupoId;}

    public Long getGrupoSelecionado() {return grupoSelecionado;}

    public void setGrupoSelecionado(Long grupoSelecionado) {this.grupoSelecionado = grupoSelecionado;}
}
