package clinica.model;

/**
 * Superclasse ABSTRATA de todas as pessoas do sistema (Pacientes e Profissionais).
 *
 * Centraliza os atributos e comportamentos comuns (nome, cpf, telefone,
 * dataNascimento), evitando duplicacao de codigo. Por ser abstrata, NAO pode
 * ser instanciada diretamente: nao existe "uma Pessoa" no sistema, existe um
 * Paciente ou um Profissional concreto.
 *
 * R6 - CLASSE ABSTRATA: possui ao menos um metodo abstrato (exibirResumo) e ao
 * menos um metodo concreto (getIdentificacao, alem dos getters/setters).
 */
public abstract class Pessoa {

    // R1 - ENCAPSULAMENTO: todos os atributos sao privados. O acesso externo
    // ocorre exclusivamente por getters/setters. Mantemos privado (e nao
    // protegido) porque as subclasses acessam via getters, o que ja basta.
    private String nome;
    private String cpf;
    private String telefone;
    private String dataNascimento;

    // SOBRECARGA de construtor: versao minima (apenas nome e CPF).
    // 'protected': so subclasses (e o proprio pacote) constroem Pessoa, reforcando
    // que Pessoa nunca e criada isoladamente, sempre via uma subclasse concreta.
    protected Pessoa(String nome, String cpf) {
        setNome(nome);
        setCpf(cpf);
        this.telefone = "";
        this.dataNascimento = "";
    }

    // SOBRECARGA de construtor: versao completa.
    protected Pessoa(String nome, String cpf, String telefone, String dataNascimento) {
        setNome(nome);
        setCpf(cpf);
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
    }

    // Metodo ABSTRATO: cada subclasse e obrigada a definir o proprio resumo.
    // E o ponto central da ligacao dinamica/polimorfismo do relatorio unificado.
    public abstract String exibirResumo();

    // Metodo CONCRETO compartilhado por toda a hierarquia (cumpre R6).
    public String getIdentificacao() {
        return nome + " (CPF: " + cpf + ")";
    }

    // ---- Getters e Setters ----

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        // VALIDACAO (R1): nome nao pode ser vazio.
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome nao pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        // VALIDACAO (R1): CPF nao pode ser vazio (protege o estado interno do objeto).
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF nao pode ser vazio.");
        }
        this.cpf = cpf.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = (telefone == null) ? "" : telefone;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = (dataNascimento == null) ? "" : dataNascimento;
    }
}
