package br.com.barionic.webcrud.entity;

public enum Cor {
    AZUL("Azul"),
    VERDE("Verde"),
    AMARELO("Amarelo"),
    LARANJA("Laranja"),
    VERMELHO("Vermelho"),
    ROSA("Rosa"),
    ROXO("Roxo"),
    PRETO("Preto");

    private final String descricao;

    Cor(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }

}
