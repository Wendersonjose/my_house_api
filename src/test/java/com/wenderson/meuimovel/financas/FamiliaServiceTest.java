
package com.wenderson.meuimovel.financas;

import com.wenderson.meuimovel.domain.financas.DadosFamiliaUsuario;
import com.wenderson.meuimovel.domain.financas.FamiliaService;
import com.wenderson.meuimovel.domain.financas.MembroFamiliaRepository;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamiliaServiceTest {

    @Mock
    private MembroFamiliaRepository repository;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private FamiliaService service;

    private UUID usuarioId;

    @BeforeEach
    void preparar() {
        usuarioId = UUID.randomUUID();
    }

    @Test
    void deveListarSomenteFamiliasDoUsuarioAutenticado() {

        var familia = new DadosFamiliaUsuario(
                UUID.randomUUID(),
                "Minha Família",
                "ADMIN"
        );

        when(usuarioAutenticado.obterUsuarioId())
                .thenReturn(usuarioId);

        when(repository.listarFamiliasAtivas(usuarioId))
                .thenReturn(List.of(familia));

        var resultado = service.listarMinhasFamilias();

        assertEquals(1, resultado.size());
        assertEquals(familia, resultado.get(0));

        verify(repository).listarFamiliasAtivas(usuarioId);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoPossuiFamilias() {

        when(usuarioAutenticado.obterUsuarioId())
                .thenReturn(usuarioId);

        when(repository.listarFamiliasAtivas(usuarioId))
                .thenReturn(List.of());

        var resultado = service.listarMinhasFamilias();

        assertEquals(0, resultado.size());

        verify(repository).listarFamiliasAtivas(usuarioId);
    }
}
