package com.wenderson.meuimovel.domain.manutencao;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DadosDetalhamentoManutencao(

        UUID id,
        UUID familiaId,
        Long imovelId,
        UUID autorUsuarioId,
        String titulo,
        String descricao,
        String ambiente,
        TipoManutencao tipo,
        PrioridadeManutencao prioridade,
        StatusManutencao status,
        BigDecimal valorEstimado,
        LocalDate dataPrevista,
        LocalDate dataInicio,
        LocalDate dataConclusao,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm

) {

    public DadosDetalhamentoManutencao(Manutencao manutencao) {

        this(
                manutencao.getId(),
                manutencao.getFamiliaId(),
                manutencao.getImovelId(),
                manutencao.getAutorUsuarioId(),
                manutencao.getTitulo(),
                manutencao.getDescricao(),
                manutencao.getAmbiente(),
                manutencao.getTipo(),
                manutencao.getPrioridade(),
                manutencao.getStatus(),
                manutencao.getValorEstimado(),
                manutencao.getDataPrevista(),
                manutencao.getDataInicio(),
                manutencao.getDataConclusao(),
                manutencao.getObservacao(),
                manutencao.getCriadoEm(),
                manutencao.getAtualizadoEm()
        );
    }
}