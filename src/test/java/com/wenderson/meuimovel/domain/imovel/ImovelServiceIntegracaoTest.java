
package com.wenderson.meuimovel.domain.imovel;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.domain.financas.MembroFamiliaRepository;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/meu_imovel_dev",
        "spring.datasource.username=postgres",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import({
        ImovelService.class,
        ImovelFamiliaRepository.class,
        ValidadorCadastroImovel.class,
        AutorizacaoFamiliaService.class,
        MembroFamiliaRepository.class
})
class ImovelServiceIntegracaoTest {

    @Autowired
    private ImovelService imovelService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private UsuarioAutenticado usuarioAutenticado;

    @Test
    void deveCadastrarImovelEAssociarAFamilia() {

        // Confirma o banco antes de executar INSERTs.
        String banco = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
        );

        assertEquals("meu_imovel_dev", banco);

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Cria uma família exclusivamente para o teste.
        jdbcTemplate.update("""
                INSERT INTO financas.fin_familia (id, nome)
                VALUES (?, 'Familia Teste Imovel')
                """, familiaId);

        // Cadastra um administrador ativo.
        jdbcTemplate.update("""
                INSERT INTO financas.fin_membro (
                    familia_id,
                    usuario_id,
                    nome_exibicao,
                    papel,
                    ativo
                )
                VALUES (?, ?, 'Admin Teste', 'ADMIN', TRUE)
                """, familiaId, usuarioId);

        // Simula a identidade obtida do JWT.
        when(usuarioAutenticado.obterUsuarioId())
                .thenReturn(usuarioId);

        var dados = new DadosCadastroImovel(
                "Imovel de teste de integracao",
                "Residencial",
                null,
                null,
                null,
                null,
                "Rua Teste",
                "100",
                "Uberlandia",
                "MG",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal("150000.00"),
                null,
                null,
                null,
                "Teste automatizado"
        );

        // Executa o Service real.
        Imovel imovel = imovelService.cadastrar(
                familiaId,
                dados
        );

        assertNotNull(imovel.getId());

        // Verifica a associação no PostgreSQL real.
        Integer quantidade = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM financas.fin_imovel_familia
                WHERE familia_id = ?
                  AND imovel_id = ?
                """,
                Integer.class,
                familiaId,
                imovel.getId()
        );

        assertEquals(1, quantidade);
    }
}
