package ro.rcsrds.digicare;

import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.fasttrackit.util.BankCardDetails;
import org.fasttrackit.util.TestBase;
import ro.euplatesc.secure.CardView;

public class DigiSteps extends TestBase {
    private LoginView loginView = new LoginView();
    private InvoiceListView invoicesView = new InvoiceListView();
    private CardView cardView = new CardView();

    @Then("^I login on DIGI using \"([^\"]*)\"/\"([^\"]*)\"$")
    public void login(String user, String pass) {
        loginView.login(user, pass);
        Utils.sleep(2);
    }

    @When("^I open invoice servicii telecomunicatii list on DIGI$")
    public void openInvoiceList() {
        invoicesView.openTelecomunicatiiInvoiceAndSelectLastItem();
    }

    @When("^I open invoice energy list on DIGI$")
    public void openEnergyInvoiceAndSelectLastItem() {
        invoicesView.openEnergyInvoiceAndSelectLastItem();
    }

    @Then("^I select to pay all invoices on DIGI$")
    public void selectToPayAllInvoices() {
        invoicesView.payAll();
    }

    @Then("^I enter my EuPlatesc card details \"([^\"]*)\"/\"([^\"]*)\" that expires on \"([^\"]*)\"/\"([^\"]*)\" and owned by \"([^\"]*)\"$")
    public void enterCardDetails(String number, String cvv, String month, String year, String owner) {
        cardView.setValues(number, cvv, month, year, owner);
    }

    @Then("^I enter my EuPlatesc card details$")
    public void enterCardDetails() {
        BankCardDetails card = new BankCardDetails();
        cardView.setValues(card);
    }

    @Then("^I select to pay \"([^\"]*)\" invoices on DIGI$")
    public void iSelectToPayInvoicesOnDIGI(String amount) {
        invoicesView.payInvoice(amount);
    }
}
