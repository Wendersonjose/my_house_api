
package com.wenderson.meuimovel.financas;

import com.wenderson.meuimovel.domain.financas.MembroFamiliaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        // Utiliza exclusivamente o PostgreSQL local.
        "spring.datasource.url=jdbc:postgresql://localhost:5432/meu_imovel_dev",

        // Impede migrations e alteracoes automaticas no schema.
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(MembroFamiliaRepository.class)
class MembroFamiliaRepositoryIntegracaoTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MembroFamiliaRepository repository;

    @Test
    void deveEncontrarVinculoAtivoNoPostgreSQL() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Cria uma familia ficticia.
        entityManager.createNativeQuery("""
                INSERT INTO financas.fin_familia (id, nome)
                VALUES (:familiaId, 'Familia Teste JPA')
                """)
                .setParameter("familiaId", familiaId)
                .executeUpdate();

        // Cria um membro ativo nessa familia.
        entityManager.createNativeQuery("""
                INSERT INTO financas.fin_membro (
                    familia_id,
                    usuario_id,
                    nome_exibicao,
                    papel,
                    ativo
                )
                VALUES (
                    :familiaId,
                    :usuarioId,
                    'Usuario Teste JPA',
                    'ADMIN',
                    TRUE
                )
                """)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();

        // Executa a consulta real do Repository.
        boolean possuiVinculo = repository.existeVinculoAtivo(
                familiaId,
                usuarioId
        );

        // Confirma que o PostgreSQL retornou true.
        assertTrue(possuiVinculo);
    }


    @Test
    void deveRetornarFalseQuandoMembroEstaInativo() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Cria uma familia ficticia.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_familia (id, nome)
            VALUES (:familiaId, 'Familia Teste Inativo')
            """)
                .setParameter("familiaId", familiaId)
                .executeUpdate();

        // Cadastra um membro com vinculo inativo.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_membro (
                familia_id,
                usuario_id,
                nome_exibicao,
                papel,
                ativo
            )
            VALUES (
                :familiaId,
                :usuarioId,
                'Usuario Inativo',
                'MEMBRO',
                FALSE
            )
            """)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();

        // Consulta o vinculo pelo Repository real.
        boolean possuiVinculoAtivo =
                repository.existeVinculoAtivo(
                        familiaId,
                        usuarioId
                );

        // Um membro inativo nao possui acesso autorizado.
        assertFalse(possuiVinculoAtivo);
    }


    @Test
    void deveListarSomenteFamiliasAtivasDoUsuario() {

        UUID familiaAtivaId = UUID.randomUUID();
        UUID familiaInativaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Cria duas famílias fictícias.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_familia (id, nome)
            VALUES
                (:familiaAtivaId, 'Familia Ativa Teste'),
                (:familiaInativaId, 'Familia Inativa Teste')
            """)
                .setParameter("familiaAtivaId", familiaAtivaId)
                .setParameter("familiaInativaId", familiaInativaId)
                .executeUpdate();

        // Vincula o usuário às duas famílias,
        // mas deixa somente uma associação ativa.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_membro (
                familia_id,
                usuario_id,
                nome_exibicao,
                papel,
                ativo
            )
            VALUES
                (
                    :familiaAtivaId,
                    :usuarioId,
                    'Usuario Teste',
                    'ADMIN',
                    TRUE
                ),
                (
                    :familiaInativaId,
                    :usuarioId,
                    'Usuario Teste',
                    'MEMBRO',
                    FALSE
                )
            """)
                .setParameter("familiaAtivaId", familiaAtivaId)
                .setParameter("familiaInativaId", familiaInativaId)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();

        // Executa a consulta real no PostgreSQL.
        var familias = repository.listarFamiliasAtivas(usuarioId);

        // Somente a família com vínculo ativo deve aparecer.
        assertEquals(1, familias.size());

        var familia = familias.get(0);

        assertEquals(familiaAtivaId, familia.id());
        assertEquals("Familia Ativa Teste", familia.nome());
        assertEquals("ADMIN", familia.papel());
    }


    @Test
    void naoDeveRetornarFamiliasDeOutroUsuario() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioProprietario = UUID.randomUUID();
        UUID outroUsuario = UUID.randomUUID();

        // Cria uma família fictícia.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_familia (id, nome)
            VALUES (:familiaId, 'Familia Privada Teste')
            """)
                .setParameter("familiaId", familiaId)
                .executeUpdate();

        // Vincula somente o proprietário à família.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_membro (
                familia_id,
                usuario_id,
                nome_exibicao,
                papel,
                ativo
            )
            VALUES (
                :familiaId,
                :usuarioId,
                'Usuario Proprietario',
                'ADMIN',
                TRUE
            )
            """)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioProprietario)
                .executeUpdate();

        // O proprietário deve encontrar sua família.
        var familiasProprietario =
                repository.listarFamiliasAtivas(usuarioProprietario);

        assertEquals(1, familiasProprietario.size());
        assertEquals(familiaId, familiasProprietario.get(0).id());

        // Outro usuário não pode acessar essa família.
        var familiasOutroUsuario =
                repository.listarFamiliasAtivas(outroUsuario);

        assertTrue(familiasOutroUsuario.isEmpty());
    }


}
