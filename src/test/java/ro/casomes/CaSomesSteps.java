package ro.casomes;

import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;

@Slf4j
public class CaSomesSteps extends TestBase {

    private final CaSomes caSomes = new CaSomes();

    @And("I login in CaSomes")
    public void iLoginInCaSomes() {
        UserCredentials credentials = new UserCredentials();
        caSomes.login(credentials.getCaSomesEmail(), credentials.getCaSomesPassword());
        Utils.sleep(1);
    }
}
