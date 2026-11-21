package com.wenderson.meuimovel.domain.manutencao;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.domain.imovel.ImovelFamiliaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class ManutencaoService {

    private final ManutencaoRepository repository;
    private final AutorizacaoFamiliaService autorizacao;
    private final ImovelFamiliaRepository imovelFamiliaRepository;

    // CONSTRUTOR

    public ManutencaoService(
            ManutencaoRepository repository,
            AutorizacaoFamiliaService autorizacao,
            ImovelFamiliaRepository imovelFamiliaRepository
    ) {
        this.repository = repository;
        this.autorizacao = autorizacao;
        this.imovelFamiliaRepository = imovelFamiliaRepository;
    }

    // CADASTRAR MANUTENÇÃO

    @Transactional
    public DadosDetalhamentoManutencao cadastrar(
            UUID familiaId,
            UUID usuarioId,
            DadosCadastroManutencao dados
    ) {

        autorizacao.verificarAcesso(familiaId, usuarioId);

        validarImovel(familiaId, dados.imovelId());

        var manutencao = new Manutencao(
                familiaId,
                usuarioId,
                dados
        );

        repository.saveAndFlush(manutencao);

        return new DadosDetalhamentoManutencao(manutencao);
    }

    // LISTAR MANUTENÇÕES

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoManutencao> listar(
            UUID familiaId,
            UUID usuarioId
    ) {

        autorizacao.verificarAcesso(familiaId, usuarioId);

        return repository
                .findAllByFamiliaIdOrderByCriadoEmDesc(familiaId)
                .stream()
                .map(DadosDetalhamentoManutencao::new)
                .toList();
    }

    // BUSCAR MANUTENÇÃO

    @Transactional(readOnly = true)
    public DadosDetalhamentoManutencao buscar(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId
    ) {

        autorizacao.verificarAcesso(familiaId, usuarioId);

        var manutencao = localizar(familiaId, manutencaoId);

        return new DadosDetalhamentoManutencao(manutencao);
    }

    // ATUALIZAR MANUTENÇÃO

    @Transactional
    public DadosDetalhamentoManutencao atualizar(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId,
            DadosAtualizacaoManutencao dados
    ) {

        autorizacao.verificarAcesso(familiaId, usuarioId);

        var manutencao = localizar(familiaId, manutencaoId);

        try {

            manutencao.atualizar(dados);

        } catch (IllegalStateException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }

        repository.flush();

        return new DadosDetalhamentoManutencao(manutencao);
    }

    // COLOCAR EM COTAÇÃO

    @Transactional
    public DadosDetalhamentoManutencao colocarEmCotacao(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId
    ) {

        return alterarStatus(
                familiaId,
                usuarioId,
                manutencaoId,
                Manutencao::colocarEmCotacao
        );
    }

    // COLOCAR EM ESPERA

    @Transactional
    public DadosDetalhamentoManutencao aguardar(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId
    ) {

        return alterarStatus(
                familiaId,
                usuarioId,
                manutencaoId,
                Manutencao::aguardar
        );
    }

    // INICIAR MANUTENÇÃO

    @Transactional
    public DadosDetalhamentoManutencao iniciar(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId
    ) {

        return alterarStatus(
                familiaId,
                usuarioId,
                manutencaoId,
                Manutencao::iniciar
        );
    }

    // CONCLUIR MANUTENÇÃO

    @Transactional
    public DadosDetalhamentoManutencao concluir(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId
    ) {

        return alterarStatus(
                familiaId,
                usuarioId,
                manutencaoId,
                Manutencao::concluir
        );
    }

    // CANCELAR MANUTENÇÃO

    @Transactional
    public DadosDetalhamentoManutencao cancelar(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId
    ) {

        return alterarStatus(
                familiaId,
                usuarioId,
                manutencaoId,
                Manutencao::cancelar
        );
    }

    // MÉTODOS PRIVADOS AUXILIARES

    private Manutencao localizar(
            UUID familiaId,
            UUID manutencaoId
    ) {

        return repository
                .findByIdAndFamiliaId(manutencaoId, familiaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Manutenção não encontrada."
                ));
    }

    private void validarImovel(
            UUID familiaId,
            Long imovelId
    ) {

        if (!imovelFamiliaRepository.existeVinculo(
                familiaId,
                imovelId
        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Imóvel não vinculado a esta família."
            );
        }
    }

    private DadosDetalhamentoManutencao alterarStatus(
            UUID familiaId,
            UUID usuarioId,
            UUID manutencaoId,
            Consumer<Manutencao> operacao
    ) {

        autorizacao.verificarAcesso(familiaId, usuarioId);

        var manutencao = localizar(familiaId, manutencaoId);

        try {

            operacao.accept(manutencao);

        } catch (IllegalStateException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }

        repository.flush();

        return new DadosDetalhamentoManutencao(manutencao);
    }
}