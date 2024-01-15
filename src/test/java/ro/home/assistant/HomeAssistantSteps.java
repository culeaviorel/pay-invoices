package ro.home.assistant;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.homeAssistant.HomeAssistant;
import ro.homeAssistant.Option;
import ro.homeAssistant.Qs;
import ro.homeAssistant.Trigger;

import java.util.List;

@Slf4j
public class HomeAssistantSteps extends TestBase {
    private final HomeAssistant homeAssistant = new HomeAssistant();

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
        Qs qs = new Qs().selector("a").selector("b").shadow().selectors("c").nth(2).selectors("d");
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
}
