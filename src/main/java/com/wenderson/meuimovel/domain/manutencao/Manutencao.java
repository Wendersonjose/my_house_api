package com.wenderson.meuimovel.domain.manutencao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fin_manutencao", schema = "financas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    @Column(name = "familia_id", nullable = false)
    private UUID familiaId;

    @Column(name = "imovel_id", nullable = false)
    private Long imovelId;

    @Column(name = "autor_usuario_id", nullable = false)
    private UUID autorUsuarioId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String ambiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoManutencao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PrioridadeManutencao prioridade =
            PrioridadeManutencao.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusManutencao status =
            StatusManutencao.PLANEJADA;

    @Column(name = "valor_estimado",
            precision = 14, scale = 2)
    private BigDecimal valorEstimado;

    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @CreationTimestamp
    @Column(name = "criado_em",
            nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public Manutencao(
            UUID familiaId,
            UUID autorUsuarioId,
            DadosCadastroManutencao dados) {

        this.familiaId = familiaId;
        this.autorUsuarioId = autorUsuarioId;
        this.imovelId = dados.imovelId();
        this.titulo = dados.titulo().trim();
        this.descricao = dados.descricao();
        this.ambiente = dados.ambiente();
        this.tipo = dados.tipo();

        this.prioridade = dados.prioridade() != null
                ? dados.prioridade()
                : PrioridadeManutencao.MEDIA;

        this.status = StatusManutencao.PLANEJADA;
        this.valorEstimado = dados.valorEstimado();
        this.dataPrevista = dados.dataPrevista();
        this.observacao = dados.observacao();
    }
    private void validarTransicao(StatusManutencao novoStatus) {

        boolean permitida = switch (this.status) {

            case PLANEJADA ->
                    novoStatus == StatusManutencao.EM_COTACAO
                            || novoStatus == StatusManutencao.AGUARDANDO
                            || novoStatus == StatusManutencao.EM_EXECUCAO
                            || novoStatus == StatusManutencao.CANCELADA;

            case EM_COTACAO ->
                    novoStatus == StatusManutencao.AGUARDANDO
                            || novoStatus == StatusManutencao.EM_EXECUCAO
                            || novoStatus == StatusManutencao.CANCELADA;

            case AGUARDANDO ->
                    novoStatus == StatusManutencao.EM_COTACAO
                            || novoStatus == StatusManutencao.EM_EXECUCAO
                            || novoStatus == StatusManutencao.CANCELADA;

            case EM_EXECUCAO ->
                    novoStatus == StatusManutencao.AGUARDANDO
                            || novoStatus == StatusManutencao.CONCLUIDA
                            || novoStatus == StatusManutencao.CANCELADA;

            case CONCLUIDA, CANCELADA -> false;
        };

        if (!permitida) {
            throw new IllegalStateException(
                    "Transição de status não permitida: "
                            + this.status + " -> " + novoStatus
            );
        }
    }
    public void atualizar(DadosAtualizacaoManutencao dados) {

        if (this.status == StatusManutencao.CONCLUIDA
                || this.status == StatusManutencao.CANCELADA) {

            throw new IllegalStateException(
                    "Não é permitido atualizar uma manutenção finalizada."
            );
        }

        this.titulo = dados.titulo().trim();
        this.descricao = dados.descricao();
        this.ambiente = dados.ambiente();
        this.tipo = dados.tipo();
        this.prioridade = dados.prioridade();
        this.valorEstimado = dados.valorEstimado();
        this.dataPrevista = dados.dataPrevista();
        this.observacao = dados.observacao();
    }

    public void colocarEmCotacao() {

        validarTransicao(StatusManutencao.EM_COTACAO);

        this.status = StatusManutencao.EM_COTACAO;
    }

    public void aguardar() {

        validarTransicao(StatusManutencao.AGUARDANDO);

        this.status = StatusManutencao.AGUARDANDO;
    }

    public void iniciar() {

        validarTransicao(StatusManutencao.EM_EXECUCAO);

        if (this.dataInicio == null) {
            this.dataInicio = LocalDate.now();
        }

        this.status = StatusManutencao.EM_EXECUCAO;
    }

    public void concluir() {

        validarTransicao(StatusManutencao.CONCLUIDA);

        LocalDate hoje = LocalDate.now();

        if (this.dataInicio == null
                || hoje.isBefore(this.dataInicio)) {

            throw new IllegalStateException(
                    "Data de início da manutenção inválida."
            );
        }

        this.dataConclusao = hoje;
        this.status = StatusManutencao.CONCLUIDA;
    }

    public void cancelar() {

        validarTransicao(StatusManutencao.CANCELADA);

        this.status = StatusManutencao.CANCELADA;
    }
}