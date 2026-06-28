package clinica.model;

/**
 * CONTRATO (interface) de comportamento "agendavel".
 *
 * Define o que toda entidade que pode ser agendada DEVE saber fazer, sem dizer
 * COMO. Quem implementa esta interface assume a obrigacao de fornecer estes
 * tres metodos. Demonstra heranca de TIPO (implements), que e multipla, em
 * oposicao a heranca de IMPLEMENTACAO (extends), que e unica.
 */
public interface Agendavel {

    void agendar();

    void cancelar();

    void remarcar();
}
