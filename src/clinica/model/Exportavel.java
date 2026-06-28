package clinica.model;

/**
 * CONTRATO (interface) de exportacao de dados.
 *
 * Qualquer entidade que implemente Exportavel sabe produzir uma representacao
 * textual padronizada de si mesma (para backup, auditoria ou integracao com
 * outros sistemas). E implementada por Consulta, Atendimento e Pagamento (e
 * suas subclasses), permitindo que o sistema exporte qualquer uma delas de
 * forma uniforme atraves de uma unica referencia do tipo Exportavel
 * (polimorfismo via interface).
 */
public interface Exportavel {

    String exportarDados();
}
