package ro.sheet;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;

import java.util.List;

@Slf4j
public class GoogleSheetSteps extends TestBase {

    private final AppUtils appUtils = new AppUtils();

    @And("I add in Facturi or Bonuri in google sheet:")
    public void iAddInFacturiOrBonuriInGoogleSheet(List<ItemTO> items) {
        for (ItemTO item : items) {
            String location = item.getType().equals("Dovada") ? dovada() : facturi();
            appUtils.uploadFileAndAddRowInFacturiAndContForItem(item, location);
        }
    }
}
