package clinica.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Profissional: superclasse ABSTRATA que e, ela mesma, uma especializacao de
 * Pessoa (Profissional E-UMA Pessoa). E o nivel intermediario da hierarquia de
 * 3 niveis exigida (R3): Pessoa -> Profissional -> Fisioterapeuta/Psicologo/...
 *
 * Como e abstrata, nao existe "um Profissional generico": todo profissional e
 * de uma especialidade concreta. Define o metodo abstrato registrarEspecifico,
 * que cada especialidade implementa do seu jeito.
 */
public abstract class Profissional extends Pessoa {

    private String especialidade;
    private String registroProfissional;
    private double valorConsulta;

    // AGREGACAO: o profissional possui uma lista de horarios, mas os horarios
    // existem independentemente dele (vide HorarioDisponivel).
    // ArrayList: a ordem de cadastro dos horarios importa e iteramos sobre eles.
    private List<HorarioDisponivel> horarios;

    // SOBRECARGA 1: cadastro minimo do profissional.
    protected Profissional(String nome, String cpf, String especialidade) {
        super(nome, cpf);
        this.especialidade = especialidade;
        this.registroProfissional = "";
        this.valorConsulta = 0;
        this.horarios = new ArrayList<>();
    }

    // SOBRECARGA 2: com registro e valor da consulta.
    protected Profissional(String nome, String cpf, String especialidade,
                           String registroProfissional, double valorConsulta) {
        super(nome, cpf);
        this.especialidade = especialidade;
        this.registroProfissional = registroProfissional;
        setValorConsulta(valorConsulta);
        this.horarios = new ArrayList<>();
    }

    // Metodo ABSTRATO: cada especializacao adiciona ao atendimento as informacoes
    // particulares da sua area (sessoes, abordagem, plano alimentar, etc.).
    public abstract void registrarEspecifico(Atendimento atendimento);

    // Metodo PROTEGIDO (R2): acessivel apenas pelas subclasses (especializacoes),
    // nao por classes externas. Cada subclasse usa este resumo-base ao montar o
    // seu proprio exibirResumo(), reaproveitando a parte comum.
    protected String resumoBase() {
        return getNome()
                + " | Espec: " + especialidade
                + " | Registro: " + registroProfissional
                + " | Valor: R$" + String.format("%.2f", valorConsulta)
                + " | Horarios: " + horarios.size();
    }

    // ---- Comportamento de AGREGACAO (gestao de horarios) ----

    public void adicionarHorario(HorarioDisponivel horario) {
        horarios.add(horario);
    }

    // O profissional atende naquele dia da semana?
    public boolean atendeNoDia(String diaSemana) {
        for (HorarioDisponivel h : horarios) {
            if (h.getDiaSemana().equalsIgnoreCase(diaSemana)) {
                return true;
            }
        }
        return false;
    }

    public List<HorarioDisponivel> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDisponivel> horarios) {
        this.horarios = horarios;
    }

    // ---- Getters e Setters ----

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public double getValorConsulta() {
        return valorConsulta;
    }

    public void setValorConsulta(double valorConsulta) {
        // VALIDACAO (R1): valor da consulta nao pode ser negativo.
        if (valorConsulta < 0) {
            throw new IllegalArgumentException("Valor da consulta nao pode ser negativo.");
        }
        this.valorConsulta = valorConsulta;
    }
}
