package ro.shelly;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.base.Strings;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.WebLocator;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import org.fasttrackit.util.TestBase;
import ro.sheet.GoogleSheet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ShellySteps extends TestBase {
    private static final String shellySpreadsheetId = "1KLXoDL6RQtKiVM2_c9OPSmss7Lnp-InUHgc4hLsXC2A";

    @SneakyThrows
    @And("I update shelly command")
    public void iUpdateShellyCommand() {
        Sheets sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(shellySpreadsheetId, "Comanda" + "!A1:H").execute();
        List<List<Object>> values = valueRange.getValues();
        List<Request> requests = new ArrayList<>();
        for (int i = 1; i < values.size(); i++) {
            List<Object> value = values.get(i);
            String link = (String) value.get(1);
            WebDriverConfig.getDriver().get(link);
            if (i == 1) {
                WebLocator dialog = new WebLocator().setId("CybotCookiebotDialog");
                WebLocator allowAll = new WebLocator(dialog).setId("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll");
                allowAll.ready(Duration.ofSeconds(20));
                allowAll.click();
            }
            WebLocator main = new WebLocator().setId("main");
            WebLocator priceEl = new WebLocator(main).setClasses("product-detail__price", "product-detail__price--gross");
            WebLocator spanPriceEl = new WebLocator(priceEl).setTag("span").setResultIdx(1);
            String price = spanPriceEl.getText().replaceAll("€", "");
            String pretNou;
            try {
                pretNou = (String) value.get(5);
            } catch (IndexOutOfBoundsException e) {
                pretNou = "";
            }
            if (Strings.isNullOrEmpty(pretNou) || !price.equals(pretNou)) {
                String pretVechi;
                try {
                    pretVechi = (String) value.get(4);
                } catch (IndexOutOfBoundsException e) {
                    pretVechi = "";
                }
                if (!price.equals(pretVechi)) {
                    int columnIndex = Strings.isNullOrEmpty(pretVechi) ? 4 : 5;
                    GoogleSheet.addItemForUpdate(price, i, columnIndex, 0, requests);
                }
            }
            WebLocator availabilityEl = new WebLocator(main).setClasses("product-detail__availability");
            String availability = availabilityEl.getText();
            GoogleSheet.addItemForUpdate(availability, i, 2,0, requests);
        }
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(shellySpreadsheetId, batchUpdateRequest).execute();
    }
}
