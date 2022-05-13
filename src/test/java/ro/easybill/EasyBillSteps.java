package ro.easybill;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.fasttrackit.util.TestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Scenario: Generate receipts
 * Given I open url "https://apps.easybill.ro/cgi-bin/index.cgi?action=login"
 * And I login on EasyBill using "EMAIL"/"PASSWORD"
 * And I generate receipts for:
 * | name         | amount | date       |address                                    |
 * | Ion Petrescu | 100    | 2017-01-11 |str. Rasaritului nr. 8, ap. 2, Cluj-Napoca |
 */
public class EasyBillSteps extends TestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyBillSteps.class);

    private LoginView loginView = new LoginView();
    private ReceiptsView receiptsView = new ReceiptsView();

    @Then("^I login on EasyBill using \"([^\"]*)\"/\"([^\"]*)\"$")
    public void login(String user, String pass) {
        assertThat(loginView.login(user, pass), is(true));
    }

    @And("^I generate receipts for:$")
    public void iGenerateReceiptsFor(List<Customers> customers) {
        for (Customers c : customers) {
            LOGGER.info("{}", c.toString());
            receiptsView.generateReceiptsForNew(c);
        }
        driver.get("https://apps.easybill.ro/cgi-bin/index.cgi?action=plugin&mode=facturi&sec=desfasuratorc_frm");
    }
}
