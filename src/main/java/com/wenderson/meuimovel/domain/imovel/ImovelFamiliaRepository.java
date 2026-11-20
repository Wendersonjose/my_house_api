package com.wenderson.meuimovel.domain.imovel;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ImovelFamiliaRepository {

    private final EntityManager entityManager;

    public ImovelFamiliaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void vincular(
            Long imovelId,
            UUID familiaId
    ) {
        var sql = """
                INSERT INTO financas.fin_imovel_familia
                (familia_id, imovel_id)
                VALUES
                (:familia_id, :imovel_id)
                """;
        entityManager.createNativeQuery(sql)
                .setParameter("familia_id", familiaId)
                .setParameter("imovel_id", imovelId)
                .executeUpdate();
    }
    public boolean existeVinculo(
            UUID familiaId,
            Long imovelId
    ) {

        var sql = """
            SELECT EXISTS (
                SELECT 1
                FROM financas.fin_imovel_familia
                WHERE familia_id = :familiaId
                  AND imovel_id = :imovelId
            )
            """;

        return Boolean.TRUE.equals(
                entityManager.createNativeQuery(sql)
                        .setParameter("familiaId", familiaId)
                        .setParameter("imovelId", imovelId)
                        .getSingleResult()
        );
    }
}
