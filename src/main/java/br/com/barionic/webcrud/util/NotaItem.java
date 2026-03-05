package br.com.barionic.webcrud.util;

public class NotaItem {
    private boolean concluido;
    private String texto;

    public NotaItem(boolean concluido, String texto){
        this.concluido = concluido;
        this.texto = texto;
    }

    public NotaItem(String texto){
        this.concluido = false;
        this.texto = texto;
    }

    public boolean isConcluido() {return concluido;}
    public void setConcluido(boolean concluido) {this.concluido = concluido;}

    public String getTexto() {return texto;}
    public void setTexto(String texto) {this.texto = texto;}
}
