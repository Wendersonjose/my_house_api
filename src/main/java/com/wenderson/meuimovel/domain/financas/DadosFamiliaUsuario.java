
package com.wenderson.meuimovel.domain.financas;

import java.util.UUID;

public record DadosFamiliaUsuario(
        UUID id,
        String nome,
        String papel
) {
}
