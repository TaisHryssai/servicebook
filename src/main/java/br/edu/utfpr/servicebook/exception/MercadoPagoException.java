package br.edu.utfpr.servicebook.exception;

public class MercadoPagoException extends RuntimeException {
    public MercadoPagoException(String message) {
        super(message);
    }
}