package com.wenderson.meuimovel.domain.imovel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImovelRepository extends JpaRepository<Imovel, Long> {

    boolean existsByCnm(String cnm);

    boolean existsByCartorioAndMatricula(
            String cartorio,
            String matricula
    );

    boolean existsByCidadeAndUfAndCadastroMunicipal(
            String cidade,
            String uf,
            String cadastroMunicipal
    );

    boolean existsByCnmAndIdNot(
            String cnm,
            Long id
    );

    boolean existsByCartorioAndMatriculaAndIdNot(
            String cartorio,
            String matricula,
            Long id
    );

    boolean existsByCidadeAndUfAndCadastroMunicipalAndIdNot(
            String cidade,
            String uf,
            String cadastroMunicipal,
            Long id
    );

    @Query(value = """
        SELECT i.*
        FROM public.imovel i
        JOIN financas.fin_imovel_familia fif
            ON fif.imovel_id = i.id
        JOIN financas.fin_membro fm
            ON fm.familia_id = fif.familia_id
        WHERE fm.usuario_id = :usuarioId
          AND fm.ativo = TRUE
        ORDER BY i.id
        """, nativeQuery = true)
    List<Imovel> findAllAutorizados(
            @Param("usuarioId") UUID usuarioId
    );

    @Query(value = """
        SELECT i.*
        FROM public.imovel i
        JOIN financas.fin_imovel_familia fif
            ON fif.imovel_id = i.id
        JOIN financas.fin_membro fm
            ON fm.familia_id = fif.familia_id
        WHERE i.id = :imovelId
          AND fm.usuario_id = :usuarioId
          AND fm.ativo = TRUE
        """, nativeQuery = true)
    Optional<Imovel> findByIdAutorizado(
            @Param("imovelId") Long imovelId,
            @Param("usuarioId") UUID usuarioId
    );
}