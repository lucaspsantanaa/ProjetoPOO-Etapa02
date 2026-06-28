package clinica.model;

/**
 * Horario disponivel (dia da semana + turno) como ENTIDADE propria.
 *
 * Participa de uma relacao de AGREGACAO com Profissional: um Profissional
 * possui uma lista de HorarioDisponivel, mas os horarios EXISTEM
 * INDEPENDENTEMENTE do profissional. Se o profissional for desligado, o mesmo
 * objeto HorarioDisponivel pode ser reaproveitado e atribuido a outro
 * profissional - ele nao e destruido junto com o profissional.
 */
public class HorarioDisponivel {

    private String diaSemana; // segunda, terca, quarta, quinta, sexta, sabado, domingo
    private String turno;     // manha, tarde

    public HorarioDisponivel(String diaSemana, String turno) {
        this.diaSemana = diaSemana.toLowerCase();
        this.turno = turno.toLowerCase();
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana.toLowerCase();
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno.toLowerCase();
    }

    @Override
    public String toString() {
        return diaSemana + " (" + turno + ")";
    }
}
