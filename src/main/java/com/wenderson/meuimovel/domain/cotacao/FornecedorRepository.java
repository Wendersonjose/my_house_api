
package com.wenderson.meuimovel.domain.cotacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FornecedorRepository
        extends JpaRepository<Fornecedor, UUID> {

    // LISTAR FORNECEDORES DA FAMÍLIA
    List<Fornecedor> findAllByFamiliaIdOrderByNomeAsc(
            UUID familiaId
    );

    // BUSCAR FORNECEDOR PERTENCENTE À FAMÍLIA
    Optional<Fornecedor> findByIdAndFamiliaId(
            UUID id,
            UUID familiaId
    );
}
