package br.com.barionic.webcrud.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name= "hiperlinks")
public class Hiperlink implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 5000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Cor color; //= Cor.PRETO;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @ManyToMany
    @JoinTable(
            name = "hiperlink_tag",
            joinColumns = @JoinColumn(name = "hiperlink_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @Column(name = "data_criacao", nullable=false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist(){
        this.dataCriacao = LocalDateTime.now();
    }

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate(){
        this.dataAtualizacao = LocalDateTime.now();
    }

    public String getDataCriacaoFormatada(){
        return dataCriacao != null ? dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getDataAtualizacaoFormatada(){
        return dataAtualizacao != null ? dataAtualizacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getTagsFormatadas(){
        if (tags == null || tags.isEmpty()) return "-";
        return tags.stream().map(Tag::getTagName).collect(Collectors.joining(", "));
    }

    // ==== Getters & Setters ====
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Cor getColor() {
        return color;
    }

    public void setColor(Cor color) {
        this.color = color;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getOrdem() {return ordem;}

    public void setOrdem(Integer ordem) {this.ordem = ordem;}

    public LocalDateTime getDataCriacao() {return dataCriacao;}

    public void setDataCriacao(LocalDateTime dataCriacao) {this.dataCriacao = dataCriacao;}

    public LocalDateTime getDataAtualizacao() {return dataAtualizacao;}

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {this.dataAtualizacao = dataAtualizacao;}

    public List<String> getNotasEmTopicos() {
        if (notes == null || notes.isBlank()) return List.of();
        return Arrays.asList(notes.split("\\r?\\n"));
    }
}
