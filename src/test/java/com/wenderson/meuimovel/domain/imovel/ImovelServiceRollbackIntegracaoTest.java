
package com.wenderson.meuimovel.domain.imovel;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        ValidadorCadastroImovel.class
})
class ImovelServiceRollbackIntegracaoTest {

    @Autowired
    private ImovelService imovelService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private UsuarioAutenticado usuarioAutenticado;

    @MockitoBean
    private AutorizacaoFamiliaService autorizacaoFamiliaService;

    @MockitoBean
    private ImovelFamiliaRepository imovelFamiliaRepository;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deveDesfazerCadastroQuandoVinculoFalhar() {

        String banco = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
        );

        assertEquals("meu_imovel_dev", banco);

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        String descricao = "Teste rollback " + UUID.randomUUID();

        when(usuarioAutenticado.obterUsuarioId())
                .thenReturn(usuarioId);

        doThrow(new IllegalStateException("Falha simulada no vinculo"))
                .when(imovelFamiliaRepository)
                .vincular(anyLong(), eq(familiaId));

        var dados = new DadosCadastroImovel(
                descricao,
                "Residencial",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
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
                "Teste de rollback"
        );

        assertThrows(
                IllegalStateException.class,
                () -> imovelService.cadastrar(familiaId, dados)
        );

        Integer quantidade = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM public.imovel
                WHERE descricao = ?
                """,
                Integer.class,
                descricao
        );

        assertEquals(0, quantidade);

        verify(imovelFamiliaRepository)
                .vincular(anyLong(), eq(familiaId));
    }
}
