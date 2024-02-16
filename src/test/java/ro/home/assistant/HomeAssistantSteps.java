package ro.home.assistant;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.homeAssistant.*;
import ro.sheet.GoogleSheet;
import ro.shelly.Item;

import java.util.List;

@Slf4j
public class HomeAssistantSteps extends TestBase {

    private static final String shellyDevicesSpreadsheetId = "1APk6GyGLQH-FqaCIBfqIWc6KjXnM7zw-2ShRAL5c15I";
    private final HomeAssistant homeAssistant = new HomeAssistant();

    private static List<Item> devices;

    @And("I login in HA")
    public void iLoginInHA() {
        UserCredentials credentials = new UserCredentials();
        homeAssistant.login(credentials.getHomeAssistantName(), credentials.getHomeAssistantPassword());
    }

    @And("I create automation in HA with triggers:")
    public void iCreateAutomationInHA(List<Trigger> triggers) {
        homeAssistant.openSettings();
        homeAssistant.doOpenSettingsItem("Automations & Scenes");
        homeAssistant.addTriggers(triggers);
    }

    public static void main(String[] args) {
        Qs child = new Qs().selector("child");
        Qs qs = new Qs().selector("a").selector("b").shadow().selectors("c").nth(2).selectors("d").child(child);
        qs.selector("e");
        String s = qs.toSting();
        log.info("s:{}", s);
        //s = "return document.querySelector('a').shadowRoot.querySelector('c')";
    }

    @And("I create automation in HA with options:")
    public void iCreateAutomationInHAWithOptions(List<Option> options) {
        homeAssistant.addOptions(options);
    }

    @And("I save automation with {string} name")
    public void iSaveAutomationWithName(String name) {
        homeAssistant.saveAutomation(name);
    }

    @And("I create helpers in HA:")
    public void iCreateHelpersInHA(List<Helper> helpers) {
        homeAssistant.createHelpers(helpers);
    }

    @SneakyThrows
    @And("in HA I get Shelly devices from google sheet")
    public void iGetShellyDevicesFromGoogleSheet() {
        Sheets sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(shellyDevicesSpreadsheetId, "Devices" + "!A2:C").execute();
        List<List<Object>> values = valueRange.getValues();
        devices = values.stream().map(i -> new Item(i.get(1).toString(), i.get(0).toString(), i.get(2).toString())).toList();
    }

    @And("in HA I edit devices")
    public void inHAIEditDevices() {
        String name = devices.get(0).name();
        homeAssistant.editDevices(devices);
        Utils.sleep(1);
    }
}
