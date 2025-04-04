package ro.transcrieri;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Transcrieri {

    public void make(List<Item> items) {
        WebLocator programare = new WebLocator().setText("Programare On-line").setClasses("prima_meniu");
        programare.click();
        WebLocator informare = new WebLocator().setClasses("ui-dialog").setVisibility(true);
        Button close = new Button(informare).setClasses("ui-dialog-titlebar-close");
        WebLocator dayEl = new WebLocator().setClasses("af", "af_da", "ok");
        WebLocator containerOre = new WebLocator().setClasses("ore").setAttribute("style", "display: block;", SearchType.CONTAINS);
        RetryUtils.retry(15, () -> {
            boolean isVisible = informare.isPresent();
            if (isVisible) {
                close.click();
            }
            return isVisible;
        });
        for (Item item : items) {
            RetryUtils.retry(5, () -> {
                dayEl.click();
                boolean success;
                boolean isVisible = informare.isPresent();
                if (isVisible) {
                    close.click();
                    success = false;
                    Utils.sleep(1000);
                } else {
                    success = containerOre.isPresent();
                }
                return success;
            });
        WebLocator oreEl = new WebLocator(containerOre).setClasses("min").setExcludeClasses("mocupat");
        oreEl.click();
            Utils.sleep(1);
        }
    }
}
