package ro.leiibvb;

import io.cucumber.java.en.And;
import org.fasttrackit.util.TestBase;

import java.util.List;

/**
 * Scenario: added transaction in leiiBVB
 * Given I open url "http://www.leiibvb.ro/"
 * And I add transactions:
 * | prenume | nume  | email             | phone      | data       | hour  | symbol | amount | broker     |
 * | Viorel  | Culea | myemail@gmail.com | 0730000000 | 11/05/2017 | 11/34 | SNP    | 20.54  | Tradeville |
 */
public class LeiiBVBSteps extends TestBase {
    private LeiiBVBView leiiBVBView = new LeiiBVBView();

    @And("^I add transactions:$")
    public void iAddTransactions(List<Transaction> transactions) {
        leiiBVBView.addTransactions(transactions);
    }
}
