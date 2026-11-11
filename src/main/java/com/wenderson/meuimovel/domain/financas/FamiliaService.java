
package com.wenderson.meuimovel.domain.financas;

import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FamiliaService {

    private final MembroFamiliaRepository repository;
    private final UsuarioAutenticado usuarioAutenticado;

    public FamiliaService(
            MembroFamiliaRepository repository,
            UsuarioAutenticado usuarioAutenticado
    ) {
        this.repository = repository;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    @Transactional(readOnly = true)
    public List<DadosFamiliaUsuario> listarMinhasFamilias() {

        var usuarioId = usuarioAutenticado.obterUsuarioId();

        return repository.listarFamiliasAtivas(usuarioId);
    }
}
