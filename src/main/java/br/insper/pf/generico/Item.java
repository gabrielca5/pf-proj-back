package br.insper.pf.generico;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Modelo generico: um campo de cada tipo mais comum. Renomeie e apague o que nao usar.
@Entity
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String nome;

    private String descricao;

    @NotNull
    @Min(0)
    private Double valor;

    private Boolean ativo = true;

    // preenchidos pelo back
    private String emailCriador;
    private LocalDateTime dataCriacao;
}
