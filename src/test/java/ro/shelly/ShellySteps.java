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
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.sheet.GoogleSheet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ShellySteps extends TestBase {
    private static final String shellySpreadsheetId = "1KLXoDL6RQtKiVM2_c9OPSmss7Lnp-InUHgc4hLsXC2A";
    private static final String shellyDevicesSpreadsheetId = "1APk6GyGLQH-FqaCIBfqIWc6KjXnM7zw-2ShRAL5c15I";
    private final Shelly shelly = new Shelly();
    private static List<Item> devices;

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
            GoogleSheet.addItemForUpdate(availability, i, 2, 0, requests);
        }
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(shellySpreadsheetId, batchUpdateRequest).execute();
    }

    private void logAsList(List<Item> items) {
        StringBuilder stringBuilder = new StringBuilder("\nList.of(\n");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (i == 0) {
                stringBuilder.append("new Item(\"").append(item.id()).append("\",\"").append(item.name()).append("\",\"").append(item.ip()).append("\")\n");
            } else {
                stringBuilder.append(", new Item(\"").append(item.id()).append("\",\"").append(item.name()).append("\",\"").append(item.ip()).append("\")\n");
            }
        }
        stringBuilder.append(");");
        log.info(stringBuilder.toString());
    }

    @And("I login in Shelly")
    public void iLoginInShelly() {
        UserCredentials credentials = new UserCredentials();
        shelly.login(credentials.getShellyEmail(), credentials.getShellyPassword());
    }

    @And("I collect all devices")
    public void iCollectAllDevices() {
        shelly.openTab("My home");
        shelly.openTab("all devices");
        List<Item> items = shelly.collectAllCardsName();
        logAsList(items);
    }

    @SneakyThrows
    @And("I get Shelly devices from google sheet")
    public void iGetShellyDevicesFromGoogleSheet() {
        Sheets sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(shellyDevicesSpreadsheetId, "Devices" + "!A2:C").execute();
        List<List<Object>> values = valueRange.getValues();
        devices = values.stream().map(i -> new Item(i.get(1).toString(), i.get(0).toString(), i.get(2).toString())).toList();
    }

    @And("I save new ip for {string} and {string}")
    public void iSaveNewIpForAnd(String wifi, String password) {
        shelly.openTab("My home");
        shelly.openTab("all devices");
        for (Item device : devices) {
            if (!device.name().contains("EM")) {
                shelly.saveIP(device, wifi, password);
            }
        }
    }
}
