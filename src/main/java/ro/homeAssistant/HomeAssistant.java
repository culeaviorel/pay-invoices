package ro.homeAssistant;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import ro.shelly.Item;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeAssistant {

    public void login(String name, String password) {
        WebLocator content = new WebLocator().setClasses("card-content");
        TextField userNameEl = new TextField(content).setName("username");
        TextField passwordEl = new TextField(content).setName("password");
        WebLocator logIn = new WebLocator().setTag("mwc-button");
        RetryUtils.retry(2, () -> userNameEl.setValue(name));
        passwordEl.setValue(password);
        logIn.click();
    }

    public List<Item> collectAllNames() {
        List<Item> names = new ArrayList<>();
        String selector = getSelector("home-assistant", "home-assistant-main", "ha-panel-lovelace", "hui-root", "hui-masonry-view");
        WebElement main = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector));
        List<WebElement> elements = main.getShadowRoot().findElements(By.cssSelector("hui-entities-card"));
        for (WebElement element : elements) {
            WebElement card = element.getShadowRoot().findElement(By.cssSelector("ha-card"));
            WebElement header = card.findElement(By.className("card-header"));
            String cardName = header.getText();
            WebElement states = card.findElement(By.id("states"));
            List<WebElement> rows = states.findElements(By.tagName("div"));
            for (WebElement row : rows) {
                String text = RetryUtils.retry(2, row::getText).split("\n")[0];
                names.add(new Item(text, cardName));
            }
        }
        return names;
    }

    private String getSelector(String... selectors) {
        StringBuilder stringBuilder = new StringBuilder("return document");
        int length = selectors.length;
        for (int i = 0; i < length; i++) {
            String selector = selectors[i];
            if (i == 0) {
                stringBuilder.append(".querySelector('").append(selector).append("')");
            } else {
                stringBuilder.append(".shadowRoot.querySelector('").append(selector).append("')");
            }
        }
        return stringBuilder.toString();
    }

    private String getSelector1(String... selectors) {
        StringBuilder stringBuilder = new StringBuilder("return document");
        int length = selectors.length;
        for (int i = 0; i < length; i++) {
            String selector = selectors[i];
            if (i == 0) {
                stringBuilder.append(".querySelector('").append(selector).append("')");
            } else {
                stringBuilder.append(".shadowRoot.querySelector('").append(selector).append("')");
            }
        }
        stringBuilder.append(".shadowRoot");
        return stringBuilder.toString();
    }

    public void editDevices(List<Item> items) {
        String selector = getSelector("home-assistant", "home-assistant-main", "ha-sidebar", "paper-listbox");
        WebElement main = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector));
        WebElement element = main.findElement(By.className("configuration-container"));
        if (RetryUtils.retry(12, element::getText).equals("Settings")) {
            element.click();
            Utils.sleep(1500);
        }
        String selector1 = getSelector("home-assistant", "home-assistant-main", "ha-config-dashboard", "ha-config-navigation", "ha-navigation-list");
        WebElement main1 = (WebElement) RetryUtils.retry(Duration.ofSeconds(40), () -> WebLocatorUtils.doExecuteScript(selector1));
        if (main1 == null) {
            Utils.sleep(1);
        }
        List<WebElement> elements = RetryUtils.retry(12, () -> main1.getShadowRoot().findElements(By.cssSelector("ha-list-item")));
        for (WebElement item : elements) {
            String name = RetryUtils.retry(12, item::getText);
            if (name.contains("Devices & Services")) {
                item.click();
                Utils.sleep(500);
                break;
            }
        }
        String selector2 = getSelector("home-assistant", "home-assistant-main", "ha-config-integrations-dashboard", "hass-tabs-subpage");
        WebElement main2 = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector2));
        List<WebElement> elements2 = main2.getShadowRoot().findElements(By.cssSelector("ha-tab"));
        for (WebElement item : elements2) {
            String name = RetryUtils.retry(12, item::getText);
            if (name.equals("Devices")) {
                item.click();
                Utils.sleep(500);
                break;
            }
        }
        String selector3 = getSelector("home-assistant", "home-assistant-main", "ha-config-devices-dashboard", "hass-tabs-subpage-data-table", "ha-data-table");
        WebElement main3 = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector3));
        WebElement el1 = main3.getShadowRoot().findElement(By.cssSelector("*"));
        WebLocator el2 = new WebLocator(el1).setClasses("mdc-data-table__table");
        WebLocator item = new WebLocator(el2).setClasses("mdc-data-table__row").setRoot("//");
        int size = RetryUtils.retry(2, item::size);
        for (int i = 1; i <= size; i++) {
            item.setResultIdx(i);
            WebLocator cell = new WebLocator(item).setClasses("mdc-data-table__cell", "grows").setRoot("//");
            String name = RetryUtils.retry(12, cell::getText);
            Optional<Item> first = items.stream().filter(it -> name.contains(it.getId())).findFirst();
            if (first.isPresent()) {
                item.click();
                editDevice(first.get());
                goBack();
            }
        }
        item.setResultIdx(size - 1);
        WebLocatorUtils.scrollToWebLocator(item, -100);
        item.setResultIdx(0);
        size = RetryUtils.retry(2, item::size);
        for (int i = 1; i <= size; i++) {
            item.setResultIdx(i);
            WebLocator cell = new WebLocator(item).setClasses("mdc-data-table__cell", "grows").setRoot("//");
            String name = RetryUtils.retry(12, cell::getText);
            Optional<Item> first = items.stream().filter(it -> name.contains(it.getId())).findFirst();
            if (first.isPresent()) {
                item.click();
                editDevice(first.get());
                goBack();
            }
        }
        Utils.sleep(1);
    }

    private void goBack() {
        String selector = getSelector("home-assistant", "home-assistant-main", "ha-config-device-page", "hass-subpage", "ha-icon-button-arrow-prev", "ha-icon-button");
        WebElement backButton = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector));
        backButton.click();
    }

    private void editDevice(Item item) {
        String selector = getSelector("home-assistant", "home-assistant-main", "ha-config-device-page", "ha-icon-button");
        WebElement editButton = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector));
        editButton.click();
        String selector1 = getSelector("home-assistant", "dialog-device-registry-detail", "ha-textfield");
        WebElement textFieldParent = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(selector1));
        SearchContext shadowRoot = textFieldParent.getShadowRoot();
        WebElement textField = shadowRoot.findElement(By.cssSelector("input"));
        textField.sendKeys(item.getName());
        String selector2 = getSelector("home-assistant", "dialog-device-registry-detail");
        WebElement buttonParent = (WebElement) WebLocatorUtils.doExecuteScript(selector2);
        List<WebElement> elements = buttonParent.getShadowRoot().findElements(By.cssSelector("mwc-button"));
        for (WebElement element : elements) {
            String text = element.getText();
            if (text.equals("UPDATE")) {
                element.click();
                Utils.sleep(1000);
                String selector3 = getSelector("home-assistant", "dialog-box");
                WebElement buttonParent1 = (WebElement) WebLocatorUtils.doExecuteScript(selector3);
                if (buttonParent1 == null) {
                    Utils.sleep(1);
                }
                List<WebElement> elements1 = RetryUtils.retry(2, () -> buttonParent1.getShadowRoot().findElements(By.cssSelector("mwc-button")));
                for (WebElement element1 : elements1) {
                    String text1 = element1.getText();
                    if (text1.equals("RENAME")) {
                        element1.click();
                        break;
                    }
                }
                break;
            }
        }
    }
}
