package br.com.barionic.webcrud.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String grupoName;

    @OneToMany(mappedBy = "grupo")
    private List<Hiperlink> hiperlinks;

    // ==== Getters & Setters ====
    public Long getId() {
        return id;
    }

    public String getGrupoName() {
        return grupoName;
    }

    public void setGrupoName(String grupoName) {
        this.grupoName = grupoName;
    }

    public List<Hiperlink> getHiperlinks() {
        return hiperlinks;
    }

    public void setHiperlinks(List<Hiperlink> hiperlinks) {
        this.hiperlinks = hiperlinks;
    }

    // ==== Equals & HashCode ====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Grupo)) return false;
        Grupo grupo = (Grupo) o;
        return id != null && id.equals(grupo.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
