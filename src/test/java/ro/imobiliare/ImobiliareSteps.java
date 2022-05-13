package ro.imobiliare;

import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.fasttrackit.util.TestBase;

/**
 * Scenario: Search
 * Given I open url "http://www.imobiliare.ro/vanzare-apartamente/cluj-napoca?id=50210550"
 * And I login on Imobiliare using "EMAIL"/"PASSWORD"
 * And I select my search
 * And I view news
 */
public class ImobiliareSteps extends TestBase {
    private LoginView loginView = new LoginView();
    private SearchView searchView = new SearchView();
    private ListView listView = new ListView();

    @Then("^I login on Imobiliare using \"([^\"]*)\"/\"([^\"]*)\"$")
    public void login(String user, String pass) {
        loginView.login(user, pass);
        Utils.sleep(2);
    }

    @And("^I select my search$")
    public void iSelectMySearch() {
        searchView.mySearch();
    }

    @And("^I view news")
    public void iViewNews() {
        listView.getData();
    }
}
