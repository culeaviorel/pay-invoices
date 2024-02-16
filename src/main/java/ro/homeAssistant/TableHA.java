package ro.homeAssistant;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.utils.RetryUtils;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class TableHA {
    private final Qs qs;

    public TableHA(Qs qs) {
        this.qs = qs;
    }

    public void click() {
        WebElement button = find();
        button.click();
    }

    public WebElement find() {
        Qs qs1 = new Qs(qs).shadow().selector("ha-data-table");
        WebElement table = (WebElement) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(qs1.toSting()));
        return table;
    }

    public List<WebElement> getRows() {
        Qs tableQs = new Qs(qs).shadow().selector("ha-data-table");
        Qs rowQs = new Qs(tableQs).shadow().selectors(".mdc-data-table__row");
        List<WebElement> rows = (List<WebElement>) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(rowQs.toSting()));
        return rows;
    }

    public WebElement getCell(int indexRow, String cls) {
        Qs tableQs = new Qs(qs).shadow().selector("ha-data-table");
        Qs rowQs = new Qs(tableQs).shadow().selectors(".mdc-data-table__row").nth(indexRow).selector(cls);
        WebElement cell = (WebElement) RetryUtils.retry(Duration.ofSeconds(5), () -> WebLocatorUtils.doExecuteScript(rowQs.toSting()));
        return cell;
    }
}
