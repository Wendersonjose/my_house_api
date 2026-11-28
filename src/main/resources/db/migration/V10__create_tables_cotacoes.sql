
-- V10 - Fornecedores e cotacoes de manutencoes

CREATE TABLE financas.fin_fornecedor (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                         familia_id UUID NOT NULL,
                                         nome VARCHAR(150) NOT NULL
                                             CHECK (length(btrim(nome)) > 0),
                                         telefone VARCHAR(30),
                                         email VARCHAR(150),
                                         observacao TEXT,
                                         criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),

                                         CONSTRAINT fk_fornecedor_familia
                                             FOREIGN KEY (familia_id)
                                                 REFERENCES financas.fin_familia (id),

                                         CONSTRAINT uk_fornecedor_familia_id
                                             UNIQUE (familia_id, id)
);

CREATE INDEX idx_fornecedor_familia_nome
    ON financas.fin_fornecedor (familia_id, nome);


CREATE TABLE financas.fin_cotacao (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      familia_id UUID NOT NULL,
                                      manutencao_id UUID NOT NULL,
                                      fornecedor_id UUID NOT NULL,

                                      numero_orcamento VARCHAR(60),
                                      data_cotacao DATE NOT NULL,
                                      valor_total NUMERIC(14,2) NOT NULL
                                          CHECK (valor_total >= 0),

                                      selecionada BOOLEAN NOT NULL DEFAULT FALSE,
                                      observacao TEXT,

                                      criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
                                      atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),

                                      CONSTRAINT uk_cotacao_familia_id
                                          UNIQUE (familia_id, id),

                                      CONSTRAINT fk_cotacao_manutencao
                                          FOREIGN KEY (familia_id, manutencao_id)
                                              REFERENCES financas.fin_manutencao (familia_id, id),

                                      CONSTRAINT fk_cotacao_fornecedor
                                          FOREIGN KEY (familia_id, fornecedor_id)
                                              REFERENCES financas.fin_fornecedor (familia_id, id)
);

CREATE INDEX idx_cotacao_familia_manutencao
    ON financas.fin_cotacao (familia_id, manutencao_id);

-- Uma manutencao pode ter somente uma cotacao selecionada.
CREATE UNIQUE INDEX uk_cotacao_selecionada_manutencao
    ON financas.fin_cotacao (familia_id, manutencao_id)
    WHERE selecionada = TRUE;


CREATE TABLE financas.fin_cotacao_item (
                                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           familia_id UUID NOT NULL,
                                           cotacao_id UUID NOT NULL,

                                           descricao VARCHAR(200) NOT NULL
                                               CHECK (length(btrim(descricao)) > 0),

                                           marca VARCHAR(100),
                                           unidade VARCHAR(20) NOT NULL,

                                           quantidade NUMERIC(12,3) NOT NULL
                                               CHECK (quantidade > 0),

                                           valor_unitario NUMERIC(14,2) NOT NULL
                                               CHECK (valor_unitario >= 0),

                                           subtotal NUMERIC(14,2)
                                               GENERATED ALWAYS AS (
                                                   ROUND(quantidade * valor_unitario, 2)
                                                   ) STORED,

                                           CONSTRAINT fk_cotacao_item_cotacao
                                               FOREIGN KEY (familia_id, cotacao_id)
                                                   REFERENCES financas.fin_cotacao (familia_id, id),

                                           CONSTRAINT ck_cotacao_item_subtotal
                                               CHECK (ROUND(quantidade * valor_unitario, 2)
                                                   BETWEEN 0 AND 999999999999.99)
);

CREATE INDEX idx_cotacao_item_cotacao
    ON financas.fin_cotacao_item (familia_id, cotacao_id);
