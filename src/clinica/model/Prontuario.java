package clinica.model;

import java.util.ArrayList;
import java.util.List;

public class Prontuario {

    private String observacoes;
    private String diagnostico;
    private List<String> procedimentos;
    private String dataRegistro;

    Prontuario(String observacoes, String diagnostico, String dataRegistro) {
        this.observacoes = observacoes;
        this.diagnostico = diagnostico;
        this.dataRegistro = dataRegistro;
        this.procedimentos = new ArrayList<>();
    }

    void adicionarProcedimento(String procedimento) {
        procedimentos.add(procedimento);
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public List<String> getProcedimentos() {
        return procedimentos;
    }

    public String exibirResumo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Observacoes: ").append(observacoes);
        if (diagnostico != null && !diagnostico.isEmpty()) {
            sb.append("\n    Diagnostico: ").append(diagnostico);
        }
        if (!procedimentos.isEmpty()) {
            sb.append("\n    Procedimentos: ");
            for (int i = 0; i < procedimentos.size(); i++) {
                sb.append(procedimentos.get(i));
                if (i < procedimentos.size() - 1) {
                    sb.append("; ");
                }
            }
        }
        sb.append("\n    Registrado em: ").append(dataRegistro);
        return sb.toString();
    }
}
