package ro.eon;

import com.sdl.selenium.web.utils.Utils;
import cucumber.api.java.en.Then;
import org.fasttrackit.util.TestBase;
import ro.electricafurnizare.oficiulvirtual.LoginView;

public class EONSteps extends TestBase {
    private LoginView loginView = new LoginView();

    @Then("^I login on E-ON using \"([^\"]*)\"/\"([^\"]*)\"$")
    public void login(String user, String pass) {
        loginView.login(user, pass);
        Utils.sleep(2);
    }
}
