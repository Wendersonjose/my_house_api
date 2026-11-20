CREATE TABLE financas.fin_manutencao (

                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                         familia_id UUID NOT NULL,
                                         imovel_id BIGINT NOT NULL,
                                         autor_usuario_id UUID NOT NULL,

                                         titulo VARCHAR(150) NOT NULL
                                             CHECK (length(btrim(titulo)) > 0),

                                         descricao TEXT,

                                         ambiente VARCHAR(100),

                                         tipo VARCHAR(20) NOT NULL
                                             CHECK (tipo IN (
                                                             'PREVENTIVA',
                                                             'CORRETIVA',
                                                             'REFORMA',
                                                             'MELHORIA'
                                                 )),

                                         prioridade VARCHAR(10) NOT NULL DEFAULT 'MEDIA'
                                             CHECK (prioridade IN (
                                                                   'BAIXA',
                                                                   'MEDIA',
                                                                   'ALTA',
                                                                   'URGENTE'
                                                 )),

                                         status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADA'
                                             CHECK (status IN (
                                                               'PLANEJADA',
                                                               'EM_COTACAO',
                                                               'AGUARDANDO',
                                                               'EM_EXECUCAO',
                                                               'CONCLUIDA',
                                                               'CANCELADA'
                                                 )),

                                         valor_estimado NUMERIC(14,2)
                                             CHECK (valor_estimado >= 0),

                                         data_prevista DATE,
                                         data_inicio DATE,
                                         data_conclusao DATE,

                                         observacao TEXT,

                                         criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
                                         atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),

                                         CONSTRAINT uk_manutencao_familia_id
                                             UNIQUE (familia_id, id),

                                         CONSTRAINT fk_manutencao_imovel
                                             FOREIGN KEY (familia_id, imovel_id)
                                                 REFERENCES financas.fin_imovel_familia (
                                                                                         familia_id,
                                                                                         imovel_id
                                                     ),

                                         CONSTRAINT fk_manutencao_autor
                                             FOREIGN KEY (familia_id, autor_usuario_id)
                                                 REFERENCES financas.fin_membro (
                                                                                 familia_id,
                                                                                 usuario_id
                                                     ),

                                         CONSTRAINT ck_manutencao_datas
                                             CHECK (
                                                 data_conclusao IS NULL
                                                     OR (
                                                     data_inicio IS NOT NULL
                                                         AND data_conclusao >= data_inicio
                                                     )
                                                 ),

                                         CONSTRAINT ck_manutencao_conclusao
                                             CHECK (
                                                 (status = 'CONCLUIDA' AND data_conclusao IS NOT NULL)
                                                     OR
                                                 (status <> 'CONCLUIDA' AND data_conclusao IS NULL)
                                                 )
);

CREATE INDEX idx_manutencao_familia_imovel
    ON financas.fin_manutencao (
                                familia_id,
                                imovel_id
        );

CREATE INDEX idx_manutencao_familia_status
    ON financas.fin_manutencao (
                                familia_id,
                                status
        );

CREATE INDEX idx_manutencao_familia_data_prevista
    ON financas.fin_manutencao (
                                familia_id,
                                data_prevista
        );