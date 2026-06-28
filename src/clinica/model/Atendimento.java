package clinica.model;

public class Atendimento implements Exportavel {

    private Consulta consulta;
    private Profissional profissional;
    private Prontuario prontuario;

    public Atendimento(Consulta consulta, Profissional profissional, String observacoes) {
        this.consulta = consulta;
        this.profissional = profissional;
        this.prontuario = new Prontuario(observacoes, "", consulta.getData());
    }

    public Atendimento(Consulta consulta, Profissional profissional,
                       String observacoes, String diagnostico) {
        this.consulta = consulta;
        this.profissional = profissional;
        this.prontuario = new Prontuario(observacoes, diagnostico, consulta.getData());
    }

    public void adicionarProcedimento(String procedimento) {
        prontuario.adicionarProcedimento(procedimento);
    }

    public String exportarDados() {
        return "ATENDIMENTO;"
                + "paciente=" + consulta.getPaciente().getNome() + ";"
                + "profissional=" + profissional.getNome() + ";"
                + "especialidade=" + profissional.getEspecialidade() + ";"
                + "diagnostico=" + prontuario.getDiagnostico() + ";"
                + "procedimentos=" + prontuario.getProcedimentos().size();
    }

    public String exibirResumo() {
        return "Atendimento de " + consulta.getPaciente().getNome()
                + " por " + profissional.getNome()
                + " (" + profissional.getEspecialidade() + ")\n    "
                + prontuario.exibirResumo();
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }
}
