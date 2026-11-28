
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.cotacao.DadosCadastroFornecedor;
import com.wenderson.meuimovel.domain.cotacao.DadosDetalhamentoFornecedor;
import com.wenderson.meuimovel.domain.cotacao.FornecedorService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/fornecedores")
public class FornecedorController {

    private final FornecedorService service;
    private final UsuarioAutenticado usuario;

    // CONSTRUTOR

    public FornecedorController(
            FornecedorService service,
            UsuarioAutenticado usuario
    ) {
        this.service = service;
        this.usuario = usuario;
    }

    // CADASTRAR FORNECEDOR

    @PostMapping
    public ResponseEntity<DadosDetalhamentoFornecedor> cadastrar(
            @PathVariable UUID familiaId,
            @RequestBody @Valid DadosCadastroFornecedor dados
    ) {

        var fornecedor = service.cadastrar(
                familiaId,
                usuario.obterUsuarioId(),
                dados
        );

        URI uri = URI.create(
                "/familias/" + familiaId
                        + "/fornecedores/" + fornecedor.id()
        );

        return ResponseEntity
                .created(uri)
                .body(fornecedor);
    }

    // LISTAR FORNECEDORES

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoFornecedor>> listar(
            @PathVariable UUID familiaId
    ) {

        var fornecedores = service.listar(
                familiaId,
                usuario.obterUsuarioId()
        );

        return ResponseEntity.ok(fornecedores);
    }

    // BUSCAR FORNECEDOR

    @GetMapping("/{fornecedorId}")
    public ResponseEntity<DadosDetalhamentoFornecedor> buscar(
            @PathVariable UUID familiaId,
            @PathVariable UUID fornecedorId
    ) {

        var fornecedor = service.buscar(
                familiaId,
                usuario.obterUsuarioId(),
                fornecedorId
        );

        return ResponseEntity.ok(fornecedor);
    }
}
