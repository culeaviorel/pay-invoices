package ro.anaf;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;

@Slf4j
public class ANAFSteps extends TestBase {

    private final ANAF anaf = new ANAF();

    @And("I login in ANAF")
    public void iLoginInUp() {
        anaf.login();
    }

    @And("in ANAF I get all invoices for {int} days")
    public void inANAFIGetAllInvoicesForDays(int days) {
        anaf.getAllInvoices(days);
    }
}
