package ro.ase.semdam.bnr;

public class CursValutar {

    private final String moneda;
    private final double valoare;

    public CursValutar(String moneda, double valoare) {
        this.moneda = moneda;
        this.valoare = valoare;
    }

    public String getMoneda() {
        return moneda;
    }

    public double getValoare() {
        return valoare;
    }
}