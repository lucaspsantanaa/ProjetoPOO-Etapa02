package clinica.model;

/**
 * Psicologo: 3o nivel da hierarquia (Pessoa -> Profissional -> Psicologo).
 *
 * Atributo proprio: abordagem terapeutica (ex.: TCC, psicanalise, humanista).
 */
public class Psicologo extends Profissional {

    private String abordagem;

    public Psicologo(String nome, String cpf) {
        super(nome, cpf, "psicologia");
        this.abordagem = "nao informada";
    }

    public Psicologo(String nome, String cpf, String registro, double valor, String abordagem) {
        super(nome, cpf, "psicologia", registro, valor);
        setAbordagem(abordagem);
    }

    // Metodo proprio do nivel Psicologo (R3).
    public boolean usaAbordagem(String outra) {
        return abordagem.equalsIgnoreCase(outra);
    }

    @Override
    public void registrarEspecifico(Atendimento atendimento) {
        atendimento.adicionarProcedimento("Psicologia: abordagem " + abordagem);
    }

    @Override
    public String exibirResumo() {
        return "[PSICOLOGO] " + resumoBase() + " | Abordagem: " + abordagem;
    }

    public String getAbordagem() {
        return abordagem;
    }

    public void setAbordagem(String abordagem) {
        if (abordagem == null || abordagem.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo psicologo deve ter uma abordagem terapeutica.");
        }
        this.abordagem = abordagem.trim();
    }
}
