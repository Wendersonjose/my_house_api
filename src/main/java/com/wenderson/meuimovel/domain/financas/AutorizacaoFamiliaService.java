
package com.wenderson.meuimovel.domain.financas;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AutorizacaoFamiliaService {

    private final MembroFamiliaRepository membroFamiliaRepository;

    public AutorizacaoFamiliaService(
            MembroFamiliaRepository membroFamiliaRepository
    ) {
        this.membroFamiliaRepository = membroFamiliaRepository;
    }

    public void verificarAcesso(
            UUID familiaId,
            UUID usuarioId
    ) {

        boolean possuiVinculoAtivo =
                membroFamiliaRepository.existeVinculoAtivo(
                        familiaId,
                        usuarioId
                );

        if (!possuiVinculoAtivo) {
            throw new AccessDeniedException(
                    "Usuario sem permissao para acessar esta familia"
            );
        }
    }

    public void verificarAdministrador(
            UUID familiaId,
            UUID usuarioId
    ) {

        boolean administradorAtivo =
                membroFamiliaRepository.existeAdministradorAtivo(
                        familiaId,
                        usuarioId
                );

        if (!administradorAtivo) {
            throw new AccessDeniedException(
                    "Usuario sem permissao administrativa nesta familia"
            );
        }
    }
}
