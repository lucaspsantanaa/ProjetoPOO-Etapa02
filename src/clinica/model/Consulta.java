package clinica.model;

public class Consulta implements Agendavel, Exportavel {

    private Paciente paciente;
    private Profissional profissional;
    private String data;
    private String horario;
    private String tipo;
    private String status;
    private double multa;

    public Consulta(Paciente paciente, Profissional profissional, String data, String horario) {
        this.paciente = paciente;
        this.profissional = profissional;
        this.data = data;
        this.horario = horario;
        this.tipo = "inicial";
        this.status = "agendada";
        this.multa = 0;
    }

    public Consulta(Paciente paciente, Profissional profissional, String data,
                    String horario, String tipo) {
        this(paciente, profissional, data, horario);
        this.tipo = tipo;
    }

    @Override
    public void agendar() {
        this.status = "agendada";
    }

    @Override
    public void cancelar() {
        this.status = "cancelada";
    }

    @Override
    public void remarcar() {
        this.status = "remarcada";
    }

    public String cancelar(String motivo) {
        cancelar();
        return "Consulta cancelada. Motivo: " + motivo;
    }

    public void realizar() {
        this.status = "realizada";
    }

    @Override
    public String exportarDados() {
        return "CONSULTA;"
                + "paciente=" + paciente.getNome() + ";"
                + "cpf=" + paciente.getCpf() + ";"
                + "profissional=" + profissional.getNome() + ";"
                + "data=" + data + ";"
                + "horario=" + horario + ";"
                + "tipo=" + tipo + ";"
                + "status=" + status;
    }

    public String exibirResumo() {
        String resumo = "Paciente: " + paciente.getNome()
                + " | Prof: " + profissional.getNome()
                + " | " + data + " " + horario
                + " | Tipo: " + tipo
                + " | Status: " + status;
        if (multa > 0) {
            resumo += " | Multa: R$" + String.format("%.2f", multa);
        }
        return resumo;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        if (multa < 0) {
            throw new IllegalArgumentException("Multa nao pode ser negativa.");
        }
        this.multa = multa;
    }
}
