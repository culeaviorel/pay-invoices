package ro.homeAssistant;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.RetryUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class ComboBoxHA {
    private String label;
    private final Qs qs;

    public ComboBoxHA(Qs qs) {
        this.qs = qs;
    }

    public ComboBoxHA(Qs qs, String label) {
        this(qs);
        this.label = label;
    }

    public void typeAndSelect(String value) {
        WebElement input = find();
        WebElement inputEl = input.getShadowRoot().findElement(By.cssSelector("input"));
        inputEl.sendKeys(value);
        doSelect(value);
    }

    public void select(String value) {
        WebElement input = find();
        input.click();
        doSelect(value);
    }

    private static void doSelect(String value) {
        WebLocator list = new WebLocator().setTag("vaadin-combo-box-overlay").setId("overlay");
        WebLocator textEl = new WebLocator().setText(value);
        WebLocator item = new WebLocator(list).setTag("vaadin-combo-box-item").setChildNodes(textEl);
        item.click();
    }

    public WebElement find() {
        List<WebElement> inputs = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        int size = inputs.size();
        for (int i = 0; i < size; i++) {
            Qs qs1 = new Qs(qs).nth(i).shadow().selector("ha-selector-entity").shadow().selector("ha-entity-picker").shadow().selector("ha-combo-box").shadow().selector("ha-textfield");
            WebElement input = (WebElement) RetryUtils.retry(Duration.ofSeconds(1), () -> WebLocatorUtils.doExecuteScript(qs1.toSting()));
            if (input == null) {
                continue;
            }
            String actualLabel = input.getText();
            if (label.equals(actualLabel)) {
                return input;
            }
        }
        return null;
    }
}
