package clinica.model;

/**
 * Fisioterapeuta: 3o nivel da hierarquia (Pessoa -> Profissional -> Fisioterapeuta).
 * Um Fisioterapeuta E-UM Profissional que E-UMA Pessoa.
 *
 * Atributo proprio: totalSessoesPrevistas (sessoes do plano de tratamento).
 */
public class Fisioterapeuta extends Profissional {

    private int totalSessoesPrevistas;

    public Fisioterapeuta(String nome, String cpf) {
        super(nome, cpf, "fisioterapia");
        this.totalSessoesPrevistas = 10; // padrao do plano de tratamento
    }

    // SOBRECARGA: construtor completo. super(...) chama o construtor de Profissional.
    public Fisioterapeuta(String nome, String cpf, String registro, double valor,
                          int totalSessoesPrevistas) {
        super(nome, cpf, "fisioterapia", registro, valor);
        setTotalSessoesPrevistas(totalSessoesPrevistas);
    }

    // Metodo proprio do nivel Fisioterapeuta (R3).
    public int sessoesRestantes(int sessoesRealizadas) {
        return Math.max(0, totalSessoesPrevistas - sessoesRealizadas);
    }

    // SOBRESCRITA do metodo abstrato de Profissional.
    @Override
    public void registrarEspecifico(Atendimento atendimento) {
        atendimento.adicionarProcedimento(
                "Fisioterapia: plano de " + totalSessoesPrevistas + " sessoes previstas");
    }

    // SOBRESCRITA de exibirResumo: usa o metodo PROTEGIDO resumoBase() da superclasse.
    @Override
    public String exibirResumo() {
        return "[FISIOTERAPEUTA] " + resumoBase()
                + " | Sessoes previstas: " + totalSessoesPrevistas;
    }

    public int getTotalSessoesPrevistas() {
        return totalSessoesPrevistas;
    }

    public void setTotalSessoesPrevistas(int totalSessoesPrevistas) {
        if (totalSessoesPrevistas < 0) {
            throw new IllegalArgumentException("Total de sessoes nao pode ser negativo.");
        }
        this.totalSessoesPrevistas = totalSessoesPrevistas;
    }
}
