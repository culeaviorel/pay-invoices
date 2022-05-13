package ro.eon;

import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import org.fasttrackit.util.TestBase;
import ro.electricafurnizare.oficiulvirtual.LoginView;

public class EONSteps extends TestBase {
    private LoginView loginView = new LoginView();

    @And("^I login on E-ON \"(.*?)\"/\"(.*?)\"$")
    public void iLoginOnEON(String user, String password) {
        loginView.login(user, password);
        Utils.sleep(2);
    }
}
