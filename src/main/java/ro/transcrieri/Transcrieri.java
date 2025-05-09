package ro.transcrieri;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.CheckBox;
import com.sdl.selenium.web.form.ComboBox;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class Transcrieri {
    private final WebLocator programare = new WebLocator().setText("Programare On-line").setClasses("prima_meniu");
    private final WebLocator containerOre = new WebLocator().setClasses("ore").setAttribute("style", "display: block;", SearchType.CONTAINS);
    private final WebLocator informare = new WebLocator().setClasses("ui-dialog").setVisibility(true);
    private final Button close = new Button(informare).setClasses("ui-dialog-titlebar-close");
    private final Button continueButton = new Button(informare).setId("btn_continue3");
    private final WebLocator dayEl = new WebLocator().setClasses("af", "af_da", "ok");
    private final WebLocator oreEl = new WebLocator(containerOre).setClasses("min", "mliber").setExcludeClasses("mocupat");
    private final WebLocator dateCompletate = new WebLocator().setClasses("date_sc");
    private final WebLocator radio = new WebLocator(dateCompletate).setClasses("tip_act");
    private final WebLocator label3 = new WebLocator(radio).setTag("label").setAttribute("for", "act3"); // Ambele
    private final TextField nameSiPrenume = new TextField().setId("nume00");
    private final TextField nrCertificatCetatenie = new TextField().setId("nr_cet0");
    private final TextField dataCertificat = new TextField().setId("data_cet0");
    private final TextField taraEl = new TextField().setId("tara_cet0");
    private final TextField emailEl = new TextField().setId("email0");
    private final InputButton incarcaFisierulEl = new InputButton().setId("but_up_fis0");
    private final WebLocator dragAndDrop = new WebLocator().setId("cos_upload");
    private final WebLocator upload = new WebLocator().setId("aux_file0");
    private final CheckBox acord1 = new CheckBox().setId("acord0");
    private final CheckBox acord2 = new CheckBox().setId("termen0");
    private final InputButton continua = new InputButton().setId("inreg");
    private final WebLocator acordLicenta = new WebLocator().setClasses("acord_licenta");
    private final WebLocator eula = new WebLocator().setClasses("container_eula");
    private final WebLocator contract = new WebLocator(acordLicenta).setText("Pentru orice întrebări sau probleme, utilizatorii pot contacta S.P.C.L.E.P. ORADEA, Compartimentul Stare Civilă.").setTag("p");

//    public static void clickElement(WebElement element) {
//        JavascriptExecutor jse = (JavascriptExecutor) WebDriverConfig.getDriver();
//        jse.executeScript("arguments[0].click();", element);
//    }

    public void make(List<Item> items) {
        programare.click();
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

            oreEl.doClick();
            label3.click(); // Tipul actului de transcris

            typeEachChar(item.name().toUpperCase(), nameSiPrenume);
            typeEachChar(item.nr(), nrCertificatCetatenie);
            selectDate(item);
            taraEl.setValue(item.tara());
            typeEachChar(item.email(), emailEl);

            acord1.check(true);
            acord2.check(true);

            incarcaFisierulEl.click();
            dragAndDrop.click();
            String pathFile = Paths.get("C:\\Users\\vculea\\Desktop\\Transcrieri", item.file()).toString();
            upload.sendKeys(pathFile);

            continua.click();
            Utils.sleep(1);
//        for captcha ->    https://stackoverflow.com/questions/18935696/how-to-read-the-text-from-image-captcha-by-using-selenium-webdriver-with-java
        }
    }

    private void selectDate(Item item) {
        dataCertificat.click();
        LocalDate now = LocalDate.parse(item.data(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String year = now.getYear() + "";
        ComboBox yearEl = new ComboBox().setClasses("ui-datepicker-year");
        yearEl.selectByValue(year);
//        yearEl.selectByIndex(4);
        ComboBox monthEl = new ComboBox().setClasses("ui-datepicker-month");
        int month = now.getMonthValue() - 1;
        monthEl.selectByIndex(month);
        String day = now.getDayOfMonth() + "";
        WebLocator dayCalendarEl = new WebLocator().setTag("td").setAttribute("data-handler", "selectDay");
        WebLink dayEl = new WebLink(dayCalendarEl).setText(day).setClasses("ui-state-default");
        RetryUtils.retry(2, dayEl::doClick);
    }

    private static void typeEachChar(String value, TextField textField) {
        char[] chars = value.toCharArray();
        for (char aChar : chars) {
            textField.doSendKeys(aChar + "");
        }
    }
}
