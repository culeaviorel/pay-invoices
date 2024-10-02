package ro.up;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.mymoney.ItemTO;
import ro.neo.Storage;

import java.util.List;

@Slf4j
public class UpSteps extends TestBase {

    private final Up up = new Up();

    @And("I login in Up")
    public void iLoginInUp() {
        UserCredentials credentials = new UserCredentials();
        up.login(credentials.getUpEmail(), credentials.getUpPassword());
    }

    @And("in Up I collect data and save in storage")
    public void inUpICollectDataAndInsertInMyVirtual() {
        List<ItemTO> items = up.collectData();
        Storage.set("items", items);
    }
}
