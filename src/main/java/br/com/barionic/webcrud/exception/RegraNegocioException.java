package br.com.barionic.webcrud.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RegraNegocioException extends RuntimeException{

    public RegraNegocioException(String mensagem){
        super(mensagem);
    }
}
