package com.wenderson.meuimovel.infra.exception;

import com.wenderson.meuimovel.domain.imovel.ImovelDuplicadoException;
import com.wenderson.meuimovel.domain.imovel.ImovelNaoEncontradoException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ImovelDuplicadoException.class)
    public ResponseEntity<DadosErro> tratarErroImovelDuplicado(
            ImovelDuplicadoException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new DadosErro(exception.getMessage()));
    }

    @ExceptionHandler(ImovelNaoEncontradoException.class)
    public ResponseEntity<DadosErro> tratarErroImovelNaoEncontrado(
            ImovelNaoEncontradoException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new DadosErro(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DadosErroValidacao> tratarErroValidacao(
            MethodArgumentNotValidException exception
    ) {

        var erros = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DetalheErro::new)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(new DadosErroValidacao(
                        HttpStatus.BAD_REQUEST.value(),
                        "Dados inválidos",
                        erros
                ));
    }

    private record DadosErro(
            String mensagem
    ) {
    }

    private record DetalheErro(
            String campo,
            String mensagem
    ) {
        private DetalheErro(FieldError erro) {
            this(
                    erro.getField(),
                    erro.getDefaultMessage()
            );
        }
    }

    private record DadosErroValidacao(
            int status,
            String mensagem,
            List<DetalheErro> erros
    ) {
    }
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<DadosErro> tratarErroConcorrencia(
            OptimisticLockingFailureException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new DadosErro(
                        "O registro foi alterado por outra operação. "
                                + "Atualize os dados e tente novamente."
                ));
    }
}
