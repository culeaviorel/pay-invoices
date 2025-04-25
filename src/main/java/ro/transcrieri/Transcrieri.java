package ro.transcrieri;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.CheckBox;
import com.sdl.selenium.web.form.ComboBox;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.ui.Select;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
    private final WebLocator upload = new WebLocator().setId("aux_file0");
    private final CheckBox acord1 = new CheckBox().setId("acord0");
    private final CheckBox acord2 = new CheckBox().setId("termen0");
    private final InputButton continua = new InputButton().setId("inreg");

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
            dataCertificat.click();
            selectDate(item);
            taraEl.setValue(item.tara());
            WebLocator taraEl2 = new WebLocator(taraEl).setClasses("ui-menu-item-wrapper").setText(item.tara());
            taraEl2.click();
            typeEachChar(item.email(), emailEl);
            incarcaFisierulEl.click();

            acord1.click();
            acord2.click();

            String pathFile = Paths.get("C:\\Users\\vculea\\Desktop\\Transcrieri", item.file()).toString();
            upload.sendKeys(pathFile);

            continua.click();
            Utils.sleep(1);
//        for captcha ->    https://stackoverflow.com/questions/18935696/how-to-read-the-text-from-image-captcha-by-using-selenium-webdriver-with-java
        }
    }

    private static void selectDate(Item item) {
        LocalDate now = LocalDate.parse(item.data(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String year = now.getYear() + "";
        ComboBox yearEl = new ComboBox().setClasses("ui-datepicker-year");
        Select selectYear = new Select(yearEl.getWebElement());
        RetryUtils.retry(2, () -> {
            selectYear.selectByValue(year);
            String selectedOption = selectYear.getFirstSelectedOption().getText();
            log.info("Selected year: {}", selectedOption);
            return selectedOption.equals(year);
        });

        String substring = now.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ro", "RO")).substring(0, 3);
        String month = StringUtils.capitalize(substring).replace("Noi", "Noe");
        ComboBox monthEl = new ComboBox().setClasses("ui-datepicker-month");
        Select selectMonth = new Select(monthEl.getWebElement());
        RetryUtils.retry(2, () -> {
            selectMonth.selectByValue(month);
            String selectedOption = selectMonth.getFirstSelectedOption().getText();
            log.info("Selected month: {}", selectedOption);
            return selectedOption.equals(month);
        });

        int day = now.getDayOfMonth();
        WebLocator dayCalendarEl = new WebLocator().setTag("td").setText(day + "").setAttribute("data-handler", "selectDay");
        RetryUtils.retry(2, dayCalendarEl::doClick);
    }

    private static void typeEachChar(String value, TextField textField) {
        char[] chars = value.toCharArray();
        for (char aChar : chars) {
            textField.doSendKeys(aChar + "");
        }
    }
}
