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

    private final String label;

    Cor(String label){
        this.label = label;
    }

    public String getlabel(){
        return label;
    }

}
