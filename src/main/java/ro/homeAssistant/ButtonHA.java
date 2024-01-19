package ro.homeAssistant;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.utils.RetryUtils;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class ButtonHA {
    private String label;
    private final Qs qs;

    public ButtonHA(Qs qs) {
        this.qs = qs;
    }

    public ButtonHA(Qs qs, String text) {
        this(qs);
        this.label = text;
    }

    public void click() {
        WebElement button = find();
        button.click();
    }

    public WebElement find() {
        Qs qs1 = new Qs(qs).shadow().selectors("mwc-button");
        List<WebElement> buttons = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(qs1.toSting()));
        for (WebElement button : buttons) {
            String actualLabel = button.getText();
            if (label.equals(actualLabel)) {
                return button;
            }
        }
        return null;
    }
}
