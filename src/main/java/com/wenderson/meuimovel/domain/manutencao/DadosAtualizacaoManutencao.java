package com.wenderson.meuimovel.domain.manutencao;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoManutencao(

        @NotBlank
        @Size(max = 150)
        String titulo,

        String descricao,

        @Size(max = 100)
        String ambiente,

        @NotNull
        TipoManutencao tipo,

        @NotNull
        PrioridadeManutencao prioridade,

        @PositiveOrZero
        @Digits(integer = 12, fraction = 2)
        BigDecimal valorEstimado,

        LocalDate dataPrevista,

        String observacao

) {
}