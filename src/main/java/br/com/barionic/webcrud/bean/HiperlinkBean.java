package br.com.barionic.webcrud.bean;

import static br.com.barionic.webcrud.util.Constantes.SEM_GRUPO;
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
import java.util.Map;
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

    private Long grupoSelecionado;
    private boolean mostrarLog = false;
    private boolean modoEdicao = false;
    private List<Long> listaIds;
    private Map<Long, String> mapaNomes;

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
            String msg = modoEdicao ? "Link Editado com Sucesso!" : "Link Salvo com Sucesso!";

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));

            cancelarEdicao();//Limpar Registros
            carregarLista();
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
        modoEdicao = true;
    }

    public void remover(Hiperlink h){
        facade.remover(h.getId());
        lista = facade.listarTodos();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Link removido com sucesso!", null));
    }

    public void cancelarEdicao(){
        hiperlink = new Hiperlink();
        grupoId = null;
        tagIds = null;
        modoEdicao = false;
    }

    public void buscar(){
        lista = facade.buscarComFiltro(filtroNome, filtroGrupoId, filtroTagId, filtroCor);
        /*
        if (filtroNome != null && filtroNome.length() == 1) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Type at least 2 characters to search by name.", null));

        }
        */
    }

    public void limparFiltro(){
        filtroNome = null;
        filtroCor = null;
        filtroGrupoId = null;
        filtroTagId = null;
        carregarLista();
    }

    public void carregarLista(){
        lista = facade.listarPorSelecao(grupoSelecionado);
        listaIds = lista.stream().map(Hiperlink::getId).collect(Collectors.toList());
        mapaNomes = lista.stream().collect(Collectors.toMap(Hiperlink::getId, Hiperlink::getName));
    }

    public String buscarNomePorId(Long id){
        //if (mapaNomes == null) return "";
        return mapaNomes.getOrDefault(id, "");
    }

    public void salvarNovaOrdem(){
        facade.atualizarOrdem(listaIds);
        carregarLista();
    }

    // ==== Getters & Setters ====
    public Hiperlink getHiperlink() {return hiperlink;}

    public void setHiperlink(Hiperlink hiperlink) {this.hiperlink = hiperlink;}

    public List<Hiperlink> getLista() {return lista;}

    public List<Grupo> getGrupos() {return grupos;}

    public Long getGrupoId() {return grupoId;}

    public void setGrupoId(Long grupoId) {this.grupoId = grupoId;}

    public List<Tag> getTags() {return tags;}

    public List<Long> getTagIds() {return tagIds;}

    public void setTagIds(List<Long> tagIds) {this.tagIds = tagIds;}

    public Cor[] getColors(){return Cor.values();}

    public boolean isMostrarLog() {return mostrarLog;}

    public void setMostrarLog(boolean mostrarLog) {this.mostrarLog = mostrarLog;}


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


    public boolean isModoEdicao() {return modoEdicao;}

    public void setModoEdicao(boolean modoEdicao) {this.modoEdicao = modoEdicao;}

    public List<Long> getListaIds(){return listaIds;}

    public void setListaIds(List<Long> listaIds){this.listaIds = listaIds;}
}
