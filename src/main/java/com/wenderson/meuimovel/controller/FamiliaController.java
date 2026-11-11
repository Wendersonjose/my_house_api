
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.DadosFamiliaUsuario;
import com.wenderson.meuimovel.domain.financas.FamiliaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/familias")
public class FamiliaController {

    private final FamiliaService service;

    public FamiliaController(FamiliaService service) {
        this.service = service;
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<DadosFamiliaUsuario>> listarMinhasFamilias() {

        var familias = service.listarMinhasFamilias();

        return ResponseEntity.ok(familias);
    }
}
