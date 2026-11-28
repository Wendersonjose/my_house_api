
package com.wenderson.meuimovel.domain.cotacao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosCadastroFornecedor(

        @NotBlank(message = "O nome do fornecedor é obrigatório")
        @Size(max = 150)
        String nome,

        @Size(max = 30)
        String telefone,

        @Email(message = "Informe um e-mail válido")
        @Size(max = 150)
        String email,

        String observacao

) {
}
