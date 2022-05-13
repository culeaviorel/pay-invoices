package ro.mymoney;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.fasttrackit.util.TestBase;

public class MyMoneySteps extends TestBase {
    private Login login = new Login();
    private View view = new View();

    @Then("I login on MyVirtual using {string} and {string}")
    public void login(String user, String pass) {
        login.login(user, pass);
    }

    @When("^I add new insert \"([^\"]*)\"/\"([^\"]*)\"/\"([^\"]*)\"$")
    public void iAddNewInsert(String denum, String categ, String sub) {
        view.addInsert(denum, categ, sub, System.getProperty("sum").replaceAll(",","."));
    }
}
