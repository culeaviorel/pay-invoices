package ro.homeAssistant;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.utils.RetryUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class TextFieldHA {
    private String label;
    private final Qs qs;

    public TextFieldHA(Qs qs) {
        this.qs = qs;
    }

    public TextFieldHA(Qs qs, String label) {
        this(qs);
        this.label = label;
    }

    public void setValue(String value) {
        WebElement input = find();
        WebElement inputEl = input.getShadowRoot().findElement(By.cssSelector("input"));
        if (!inputEl.getAttribute("value").equals(value)) {
            inputEl.sendKeys(value);
        }
    }

    public WebElement find() {
        List<WebElement> inputs = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(qs.toSting()));
        int size = inputs.size();
        for (int i = 0; i < size; i++) {
            Qs child = new Qs().shadow().selector("ha-selector-text, ha-selector-number").shadow().selector("ha-textfield");
            Qs qs1 = new Qs(qs).nth(i).child(child);
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
