public class PagamentoConvenio extends Pagamento {

    private Convenio convenio;
    private String especialidade;

    public PagamentoConvenio(double valor, Convenio convenio, String especialidade) {
        super(valor);
        this.convenio = convenio;
        this.especialidade = especialidade;
    }

    @Override
    public double calcularValorFinal() {
        if (convenio.cobre(especialidade)) {
            return 0.0;
        }
        return valor;
    }
}
