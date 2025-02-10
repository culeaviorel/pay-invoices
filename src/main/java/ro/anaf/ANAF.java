package ro.anaf;

import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.link.WebLink;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ANAF {
    private final Button autentificareEFactura = new Button().setId("cautare");

    public void login() {
        autentificareEFactura.click();
        WebLocator card2 = new WebLocator().setClasses("card2");
        WebLocator cardFront2 = new WebLocator(card2).setClasses("card-front2");
        cardFront2.mouseOver();
        WebLocator cardBack2 = new WebLocator(card2).setClasses("card-back2");
        WebLink raspunsuri = new WebLink(cardBack2, "Răspunsuri factură", SearchType.TRIM);
        raspunsuri.click();
        List<String> windows = WebDriverConfig.getDriver().getWindowHandles().stream().toList();
        WebDriverConfig.getDriver().switchTo().window(windows.get(1));
    }

    public void getAllInvoices(int days) {
        WebLocator daysEl = new WebLocator().setId("form2:zile");
        Select selectDays = new Select(daysEl.getWebElement());
        selectDays.selectByValue(days + "");

        WebLocator cuiEl = new WebLocator().setId("form2:cui");
        Select selectCui = new Select(cuiEl.getWebElement());
        selectCui.selectByValue("26392200");

        InputButton facturi = new InputButton(null, "Obţine Răspunsuri");
        facturi.click();
    }
}
