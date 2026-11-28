
package com.wenderson.meuimovel.domain.cotacao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fin_fornecedor", schema = "financas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "familia_id", nullable = false)
    private UUID familiaId;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 30)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public Fornecedor(
            UUID familiaId,
            String nome,
            String telefone,
            String email,
            String observacao
    ) {
        this.familiaId = familiaId;
        this.nome = nome.strip();
        this.telefone = telefone;
        this.email = email;
        this.observacao = observacao;
    }
}
