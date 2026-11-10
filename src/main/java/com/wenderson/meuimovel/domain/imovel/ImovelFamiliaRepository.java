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
}
