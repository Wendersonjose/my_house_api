package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.manutencao.DadosAtualizacaoManutencao;
import com.wenderson.meuimovel.domain.manutencao.DadosCadastroManutencao;
import com.wenderson.meuimovel.domain.manutencao.DadosDetalhamentoManutencao;
import com.wenderson.meuimovel.domain.manutencao.ManutencaoService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/manutencoes")
public class ManutencaoController {

    private final ManutencaoService service;
    private final UsuarioAutenticado usuario;

    // CONSTRUTOR

    public ManutencaoController(
            ManutencaoService service,
            UsuarioAutenticado usuario
    ) {
        this.service = service;
        this.usuario = usuario;
    }

    // CADASTRAR MANUTENÇÃO

    @PostMapping
    public ResponseEntity<DadosDetalhamentoManutencao> cadastrar(
            @PathVariable UUID familiaId,
            @RequestBody @Valid DadosCadastroManutencao dados
    ) {

        var manutencao = service.cadastrar(
                familiaId,
                usuario.obterUsuarioId(),
                dados
        );

        URI uri = URI.create(
                "/familias/" + familiaId
                        + "/manutencoes/" + manutencao.id()
        );

        return ResponseEntity
                .created(uri)
                .body(manutencao);
    }

    // LISTAR MANUTENÇÕES

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoManutencao>> listar(
            @PathVariable UUID familiaId
    ) {

        var manutencoes = service.listar(
                familiaId,
                usuario.obterUsuarioId()
        );

        return ResponseEntity.ok(manutencoes);
    }

    // BUSCAR MANUTENÇÃO

    @GetMapping("/{manutencaoId}")
    public ResponseEntity<DadosDetalhamentoManutencao> buscar(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId
    ) {

        var manutencao = service.buscar(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId
        );

        return ResponseEntity.ok(manutencao);
    }

    // ATUALIZAR MANUTENÇÃO

    @PutMapping("/{manutencaoId}")
    public ResponseEntity<DadosDetalhamentoManutencao> atualizar(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId,
            @RequestBody @Valid DadosAtualizacaoManutencao dados
    ) {

        var manutencao = service.atualizar(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId,
                dados
        );

        return ResponseEntity.ok(manutencao);
    }

    // COLOCAR EM COTAÇÃO

    @PatchMapping("/{manutencaoId}/cotacao")
    public ResponseEntity<DadosDetalhamentoManutencao> colocarEmCotacao(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId
    ) {

        var manutencao = service.colocarEmCotacao(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId
        );

        return ResponseEntity.ok(manutencao);
    }

    // COLOCAR EM ESPERA

    @PatchMapping("/{manutencaoId}/aguardar")
    public ResponseEntity<DadosDetalhamentoManutencao> aguardar(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId
    ) {

        var manutencao = service.aguardar(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId
        );

        return ResponseEntity.ok(manutencao);
    }

    // INICIAR MANUTENÇÃO

    @PatchMapping("/{manutencaoId}/iniciar")
    public ResponseEntity<DadosDetalhamentoManutencao> iniciar(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId
    ) {

        var manutencao = service.iniciar(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId
        );

        return ResponseEntity.ok(manutencao);
    }

    // CONCLUIR MANUTENÇÃO

    @PatchMapping("/{manutencaoId}/concluir")
    public ResponseEntity<DadosDetalhamentoManutencao> concluir(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId
    ) {

        var manutencao = service.concluir(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId
        );

        return ResponseEntity.ok(manutencao);
    }

    // CANCELAR MANUTENÇÃO

    @PatchMapping("/{manutencaoId}/cancelar")
    public ResponseEntity<DadosDetalhamentoManutencao> cancelar(
            @PathVariable UUID familiaId,
            @PathVariable UUID manutencaoId
    ) {

        var manutencao = service.cancelar(
                familiaId,
                usuario.obterUsuarioId(),
                manutencaoId
        );

        return ResponseEntity.ok(manutencao);
    }
}