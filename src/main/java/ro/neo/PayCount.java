package ro.neo;

public record PayCount(
        int somethingNew,
        int teenChallenge,
        int casaFilip,
        int tanzania,
        int caminulFelix,
        int alegeViata,
        int apme
) {
    private static final int somethingNewTax = 0;
    private static final int teenChallengeTax = 0;
    private static final int casaFilipTax = 3;
    private static final int tanzaniaTax = 0;
    private static final int caminulFelixTax = 0;
    private static final int alegeViataTax = 3;
    private static final int apmeTax = 0;

    public int total() {
        return somethingNew + (somethingNew > 0 ? somethingNewTax : 0)
                + teenChallenge + (teenChallenge > 0 ? teenChallengeTax : 0)
                + casaFilip + (casaFilip > 0 ? casaFilipTax : 0)
                + tanzania + (tanzania > 0 ? tanzaniaTax : 0)
                + caminulFelix + (caminulFelix > 0 ? caminulFelixTax : 0)
                + alegeViata + (alegeViata > 0 ? alegeViataTax : 0)
                + apme + (apme > 0 ? apmeTax : 0)
                ;
    }
}
