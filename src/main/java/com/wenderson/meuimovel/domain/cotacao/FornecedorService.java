
package com.wenderson.meuimovel.domain.cotacao;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;
    private final AutorizacaoFamiliaService autorizacao;

    // CONSTRUTOR

    public FornecedorService(
            FornecedorRepository repository,
            AutorizacaoFamiliaService autorizacao
    ) {
        this.repository = repository;
        this.autorizacao = autorizacao;
    }

    // CADASTRAR FORNECEDOR

    @Transactional
    public DadosDetalhamentoFornecedor cadastrar(
            UUID familiaId,
            UUID usuarioId,
            DadosCadastroFornecedor dados
    ) {
        autorizacao.verificarAcesso(familiaId, usuarioId);

        var fornecedor = new Fornecedor(
                familiaId,
                dados.nome(),
                dados.telefone(),
                dados.email(),
                dados.observacao()
        );

        repository.saveAndFlush(fornecedor);

        return new DadosDetalhamentoFornecedor(fornecedor);
    }

    // LISTAR FORNECEDORES

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoFornecedor> listar(
            UUID familiaId,
            UUID usuarioId
    ) {
        autorizacao.verificarAcesso(familiaId, usuarioId);

        return repository
                .findAllByFamiliaIdOrderByNomeAsc(familiaId)
                .stream()
                .map(DadosDetalhamentoFornecedor::new)
                .toList();
    }

    // BUSCAR FORNECEDOR

    @Transactional(readOnly = true)
    public DadosDetalhamentoFornecedor buscar(
            UUID familiaId,
            UUID usuarioId,
            UUID fornecedorId
    ) {
        autorizacao.verificarAcesso(familiaId, usuarioId);

        var fornecedor = repository
                .findByIdAndFamiliaId(fornecedorId, familiaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fornecedor não encontrado."
                ));

        return new DadosDetalhamentoFornecedor(fornecedor);
    }
}
