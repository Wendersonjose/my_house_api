
package com.wenderson.meuimovel.domain.imovel;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ImovelService {

    private final ImovelRepository repository;
    private final ValidadorCadastroImovel validador;
    private final UsuarioAutenticado usuarioAutenticado;
    private final ImovelFamiliaRepository imovelFamiliaRepository;
    private final AutorizacaoFamiliaService autorizacaoFamiliaService;

    public ImovelService(
            ImovelRepository repository,
            ValidadorCadastroImovel validador,
            UsuarioAutenticado usuarioAutenticado,
            ImovelFamiliaRepository imovelFamiliaRepository,
            AutorizacaoFamiliaService autorizacaoFamiliaService
    ) {
        this.repository = repository;
        this.validador = validador;
        this.usuarioAutenticado = usuarioAutenticado;
        this.imovelFamiliaRepository = imovelFamiliaRepository;
        this.autorizacaoFamiliaService = autorizacaoFamiliaService;
    }

    // Cadastra e vincula o imóvel à família em uma única transação.
    @Transactional
    public Imovel cadastrar(
            UUID familiaId,
            DadosCadastroImovel dados
    ) {
        var usuarioId = usuarioAutenticado.obterUsuarioId();

        autorizacaoFamiliaService.verificarAdministrador(
                familiaId,
                usuarioId
        );

        validador.validar(dados);

        var imovel = new Imovel(dados);

        var imovelSalvo = repository.saveAndFlush(imovel);

        imovelFamiliaRepository.vincular(
                imovelSalvo.getId(),
                familiaId
        );

        return imovelSalvo;
    }

    public List<Imovel> listar() {
        var usuarioId = usuarioAutenticado.obterUsuarioId();

        return repository.findAllAutorizados(usuarioId);
    }

    public Imovel buscarPorId(Long id) {
        var usuarioId = usuarioAutenticado.obterUsuarioId();

        return repository.findByIdAutorizado(id, usuarioId)
                .orElseThrow(() -> new ImovelNaoEncontradoException(id));
    }

    @Transactional
    public Imovel atualizar(
            Long id,
            DadosAtualizacaoImovel dados
    ) {
        var imovel = buscarPorId(id);

        validador.validarAtualizacao(imovel, dados);

        imovel.atualizar(dados);

        return imovel;
    }
}
