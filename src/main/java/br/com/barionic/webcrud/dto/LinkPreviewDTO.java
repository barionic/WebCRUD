package br.com.barionic.webcrud.dto;

import java.util.List;

public class LinkPreviewDTO {
    private Long id;
    private String nome;
    private String url;
    private String grupo;
    private List<String> tags;
    private List<String> notas;

    public LinkPreviewDTO() {}

    public LinkPreviewDTO(Long id, String nome, String url, String grupo, List<String> tags, List<String> notas){
        this.id = id;
        this.nome = nome;
        this.url = url;
        this.grupo = grupo;
        this.tags = tags;
        this.notas = notas;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getUrl() { return url; }
    public String getGrupo() { return grupo; }
    public List<String> getTags() { return tags; }
    public List<String> getNotas() { return notas; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setUrl(String url) { this.url = url; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setNotas(List<String> notas) { this.notas = notas; }
}
