package br.com.barionic.webcrud.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tagName;

    @ManyToMany(mappedBy = "tags")
    private List<Hiperlink> hiperlinks;

    // ==== Getters & Setters ====

    public Long getId() {
        return id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return id != null && id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
