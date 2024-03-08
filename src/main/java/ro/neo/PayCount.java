package ro.neo;

public record PayCount(int somethingNew, int teenChellange, int casaFilip, int tanzania, int caminulFelix) {
    private static final int somethingNewTax = 0;
    private static final int teenChellangeTax = 0;
    private static final int casaFilipTax = 3;
    private static final int tanzaniaTax = 0;
    private static final int caminulFelixTax = 0;

    public int total() {
        return somethingNew + (somethingNew > 0 ? somethingNewTax : 0)
                + teenChellange + (teenChellange > 0 ? teenChellangeTax : 0)
                + casaFilip + (casaFilip > 0 ? casaFilipTax : 0)
                + tanzania + (tanzania > 0 ? tanzaniaTax : 0)
                + caminulFelix + (caminulFelix > 0 ? caminulFelixTax : 0)
                ;
    }
}
