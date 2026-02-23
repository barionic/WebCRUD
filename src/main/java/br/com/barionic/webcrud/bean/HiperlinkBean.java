package br.com.barionic.webcrud.bean;

import br.com.barionic.webcrud.entity.Hiperlink;
import br.com.barionic.webcrud.facade.HiperlinkFacade;
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

    @Inject
    private HiperlinkFacade facade;

    private Hiperlink hiperlink;
    private List<Hiperlink> lista;

    @PostConstruct
    public void init(){
        hiperlink = new Hiperlink();
        lista = facade.listarTodos();
    }

    public void salvar(){
        facade.salvar(hiperlink);
        hiperlink = new Hiperlink();
        lista = facade.listarTodos();
    }

    public void remover(Hiperlink h){
        facade.remover(h);
        lista = facade.listarTodos();
    }

    public List<String> getColors(){
        return colors;
    }

    public Hiperlink getHiperlink() {
        return hiperlink;
    }

    public List<Hiperlink> getLista() {
        return lista;
    }
}
