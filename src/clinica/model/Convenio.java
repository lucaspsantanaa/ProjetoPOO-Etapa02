public class Convenio {
    private String nome;
    private List<String> especialidadesCobertas;

    public boolean cobre(String especialidade) {
        return especialidadesCobertas.contains(especialidade);
    }
}
