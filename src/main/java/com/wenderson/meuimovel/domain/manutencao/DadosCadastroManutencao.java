package com.wenderson.meuimovel.domain.manutencao;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroManutencao(

        @NotNull
        @Positive
        Long imovelId,

        @NotBlank
        @Size(max = 150)
        String titulo,

        String descricao,

        @Size(max = 100)
        String ambiente,

        @NotNull
        TipoManutencao tipo,

        PrioridadeManutencao prioridade,

        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        BigDecimal valorEstimado,

        LocalDate dataPrevista,

        String observacao

) {
}