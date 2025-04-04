package ro.transcrieri;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.CheckBox;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
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
            WebLocator oreEl = new WebLocator(containerOre).setClasses("min", "liber").setExcludeClasses("mocupat");
            oreEl.click();

            WebLocator dateCompletate = new WebLocator().setClasses("date_sc");
            WebLocator radio = new WebLocator(dateCompletate).setClasses("tip_act");
            WebLocator input1 = new WebLocator(radio).setTag("input").setId("act1"); // Nastere
            WebLocator input2 = new WebLocator(radio).setTag("input").setId("act2"); // Casatorie
            WebLocator input3 = new WebLocator(radio).setTag("input").setId("act3"); // Ambele
            input3.click(); // Tipul actului de transcris
            TextField nameSiPrenume = new TextField().setId("nume00");
            nameSiPrenume.setValue((item.name()).toUpperCase());
//            TextField numeSiPrenumeMinor1 = new TextField().setId("nr_c01");
            TextField nrCertificatCetatenie = new TextField().setId("nr_cet0");
            nrCertificatCetatenie.setValue(item.nr());
            TextField dataCertificat = new TextField().setId("data_cet0");
            dataCertificat.setValue(item.data());
            TextField taraEl = new TextField().setId("tara_cet0");
            taraEl.setValue(item.tara());
            TextField emailEl = new TextField().setId("email0");
            emailEl.setValue(item.email());
            InputButton submit = new InputButton().setId("but_up_fis0");
            submit.click();
            WebLocator upload = new WebLocator().setId("aux_file0");
            Path path = Paths.get("C:\\Users\\vculea\\Desktop\\Transcrieri", item.file());
            String pathFile = path.toString();
            upload.sendKeys(pathFile);
            CheckBox acord1 = new CheckBox().setId("acord0");
            acord1.click();
            CheckBox acord2 = new CheckBox().setId("termen0");
            acord2.click();
            InputButton continua = new InputButton().setId("inreg");
            continua.click();
            Utils.sleep(1);
        }
    }
}
