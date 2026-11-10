package br.insper.pf.avaliacao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // preenchido pelo back com o email de quem esta logado
    private String emailAvaliador;

    @NotBlank
    @Email
    private String emailAvaliado;

    private String comentarios;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer nota;

    // preenchido pelo back
    private LocalDateTime dataAvaliacao;
}
