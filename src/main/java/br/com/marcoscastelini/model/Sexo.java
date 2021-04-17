package br.com.marcoscastelini.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sexo {
    MASCULINO("Masc"),
    FEMININO("Fem");

    private final String abreviacao;

}
