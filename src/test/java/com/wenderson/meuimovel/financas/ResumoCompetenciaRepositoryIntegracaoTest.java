
package com.wenderson.meuimovel.domain.financas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(ResumoCompetenciaRepository.class)
class ResumoCompetenciaRepositoryIntegracaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResumoCompetenciaRepository repository;

    private UUID familiaId;
    private UUID usuarioId;
    private UUID contaId;
    private UUID categoriaReceitaId;
    private UUID categoriaDespesaId;

    @BeforeEach
    void prepararDados() {

        // Confirma que estamos no banco de testes local.
        String banco = jdbcTemplate.queryForObject(
                "SELECT current_database()", String.class
        );

        assertEquals("meu_imovel_test", banco);

        familiaId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        contaId = UUID.randomUUID();
        categoriaReceitaId = UUID.randomUUID();
        categoriaDespesaId = UUID.randomUUID();

        criarEstrutura(
                familiaId, usuarioId, contaId,
                categoriaReceitaId, categoriaDespesaId
        );
    }

    @Test
    void deveCalcularResumoMensalCorretamente() {

        LocalDate mes = LocalDate.of(2026, 10, 15);

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaReceitaId, "RECEITA", "EFETIVADO",
                "1200.50", mes
        );

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaDespesaId, "DESPESA", "EFETIVADO",
                "300.25", mes
        );

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaReceitaId, "RECEITA", "PREVISTO",
                "100.00", mes
        );

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaDespesaId, "DESPESA", "PREVISTO",
                "90.00", mes
        );

        // Este lancamento nao pode entrar no resumo.
        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaDespesaId, "DESPESA", "CANCELADO",
                "999.00", mes
        );

        var resultado = repository.listarPorFamilia(familiaId);

        assertEquals(1, resultado.size());

        var resumo = resultado.get(0);

        assertEquals(familiaId, resumo.familiaId());
        assertEquals(LocalDate.of(2026, 10, 1),
                resumo.mesReferencia());

        assertEquals(new BigDecimal("1200.50"),
                resumo.receitasEfetivadas());

        assertEquals(new BigDecimal("300.25"),
                resumo.despesasEfetivadas());

        assertEquals(new BigDecimal("100.00"),
                resumo.receitasPrevistas());

        assertEquals(new BigDecimal("90.00"),
                resumo.despesasPrevistas());
    }

    @Test
    void deveRetornarListaVaziaSemLancamentos() {

        var resultado = repository.listarPorFamilia(familiaId);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveIsolarOsDadosEntreFamilias() {

        UUID outraFamiliaId = UUID.randomUUID();
        UUID outroUsuarioId = UUID.randomUUID();
        UUID outraContaId = UUID.randomUUID();
        UUID outraCategoriaReceitaId = UUID.randomUUID();
        UUID outraCategoriaDespesaId = UUID.randomUUID();

        criarEstrutura(
                outraFamiliaId,
                outroUsuarioId,
                outraContaId,
                outraCategoriaReceitaId,
                outraCategoriaDespesaId
        );

        LocalDate mes = LocalDate.of(2026, 10, 15);

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaReceitaId, "RECEITA", "EFETIVADO",
                "100.00", mes
        );

        inserirLancamento(
                outraFamiliaId, outroUsuarioId, outraContaId,
                outraCategoriaReceitaId, "RECEITA", "EFETIVADO",
                "900.00", mes
        );

        var resultado = repository.listarPorFamilia(familiaId);

        assertEquals(1, resultado.size());
        assertEquals(familiaId, resultado.get(0).familiaId());

        assertEquals(new BigDecimal("100.00"),
                resultado.get(0).receitasEfetivadas());
    }

    @Test
    void deveAgruparPorMesEOrdenarMaisRecentePrimeiro() {

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaReceitaId, "RECEITA", "EFETIVADO",
                "200.00", LocalDate.of(2026, 9, 10)
        );

        inserirLancamento(
                familiaId, usuarioId, contaId,
                categoriaReceitaId, "RECEITA", "EFETIVADO",
                "300.00", LocalDate.of(2026, 10, 20)
        );

        var resultado = repository.listarPorFamilia(familiaId);

        assertEquals(2, resultado.size());

        assertEquals(LocalDate.of(2026, 10, 1),
                resultado.get(0).mesReferencia());

        assertEquals(LocalDate.of(2026, 9, 1),
                resultado.get(1).mesReferencia());
    }

    // Cria registros relacionados respeitando as FKs.
    private void criarEstrutura(
            UUID familia, UUID usuario, UUID conta,
            UUID categoriaReceita, UUID categoriaDespesa
    ) {

        jdbcTemplate.update("""
                INSERT INTO financas.fin_familia (id, nome)
                VALUES (?, 'Familia Teste Integracao')
                """, familia);

        jdbcTemplate.update("""
                INSERT INTO financas.fin_membro
                    (familia_id, usuario_id, nome_exibicao, papel)
                VALUES (?, ?, 'Usuario Teste', 'ADMIN')
                """, familia, usuario);

        jdbcTemplate.update("""
                INSERT INTO financas.fin_conta
                    (id, familia_id, titular_usuario_id, nome, tipo)
                VALUES (?, ?, ?, 'Conta Teste', 'CORRENTE')
                """, conta, familia, usuario);

        jdbcTemplate.update("""
                INSERT INTO financas.fin_categoria
                    (id, familia_id, nome, tipo)
                VALUES (?, ?, 'Receitas Teste', 'RECEITA')
                """, categoriaReceita, familia);

        jdbcTemplate.update("""
                INSERT INTO financas.fin_categoria
                    (id, familia_id, nome, tipo)
                VALUES (?, ?, 'Despesas Teste', 'DESPESA')
                """, categoriaDespesa, familia);
    }

    // Cria lancamentos ficticios para os testes.
    private void inserirLancamento(
            UUID familia,
            UUID usuario,
            UUID conta,
            UUID categoria,
            String tipo,
            String status,
            String valor,
            LocalDate competencia
    ) {

        Date efetivadoEm = "EFETIVADO".equals(status)
                ? Date.valueOf(competencia)
                : null;

        jdbcTemplate.update("""
                INSERT INTO financas.fin_lancamento (
                    familia_id,
                    autor_usuario_id,
                    responsavel_usuario_id,
                    conta_id,
                    categoria_id,
                    tipo,
                    descricao,
                    valor,
                    competencia,
                    status,
                    efetivado_em
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                familia,
                usuario,
                usuario,
                conta,
                categoria,
                tipo,
                "Lancamento de teste",
                new BigDecimal(valor),
                Date.valueOf(competencia),
                status,
                efetivadoEm
        );
    }
}
