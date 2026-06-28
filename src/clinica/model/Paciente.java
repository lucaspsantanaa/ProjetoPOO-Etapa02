package clinica.model;

/**
 * Paciente: especializacao de Pessoa (Paciente E-UMA Pessoa).
 *
 * Adiciona atributos proprios (idade, ativo) e uma ASSOCIACAO com Convenio.
 * Sobrescreve exibirResumo() para mostrar informacoes especificas de paciente
 * (idade, status, convenio).
 */
public class Paciente extends Pessoa {

    private int idade;
    private boolean ativo;

    // ASSOCIACAO: Paciente conhece um Convenio, mas ambos existem
    // independentemente. Pode ser null (paciente sem convenio / particular).
    private Convenio convenio;

    // SOBRECARGA 1: cadastro minimo (nome + CPF). super(...) chama o construtor
    // da superclasse Pessoa explicitamente (R3).
    public Paciente(String nome, String cpf) {
        super(nome, cpf);
        this.idade = 0;
        this.ativo = true;
        this.convenio = null;
    }

    // SOBRECARGA 2: com idade e telefone.
    public Paciente(String nome, String cpf, int idade, String telefone) {
        super(nome, cpf);
        setIdade(idade);
        setTelefone(telefone);
        this.ativo = true;
        this.convenio = null;
    }

    // SOBRECARGA 3: cadastro completo, com data de nascimento e convenio.
    public Paciente(String nome, String cpf, int idade, String telefone,
                    String dataNascimento, Convenio convenio) {
        super(nome, cpf, telefone, dataNascimento);
        setIdade(idade);
        this.ativo = true;
        this.convenio = convenio;
    }

    // Metodo proprio do nivel Paciente (R3: cada nivel adiciona >= 1 metodo).
    public void desativar() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }

    // SOBRECARGA de metodo: complementar so idade/telefone, ou tambem o convenio.
    public void complementar(int idade, String telefone) {
        setIdade(idade);
        setTelefone(telefone);
    }

    public void complementar(int idade, String telefone, Convenio convenio) {
        setIdade(idade);
        setTelefone(telefone);
        this.convenio = convenio;
    }

    // SOBRESCRITA: mesmo nome e assinatura do metodo abstrato de Pessoa, com
    // comportamento proprio. @Override garante, em tempo de compilacao, que a
    // assinatura bate com a da superclasse.
    @Override
    public String exibirResumo() {
        String status = ativo ? "Ativo" : "Inativo";
        String nomeConvenio = (convenio == null) ? "Nenhum" : convenio.getNome();
        return "[PACIENTE] " + getNome()
                + " | CPF: " + getCpf()
                + " | Idade: " + idade
                + " | Tel: " + getTelefone()
                + " | Convenio: " + nomeConvenio
                + " | Status: " + status;
    }

    // ---- Getters e Setters ----

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        // VALIDACAO (R1): idade nao pode ser negativa.
        if (idade < 0) {
            throw new IllegalArgumentException("Idade nao pode ser negativa.");
        }
        this.idade = idade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }
}
