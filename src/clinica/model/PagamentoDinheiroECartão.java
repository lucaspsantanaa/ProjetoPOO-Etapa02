public class PagamentoDinheiro extends Pagamento {

    public PagamentoDinheiro(double valor) {
        super(valor);
    }

    @Override
    public double calcularValorFinal() {
        return valor * 0.95; // 5% de desconto
    }
}

public class PagamentoCartao extends Pagamento {

    public PagamentoCartao(double valor) {
        super(valor);
    }

    @Override
    public double calcularValorFinal() {
        return valor * 1.02; // 2% de taxa
    }
}
