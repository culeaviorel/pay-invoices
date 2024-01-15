package ro.homeAssistant;

import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import ro.shelly.Item;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HomeAssistant {
    private final Qs mainQs = new Qs().selector("home-assistant").shadow().selector("home-assistant-main").shadow();

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

    public void editDevices(List<Item> items) {
        openSettings();
        openSettingsItem();
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

    public void openSettingsItem() {
        doOpenSettingsItem("Devices & Services");
    }

    public void doOpenSettingsItem(String itemName) {
        Qs qs = new Qs().parent(mainQs.get()).selector("ha-config-dashboard").shadow().selector("ha-config-navigation").shadow().selector("ha-navigation-list");
        WebElement main1 = (WebElement) RetryUtils.retry(Duration.ofSeconds(40), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        if (main1 == null) {
            Utils.sleep(1);
        }
        List<WebElement> elements = RetryUtils.retry(12, () -> main1.getShadowRoot().findElements(By.cssSelector("ha-list-item")));
        for (WebElement item : elements) {
            String name = RetryUtils.retry(12, item::getText);
            if (name.contains(itemName)) {
                item.click();
                Utils.sleep(500);
                break;
            }
        }
    }

    public void openSettings() {
        Qs qs = new Qs().parent(mainQs.get()).selector("ha-sidebar").shadow().selector("paper-listbox");
        WebElement main = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        WebElement element = main.findElement(By.className("configuration-container"));
        if (RetryUtils.retry(12, element::getText).equals("Settings")) {
            element.click();
            Utils.sleep(1500);
        }
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

    public void addTriggers(List<Trigger> triggers) {
        clickOn(new Qs().parent(mainQs.get()).selector("ha-automation-picker").shadow().selector("ha-fab"));
        selectItem("Create new automation", new Qs().selector("home-assistant").shadow().selector("ha-dialog-new-automation").shadow().selectors("ha-list-item"));
        for (int i = 0; i < triggers.size(); i++) {
            Trigger trigger = triggers.get(i);
            clickOn(new Qs().parent(mainQs.get()).selector("ha-automation-editor").shadow().selector("manual-automation-editor").shadow().selector("ha-automation-trigger").shadow().selector("ha-button"));
            selectItem("Device", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("ha-list-item"));
            Qs rowQs = new Qs().parent(mainQs.get()).selector("ha-automation-editor").shadow().selector("manual-automation-editor").shadow().selector("ha-automation-trigger").shadow().selectors("ha-automation-trigger-row").nth(i);
            Qs deviceQs = new Qs().parent(rowQs.get()).shadow().selector("ha-automation-trigger-device");
            WebElement inputComboBox = clickOn(new Qs().parent(deviceQs.get()).shadow().selector("ha-device-picker").shadow().selector("ha-combo-box").shadow().selector("ha-textfield"));
            inputComboBox.sendKeys(trigger.getDevice());
            selectComboBoxItem(trigger.getDevice());
            selectTriggerItem("Edit ID", new Qs().parent(rowQs.get()).shadow().selector("ha-button-menu"));
            setValue("Trigger ID", trigger.getTriggerId(), new Qs().parent(rowQs.get()).shadow().selectors("ha-textfield"));
            selectTriggerItem(trigger.getTrigger(), new Qs().parent(deviceQs.get()).shadow().selector("ha-device-trigger-picker").shadow().selector("ha-select"));
//            setValue("Above", "-100", deviceParent, "ha-form", "ha-form-float", "ha-textfield");
            Time duration = getDuration(trigger);
            setValue(duration.label(), duration.value(), new Qs().parent(deviceQs.get()).shadow().selector("ha-form").shadow().selector("ha-form-positive_time_period_dict").shadow().selector("ha-duration-input").shadow().selector("ha-base-time-input").shadow().selectors("ha-textfield"));
            Utils.sleep(100);
        }
    }

    private Time getDuration(Trigger value) {
        String label = "not suported";
        if (value.getDuration().contains("ss")) {
            label = "ss";
        } else if (value.getDuration().contains("mm")) {
            label = "mm";
        } else if (value.getDuration().contains("hh")) {
            label = "hh";
        }
        String time = value.getDuration().split(label)[0];
        return new Time(label, time);
    }

    public void addOptions(List<Option> options) {
        Qs action = new Qs().parent(mainQs.get()).selector("ha-automation-editor").shadow().selector("manual-automation-editor").shadow().selector("ha-automation-action");
        clickOn(new Qs().parent(action.get()).shadow().selector("ha-button"));
        setValue("Search action", "Choose", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("search-input").nth(0).shadow().selectors("ha-textfield"));
        selectItem("Choose", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("ha-list-item"));
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            Qs rowQs = new Qs().parent(action.get()).shadow().selector("ha-automation-action-row");
            if (i == 0) {
                clickOn(rowQs);
            } else {
                clickOn(new Qs().parent(rowQs.get()).shadow().selector("ha-automation-action-choose").shadow().selector("ha-button"));
            }
            Qs condition = new Qs().parent(rowQs.get()).shadow().selector("ha-automation-action-choose").shadow().selectors("ha-automation-condition").nth(i);
            clickOn(new Qs().parent(condition.get()).shadow().selector("ha-button"));
            setValue("Search condition", "Triggered by", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("search-input").nth(0).shadow().selectors("ha-textfield"));
            selectItem("Triggered by", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("ha-list-item"));
            Qs actionQs = new Qs().parent(rowQs.get()).shadow().selector("ha-automation-action-choose").shadow().selectors("ha-card").nth(i);
            checkItem(option.getTriggerId(), new Qs().parent(actionQs.get()).selectors("ha-automation-condition").nth(0).shadow().selector("ha-automation-condition-row").shadow().selector("ha-automation-condition-editor").shadow().selector("ha-automation-condition-trigger").shadow().selector("ha-form").shadow().selector("ha-selector").shadow().selector("ha-selector-select").shadow().selectors("ha-formfield"));

            clickOn(new Qs().parent(actionQs.get()).selectors("ha-automation-action").nth(0).shadow().selector("ha-button"));
            setValue("Search action", "Call service", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("search-input").nth(0).shadow().selectors("ha-textfield"));
            selectItem("Call service", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("ha-list-item"));
            Qs serviceQs = new Qs().parent(actionQs.get()).selectors("ha-automation-action").nth(0).shadow().selector("ha-automation-action-row").shadow().selector("ha-automation-action-service").shadow().selector("ha-service-control");
            WebElement inputComboBox = clickOn(new Qs().parent(serviceQs.get()).shadow().selector("ha-service-picker").shadow().selector("ha-combo-box").shadow().selector("ha-textfield"));
            inputComboBox.sendKeys(option.getService());
            selectComboBoxItem(option.getService());
            Qs targetQs = new Qs().parent(serviceQs.get()).shadow().selector("ha-selector").shadow().selector("ha-selector-target").shadow().selector("ha-target-picker");
            WebElement targetParent = (WebElement) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(targetQs.toSting()));
            targetParent.click();
            selectComboBoxItem(option.getDevice());
            if (!Strings.isNullOrEmpty(option.getTemperature())) {
                checkItem("Temperature", new Qs().parent(serviceQs.get()).shadow().selectors("ha-settings-row"));
                setValue("", option.getTemperature(), new Qs().parent(serviceQs.get()).shadow().selectors("ha-settings-row").nth(1).selector("ha-selector").shadow().selector("ha-selector-number").shadow().selector("ha-textfield"));
            }
            if (!Strings.isNullOrEmpty(option.getMode())) {
                checkItem("HVAC mode", new Qs().parent(serviceQs.get()).shadow().selectors("ha-settings-row"));
                selectTriggerItem(option.getMode(), new Qs().parent(serviceQs.get()).shadow().selectors("ha-settings-row").nth(2).selector("ha-selector").shadow().selector("ha-selector-select").shadow().selector("ha-select"));
            }

            clickOn(new Qs().parent(actionQs.get()).selectors("ha-automation-action").nth(0).shadow().selector("ha-button"));
            setValue("Search action", "Notifications: Send a notification via mobile_app_a063", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("search-input").nth(0).shadow().selectors("ha-textfield"));
            selectItem("Notifications: Send a notification via mobile_app_a063", new Qs().selector("home-assistant").shadow().selector("add-automation-element-dialog").shadow().selectors("ha-list-item"));
            serviceQs = new Qs().parent(actionQs.get()).selectors("ha-automation-action").nth(0).shadow().selectors("ha-automation-action-row").nth(1).shadow().selector("ha-automation-action-service").shadow().selector("ha-service-control");
            setValue("", option.getMessage(), new Qs().parent(serviceQs.get()).shadow().selector("ha-selector").shadow().selector("ha-selector-text").shadow().selectors("ha-textfield"));
        }
    }

    public void saveAutomation(String name) {
        clickOn(new Qs().parent(mainQs.get()).selector("ha-automation-editor").shadow().selector("ha-fab"));
        setValue("Name", name, new Qs().selector("home-assistant").shadow().selector("ha-dialog-automation-rename").shadow().selector("ha-textfield"));
        clickOn(new Qs().selector("home-assistant").shadow().selector("ha-dialog-automation-rename").shadow().selectors("mwc-button").nth(1));
        Utils.sleep(1);
    }

    record Time(String label, String value) {
    }

    private void setValue(String label, String value, Qs qs) {
        try {
            List<WebElement> inputs = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
            for (WebElement input : inputs) {
                String text = input.getText();
                if (text.contains(label)) {
                    input.clear();
                    input.sendKeys(value);
                    break;
                }
            }
        } catch (ClassCastException e) {
            WebElement input = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
            input.sendKeys(value);
        }
    }

    private void selectTriggerItem(String itemName, Qs qs) {
        WebElement trigger = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        trigger.click();
        Utils.sleep(100);
        List<WebElement> items = trigger.findElements(By.cssSelector("mwc-list-item"));
        for (WebElement item : items) {
            WebLocatorUtils.doExecuteScript("arguments[0].scrollIntoView(true);", item);
            String text = item.getText();
            if (text.equals(itemName)) {
                item.click();
                break;
            }
        }
    }

    private void selectComboBoxItem(String itemName) {
        WebLocator list = new WebLocator().setTag("vaadin-combo-box-overlay").setId("overlay");
        WebLocator textEl = new WebLocator().setText(itemName);
        WebLocator item = new WebLocator(list).setTag("vaadin-combo-box-item").setChildNodes(textEl);
        item.click();
    }

    private WebElement clickOn(Qs qs) {
        WebElement button = (WebElement) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        if (button == null) {
            Utils.sleep(1);
        }
        WebLocatorUtils.doExecuteScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        return button;
    }

    private void selectItem(String name, Qs qs) {
        List<WebElement> items = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        for (WebElement item : items) {
            String nameItem = item.getText();
            if (nameItem.contains(name)) {
                item.click();
                break;
            }
        }
    }

    private void checkItem(String name, Qs qs) {
        List<WebElement> items = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(10), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        for (WebElement item : items) {
            WebLocatorUtils.doExecuteScript("arguments[0].scrollIntoView(true);", item);
            String nameItem = item.getText();
            if (nameItem.contains(name)) {
                WebElement input = item.findElement(By.cssSelector("ha-checkbox"));
                input.click();
                break;
            }
        }
    }
}
