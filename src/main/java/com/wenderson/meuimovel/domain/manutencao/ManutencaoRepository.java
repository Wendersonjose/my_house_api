package com.wenderson.meuimovel.domain.manutencao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManutencaoRepository
        extends JpaRepository<Manutencao, UUID> {

    List<Manutencao> findAllByFamiliaIdOrderByCriadoEmDesc(
            UUID familiaId
    );

    Optional<Manutencao> findByIdAndFamiliaId(
            UUID id,
            UUID familiaId
    );

    List<Manutencao> findAllByFamiliaIdAndImovelIdOrderByCriadoEmDesc(
            UUID familiaId,
            Long imovelId
    );

    List<Manutencao> findAllByFamiliaIdAndStatusOrderByCriadoEmDesc(
            UUID familiaId,
            StatusManutencao status
    );
}