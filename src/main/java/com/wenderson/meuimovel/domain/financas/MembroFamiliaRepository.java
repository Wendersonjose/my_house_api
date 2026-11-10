package com.wenderson.meuimovel.domain.financas;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

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
}

