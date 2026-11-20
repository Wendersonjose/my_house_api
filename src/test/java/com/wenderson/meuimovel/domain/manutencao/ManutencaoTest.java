package com.wenderson.meuimovel.domain.manutencao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ManutencaoTest {

    @Test
    void deveValidarCicloDeVidaDaManutencao() {

        // 1. Preparar os dados
        var familiaId = UUID.randomUUID();
        var usuarioId = UUID.randomUUID();

        var dados = new DadosCadastroManutencao(
                1L,
                "Pintura residencial",
                "Pintura das paredes",
                "Sala",
                TipoManutencao.REFORMA,
                PrioridadeManutencao.MEDIA,
                new BigDecimal("1955.50"),
                LocalDate.now(),
                null
        );

        // 2. Cadastrar manutenção
        var manutencao = new Manutencao(
                familiaId, usuarioId, dados
        );

        assertEquals(StatusManutencao.PLANEJADA,
                manutencao.getStatus());

        assertEquals(familiaId, manutencao.getFamiliaId());
        assertEquals(usuarioId, manutencao.getAutorUsuarioId());
        assertEquals(1L, manutencao.getImovelId());
        assertNull(manutencao.getDataInicio());
        assertNull(manutencao.getDataConclusao());

        // 3. Impedir conclusão antes do início
        assertThrows(IllegalStateException.class,
                manutencao::concluir);

        // 4. Colocar em cotação
        manutencao.colocarEmCotacao();

        assertEquals(StatusManutencao.EM_COTACAO,
                manutencao.getStatus());

        // 5. Colocar em espera
        manutencao.aguardar();

        assertEquals(StatusManutencao.AGUARDANDO,
                manutencao.getStatus());

        // 6. Iniciar manutenção
        manutencao.iniciar();

        assertEquals(StatusManutencao.EM_EXECUCAO,
                manutencao.getStatus());

        assertNotNull(manutencao.getDataInicio());

        // 7. Concluir manutenção
        manutencao.concluir();

        assertEquals(StatusManutencao.CONCLUIDA,
                manutencao.getStatus());

        assertNotNull(manutencao.getDataConclusao());

        assertFalse(manutencao.getDataConclusao()
                .isBefore(manutencao.getDataInicio()));

        // 8. Impedir alterações de status após conclusão
        assertThrows(IllegalStateException.class,
                manutencao::cancelar);

        assertThrows(IllegalStateException.class,
                manutencao::iniciar);

        // 9. Validar cancelamento em outro registro
        var cancelada = new Manutencao(
                familiaId, usuarioId, dados
        );

        cancelada.cancelar();

        assertEquals(StatusManutencao.CANCELADA,
                cancelada.getStatus());

        assertNull(cancelada.getDataConclusao());

        assertThrows(IllegalStateException.class,
                cancelada::iniciar);
    }
}