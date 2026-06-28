package clinica.model;

/**
 * Nutricionista: 3o nivel da hierarquia (Pessoa -> Profissional -> Nutricionista).
 *
 * Atributo proprio: planoAlimentar (texto descritivo).
 */
public class Nutricionista extends Profissional {

    private String planoAlimentar;

    public Nutricionista(String nome, String cpf) {
        super(nome, cpf, "nutricao");
        this.planoAlimentar = "a definir";
    }

    public Nutricionista(String nome, String cpf, String registro, double valor, String planoAlimentar) {
        super(nome, cpf, "nutricao", registro, valor);
        setPlanoAlimentar(planoAlimentar);
    }

    // Metodo proprio do nivel Nutricionista (R3).
    public boolean possuiPlanoDefinido() {
        return !planoAlimentar.equalsIgnoreCase("a definir");
    }

    @Override
    public void registrarEspecifico(Atendimento atendimento) {
        atendimento.adicionarProcedimento("Nutricao: plano alimentar - " + planoAlimentar);
    }

    @Override
    public String exibirResumo() {
        return "[NUTRICIONISTA] " + resumoBase() + " | Plano: " + planoAlimentar;
    }

    public String getPlanoAlimentar() {
        return planoAlimentar;
    }

    public void setPlanoAlimentar(String planoAlimentar) {
        this.planoAlimentar = (planoAlimentar == null || planoAlimentar.trim().isEmpty())
                ? "a definir" : planoAlimentar.trim();
    }
}
