package clinica.exception;

/**
 * Excecao de negocio: a especialidade da consulta nao e coberta pelo convenio do paciente.
 *
 * Estende Exception (excecao VERIFICADA/checked): obriga o metodo que a lanca
 * a declarar 'throws' e obriga o chamador a tratar. Isso documenta no proprio
 * contrato do metodo quais falhas de negocio podem ocorrer.
 *
 * Possui dois construtores (SOBRECARGA): um so com mensagem e outro com
 * mensagem + causa (para encadeamento de excecoes / "exception chaining").
 */
public class ConvenioNaoCobreException extends Exception {

    // SOBRECARGA 1: apenas a mensagem descritiva.
    public ConvenioNaoCobreException(String mensagem) {
        super(mensagem);
    }

    // SOBRECARGA 2: mensagem + causa original (preserva o stack trace de origem).
    public ConvenioNaoCobreException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
