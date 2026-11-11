package com.wenderson.meuimovel.domain.financas;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class MembroFamiliaRepository {

    private final EntityManager entityManager;

    public MembroFamiliaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Verifica se o usuário pertence à família e está ativo.
    public boolean existeVinculoAtivo(
            UUID familiaId,
            UUID usuarioId
    ) {

        var sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM financas.fin_membro
                    WHERE familia_id = :familiaId
                      AND usuario_id = :usuarioId
                      AND ativo = TRUE
                )
                """;

        var resultado = entityManager
                .createNativeQuery(sql)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();

        return Boolean.TRUE.equals(resultado);
    }

    // Verifica se o usuário é administrador ativo da família.
    public boolean existeAdministradorAtivo(
            UUID familiaId,
            UUID usuarioId
    ) {

        var sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM financas.fin_membro
                    WHERE familia_id = :familiaId
                      AND usuario_id = :usuarioId
                      AND papel = 'ADMIN'
                      AND ativo = TRUE
                )
                """;

        var resultado = entityManager
                .createNativeQuery(sql)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();

        return Boolean.TRUE.equals(resultado);
    }


    public List<DadosFamiliaUsuario> listarFamiliasAtivas(UUID usuarioId) {

        var sql = """
            SELECT
                f.id,
                f.nome,
                m.papel
            FROM financas.fin_familia f
            INNER JOIN financas.fin_membro m
                ON m.familia_id = f.id
            WHERE m.usuario_id = :usuarioId
              AND m.ativo = TRUE
            ORDER BY f.nome, f.id
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = entityManager
                .createNativeQuery(sql)
                .setParameter("usuarioId", usuarioId)
                .getResultList();

        return resultados.stream()
                .map(linha -> new DadosFamiliaUsuario(
                        (UUID) linha[0],
                        (String) linha[1],
                        (String) linha[2]
                ))
                .toList();
    }

}

