package ro.sheet;

import com.google.common.base.Strings;
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
            String facturaPath = item.getType().equals("Dovada") ? dovada2025() : facturi2025();
            String extrasCardPath = Strings.isNullOrEmpty(item.getExtrasCard()) ? "" : extrasCard2025();
            String decontPath = Strings.isNullOrEmpty(item.getDecont()) ? "" : decont2025();
            appUtils.uploadFileAndAddRowInFacturiAndContForItem(item, facturaPath, extrasCardPath, decontPath);
        }
    }
}
