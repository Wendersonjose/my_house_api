
package com.wenderson.meuimovel.domain.financas.categoria;

import com.wenderson.meuimovel.enums.TipoCategoria;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoriaRepositoryIntegracaoTest {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    private UUID familiaId;

    @BeforeEach
    void prepararDados() {

        String bancoAtual = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
        );

        assertEquals(
                "meu_imovel_test",
                bancoAtual,
                "Os testes devem utilizar exclusivamente o banco de testes."
        );

        familiaId = UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO financas.fin_familia (id, nome)
                VALUES (?, ?)
                """,
                familiaId,
                "Família de teste"
        );
    }

    private Categoria criarCategoria(
            String nome,
            TipoCategoria tipo
    ) {

        var dados = new DadosCadastroCategoria(nome, tipo);

        return repository.saveAndFlush(
                new Categoria(familiaId, dados)
        );
    }

    @Test
    void devePersistirCategoriaNoPostgreSQL() {

        Categoria categoria = criarCategoria(
                "Supermercado",
                TipoCategoria.DESPESA
        );

        assertNotNull(categoria.getId());
        assertEquals("Supermercado", categoria.getNome());
        assertEquals(TipoCategoria.DESPESA, categoria.getTipo());
        assertTrue(categoria.isAtiva());

        UUID categoriaId = categoria.getId();

        entityManager.clear();

        Categoria encontrada = repository.findById(
                categoriaId
        ).orElseThrow();

        assertEquals("Supermercado", encontrada.getNome());
        assertEquals(TipoCategoria.DESPESA, encontrada.getTipo());
        assertEquals(familiaId, encontrada.getFamiliaId());
    }

    @Test
    void deveListarCategoriasAtivasDaFamilia() {

        criarCategoria(
                "Supermercado",
                TipoCategoria.DESPESA
        );

        criarCategoria(
                "Salário",
                TipoCategoria.RECEITA
        );

        var categorias = repository
                .findByFamiliaIdAndAtivaTrueOrderByNomeAsc(
                        familiaId
                );

        assertEquals(2, categorias.size());

        assertEquals(
                "Salário",
                categorias.get(0).getNome()
        );

        assertEquals(
                "Supermercado",
                categorias.get(1).getNome()
        );
    }

    @Test
    void deveBuscarCategoriaPorIdEFamilia() {

        Categoria categoria = criarCategoria(
                "Energia elétrica",
                TipoCategoria.DESPESA
        );

        var resultado = repository.findByIdAndFamiliaId(
                categoria.getId(),
                familiaId
        );

        assertTrue(resultado.isPresent());

        assertEquals(
                "Energia elétrica",
                resultado.orElseThrow().getNome()
        );
    }

    @Test
    void naoDeveEncontrarCategoriaDeOutraFamilia() {

        Categoria categoria = criarCategoria(
                "Água",
                TipoCategoria.DESPESA
        );

        UUID outraFamiliaId = UUID.randomUUID();

        var resultado = repository.findByIdAndFamiliaId(
                categoria.getId(),
                outraFamiliaId
        );

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveIdentificarCategoriaDuplicada() {

        criarCategoria(
                "Supermercado",
                TipoCategoria.DESPESA
        );

        boolean existe = repository
                .existsByFamiliaIdAndTipoAndNomeIgnoreCase(
                        familiaId,
                        TipoCategoria.DESPESA,
                        "supermercado"
                );

        assertTrue(existe);

        boolean existeOutroTipo = repository
                .existsByFamiliaIdAndTipoAndNomeIgnoreCase(
                        familiaId,
                        TipoCategoria.RECEITA,
                        "Supermercado"
                );

        assertFalse(existeOutroTipo);
    }

    @Test
    void deveAtualizarNomeDaCategoria() {

        Categoria categoria = criarCategoria(
                "Mercado",
                TipoCategoria.DESPESA
        );

        UUID categoriaId = categoria.getId();

        var dadosAtualizacao = new DadosAtualizacaoCategoria(
                "Compras de supermercado"
        );

        categoria.atualizar(dadosAtualizacao);

        repository.flush();
        entityManager.clear();

        Categoria atualizada = repository.findById(
                categoriaId
        ).orElseThrow();

        assertEquals(
                "Compras de supermercado",
                atualizada.getNome()
        );

        assertEquals(
                TipoCategoria.DESPESA,
                atualizada.getTipo()
        );
    }

    @Test
    void deveDesativarCategoriaSemExcluirRegistro() {

        Categoria categoria = criarCategoria(
                "Internet",
                TipoCategoria.DESPESA
        );

        UUID categoriaId = categoria.getId();

        categoria.desativar();

        repository.flush();
        entityManager.clear();

        Categoria desativada = repository.findById(
                categoriaId
        ).orElseThrow();

        assertFalse(desativada.isAtiva());

        var categoriasAtivas = repository
                .findByFamiliaIdAndAtivaTrueOrderByNomeAsc(
                        familiaId
                );

        assertTrue(categoriasAtivas.isEmpty());

        assertTrue(repository.existsById(categoriaId));
    }
}
