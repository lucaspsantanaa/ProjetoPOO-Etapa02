package clinica.model;

/**
 * ClinicoGeral: 3o nivel da hierarquia (Pessoa -> Profissional -> ClinicoGeral).
 *
 * Atributo proprio: encaminhamento (para outras especialidades).
 */
public class ClinicoGeral extends Profissional {

    private String encaminhamento;

    public ClinicoGeral(String nome, String cpf) {
        super(nome, cpf, "clinica geral");
        this.encaminhamento = "nenhum";
    }

    public ClinicoGeral(String nome, String cpf, String registro, double valor, String encaminhamento) {
        super(nome, cpf, "clinica geral", registro, valor);
        setEncaminhamento(encaminhamento);
    }

    // Metodo proprio do nivel ClinicoGeral (R3).
    public boolean possuiEncaminhamento() {
        return !encaminhamento.equalsIgnoreCase("nenhum");
    }

    @Override
    public void registrarEspecifico(Atendimento atendimento) {
        atendimento.adicionarProcedimento("Clinica Geral: encaminhamento - " + encaminhamento);
    }

    @Override
    public String exibirResumo() {
        return "[CLINICO GERAL] " + resumoBase() + " | Encaminhamento: " + encaminhamento;
    }

    public String getEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(String encaminhamento) {
        this.encaminhamento = (encaminhamento == null || encaminhamento.trim().isEmpty())
                ? "nenhum" : encaminhamento.trim();
    }
}
