public abstract class Pagamento implements Exportavel {

    protected double valor;

    public Pagamento(double valor) {
        this.valor = valor;
    }

    public abstract double calcularValorFinal();

    @Override
    public String exportar() {
        return "Valor: " + calcularValorFinal();
    }
}
