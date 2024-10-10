package ro.btgo;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import ro.neo.Invoice;
import ro.neo.Storage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;


@Slf4j
public class BTGo {
    private final Locale roLocale = new Locale("ro", "RO");
    private final Button nextButton = new Button().setId("moveForwardBtn");

    public void login(String id, String password) {
        TextField idEl = new TextField().setId("user");
        TextField passwordEl = new TextField().setId("password");
        InputButton logIn = new InputButton().setText("Mergi mai departe");
        idEl.ready(Duration.ofSeconds(40));
        WebLink accept = new WebLink(null, " Am înțeles");
        accept.click();
        RetryUtils.retry(40, () -> idEl.setValue(id));
        WebLocatorUtils.scrollToWebLocator(passwordEl);
        passwordEl.setValue(password);
        RetryUtils.retry(5, () -> {
            boolean doClick = logIn.doClick();
            if (!doClick) {
                Utils.sleep(100);
                WebLocatorUtils.scrollToWebLocator(logIn);
            }
            return doClick;
        });
        Utils.sleep(1); //wait for accept from BTGo
    }


    public void transferFromDepozitIntoContCurent(int intValue) {
        WebLocator accountDetails = new WebLocator().setTag("fba-account-details");
        accountDetails.ready(Duration.ofSeconds(10));
        List<String> list = accountDetails.getText().lines().toList();
        String sumaInCont = list.get(2).replaceAll(",", "");
        float tmpValue = Float.parseFloat(sumaInCont);
        int sumaActuala = (int) tmpValue;
        if (sumaActuala < intValue) {
            WebLocator textEl = new WebLocator().setText(" Transfer intern ");
            WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setChildNodes(textEl);
            transfer.click();
            WebLocator sourceAccount = new WebLocator().setId("sourceAccount");
            WebLocator openAccounts = new WebLocator(sourceAccount).setClasses("accounts-drd");
            openAccounts.click();
            WebLocator contEconomii = new WebLocator().setText(" Cont de economii ");
            WebLocator contEconomiiEl = new WebLocator().setTag("fba-account-details").setChildNodes(contEconomii);
            contEconomiiEl.click();
            WebLocator targetAccount = new WebLocator().setId("targetAccount");
            WebLocator openTargetAccounts = new WebLocator(targetAccount).setClasses("accounts-drd");
            openTargetAccounts.click();
            WebLocator contCurent = new WebLocator().setText(" Cont curent ");
            WebLocator contCurentEl = new WebLocator().setTag("fba-account-details").setChildNodes(contCurent);
            contCurentEl.click();
            TextField sumaEl = new TextField().setId("destinationAccountValueInput");
            sumaEl.setValue(String.valueOf(intValue));

            nextButton.click();
            Utils.sleep(1);
        }
    }

    @SneakyThrows
    public boolean invoicePayment(Invoice invoice, String dovada) {
        WebLocator textEl = new WebLocator().setText(" Plată nouă ");
        WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setChildNodes(textEl);
        transfer.click();
        WebLocator transferBani = new WebLocator().setText(" Transferă bani ");
        transferBani.click();
        WebLink destinatarNou = new WebLink(null, " Beneficiar nou ");
        destinatarNou.click();
        TextField nume = new TextField().setId("partnerNameInput");
        nume.setValue(invoice.getFurnizor());
        TextField iban = new TextField().setId("ibanInput");
        iban.setValue(invoice.getIban());
        iban.sendKeys(Keys.ENTER);
        Utils.sleep(500);
        WebLocatorUtils.scrollToWebLocator(nextButton);
        nextButton.click();
        TextField sumaEL = new TextField().setId("transferAmountInput");
        sumaEL.setValue(invoice.getValue());
        TextField descriptionInput = new TextField().setId("descriptionInput");
        descriptionInput.setValue(invoice.getNr());
        WebLocatorUtils.scrollToWebLocator(nextButton);
        nextButton.click();
        Utils.sleep(500);
        nextButton.ready(Duration.ofSeconds(10));
        RetryUtils.retry(5, () -> {
            boolean doClick = nextButton.doClick();
            if (!doClick) {
                Utils.sleep(100);
                WebLocatorUtils.scrollToWebLocator(nextButton);
            }
            return doClick;
        });
        Utils.sleep(1); // wait for accept from BTGo
        Button download = new Button().setId("successPageActionBtn");
        download.ready(Duration.ofSeconds(30));
        download.click();
        Files.walk(Paths.get(WebDriverConfig.getDownloadPath()))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String fileName = file.toString();
                    if (fileName.contains("pdf")) {
                        Storage.set("fileName", fileName);
                    }
                });
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String fileName = "DovadaPlata" + invoice.getCategory().replaceAll(" ", "") + month + ".pdf";
        String pdfName = Storage.get("fileName");
        File pdfFile = new File(pdfName);
        boolean success = pdfFile.exists();
        pdfFile.renameTo(new File(dovada + fileName));
        WebLink goHome = new WebLink().setId("homeScreenBtn");
        goHome.click();
        WebLocator goBack = new WebLocator().setId("historyBackBtn");
        goBack.click();
        return success;
    }
}
