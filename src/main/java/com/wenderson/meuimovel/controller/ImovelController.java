package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.imovel.DadosAtualizacaoImovel;
import com.wenderson.meuimovel.domain.imovel.DadosCadastroImovel;
import com.wenderson.meuimovel.domain.imovel.DadosDetalhamentoImovel;
import com.wenderson.meuimovel.domain.imovel.ImovelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
public class ImovelController {

    private final ImovelService service;

    public ImovelController(ImovelService service) {
        this.service = service;
    }

    // Cadastra um imóvel vinculado a uma família.
    // Somente um administrador ativo pode realizar o cadastro.
    @PostMapping("/familias/{familiaId}/imoveis")
    public ResponseEntity<DadosDetalhamentoImovel> cadastrar(
            @PathVariable UUID familiaId,
            @RequestBody @Valid DadosCadastroImovel dados,
            UriComponentsBuilder uriBuilder
    ) {

        var imovel = service.cadastrar(familiaId, dados);

        var uri = uriBuilder
                .path("/imoveis/{id}")
                .buildAndExpand(imovel.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(new DadosDetalhamentoImovel(imovel));
    }

    // Lista somente imóveis autorizados para o usuário.
    @GetMapping("/imoveis")
    public ResponseEntity<List<DadosDetalhamentoImovel>> listar() {

        var imoveis = service.listar();

        var dados = imoveis.stream()
                .map(DadosDetalhamentoImovel::new)
                .toList();

        return ResponseEntity.ok(dados);
    }

    // Consulta um imóvel respeitando a autorização familiar.
    @GetMapping("/imoveis/{id}")
    public ResponseEntity<DadosDetalhamentoImovel> detalhar(
            @PathVariable Long id
    ) {

        var imovel = service.buscarPorId(id);

        return ResponseEntity.ok(
                new DadosDetalhamentoImovel(imovel)
        );
    }

    // Atualiza parcialmente um imóvel autorizado.
    @PatchMapping("/imoveis/{id}")
    public ResponseEntity<DadosDetalhamentoImovel> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoImovel dados
    ) {

        var imovel = service.atualizar(id, dados);

        return ResponseEntity.ok(
                new DadosDetalhamentoImovel(imovel)
        );
    }
}
