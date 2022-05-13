package ro.electricafurnizare.oficiulvirtual;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.fasttrackit.util.BankCardDetails;
import org.fasttrackit.util.TestBase;
import ro.mobilpay.secure.CardView;

public class ElectricaSteps extends TestBase {
    private LoginView loginView = new LoginView();
    private InvoiceListView invoicesView = new InvoiceListView();
    private CardView cardView = new CardView();

    @Then("^I login on Electrica using \"([^\"]*)\"/\"([^\"]*)\"$")
    public void login(String user, String pass) {
        loginView.login(user, pass);
        Utils.sleep(2);
    }

    @When("^I open invoice list on Electrica for place \"([^\"]*)\"$")
    public void openInvoiceList(String place) {
        invoicesView.selectAll(place);
    }

    @Then("^I select to pay all invoices on Electrica$")
    public void selectToPayAllInvoices() {
        invoicesView.payAll();
    }

    @Then("^I enter my card details \"([^\"]*)\"/\"([^\"]*)\" that expires on \"([^\"]*)\"/\"([^\"]*)\" and owned by \"([^\"]*)\" on MobilPay$")
    public void enterCardDetails(String number, String cvv, String month, String year, String owner) {
        cardView.setValues(number, cvv, month, year, owner);
    }

    @Then("^I enter my card details on MobilPay$")
    public void enterCardDetails() {
        BankCardDetails card = new BankCardDetails();
        cardView.setValues(card);
    }

    @Then("^I proceed to MobilPay payment$")
    public void iProceedToSecureGWPayment() {
        cardView.pay();

//        WebDriverWait wait = new WebDriverWait(WebDriverConfig.getDriver(), 5);
//        wait.until(ExpectedConditions.alertIsPresent());
//        WebDriverConfig.getDriver().switchTo().alert().accept();
    }

    @And("^I should see the text \"([^\"]*)\"$")
    public void iShouldSeeTheText(String msg) {
        WebLocator element = new WebLocator().setClasses("ov-continut-alerta").setText(msg, SearchType.CONTAINS, SearchType.DEEP_CHILD_NODE_OR_SELF);
        element.assertReady();
    }
}
