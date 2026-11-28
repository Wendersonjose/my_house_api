package com.wenderson.meuimovel.domain.cotacao;

import java.time.Instant;
import java.util.UUID;

public record DadosDetalhamentoFornecedor(
        UUID id,
        UUID familiaId,
        String nome,
        String telefone,
        String email,
        String observacao,
        Instant criadoEm

) {
    public DadosDetalhamentoFornecedor(Fornecedor fornecedor) {
        this(
                fornecedor.getId(),
                fornecedor.getFamiliaId(),
                fornecedor.getNome(),
                fornecedor.getTelefone(),
                fornecedor.getEmail(),
                fornecedor.getObservacao(),
                fornecedor.getCriadoEm()
        );
    }
}
