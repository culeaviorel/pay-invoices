package ro.btgo;

import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
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
    private final WebLink goHome = new WebLink().setId("homeScreenBtn");
    private final WebLocator goBack = new WebLocator().setId("historyBackBtn");

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
            WebLocatorUtils.scrollToWebLocator(sourceAccount);
            WebLocator openAccounts = new WebLocator(sourceAccount).setClasses("accounts-drd");
            openAccounts.click();
            WebLocator contEconomii = new WebLocator().setText(" Cont de economii ");
            WebLocator contEconomiiEl = new WebLocator().setTag("fba-account-details").setChildNodes(contEconomii);
            contEconomiiEl.click();
            WebLocator targetAccount = new WebLocator().setId("targetAccount");
            WebLocatorUtils.scrollToWebLocator(targetAccount);
            WebLocator openTargetAccounts = new WebLocator(targetAccount).setClasses("accounts-drd");
            RetryUtils.retry(2, openTargetAccounts::click);
            WebLocator contCurent = new WebLocator().setText(" Cont curent ");
            WebLocator contCurentEl = new WebLocator().setTag("fba-account-details").setChildNodes(contCurent);
            contCurentEl.click();
            TextField sumaEl = new TextField().setId("destinationAccountValueInput");
            sumaEl.setValue(String.valueOf(intValue - sumaActuala + 5));
            Button nextButton = new Button(null, "Mergi mai departe", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(nextButton);
            Utils.sleep(1000);
            Button transferaButton = new Button(null, "Transferă", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(transferaButton);
            goHome.ready(Duration.ofSeconds(10));
            goHome.click();
            WebLocatorUtils.scrollToWebLocator(goBack);
            goBack.click();
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
        WebLocatorUtils.scrollToWebLocator(iban);
        Button maiDeparteButton = new Button(null, "Mergi mai departe", SearchType.TRIM).setId("moveForwardBtn");
        scrollAndDoClickOn(maiDeparteButton);
        TextField sumaEL = new TextField().setId("transferAmountInput");
        sumaEL.setValue(invoice.getValue());
        TextField descriptionInput = new TextField().setId("descriptionInput");
        descriptionInput.setValue("factura " + invoice.getNr());
        WebLocatorUtils.scrollToWebLocator(descriptionInput);
        scrollAndDoClickOn(maiDeparteButton);
        Utils.sleep(500);
        Button laSemnareButton = new Button(null, "Mergi la semnare", SearchType.TRIM).setId("moveForwardBtn");
        scrollAndDoClickOn(laSemnareButton);
        Utils.sleep(10000); // wait for accept from BTGo
        Button download = new Button().setId("successPageActionBtn");
        download.ready(Duration.ofSeconds(30));
        WebLocatorUtils.scrollToWebLocator(download);
        download.click();
        Files.walk(Paths.get(WebDriverConfig.getDownloadPath()))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String fileName = file.toString();
                    if (fileName.contains("pdf")) {
                        Storage.set("filePath", fileName);
                    }
                });
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String extra = Strings.isNullOrEmpty(invoice.getFileName()) ? invoice.getCategory().replaceAll(" ", "") + month : "Factura" + invoice.getNr();
        String fileName = "DovadaPlata" + extra + ".pdf";
        Storage.set("fileName", fileName);
        String pdfPath = Storage.get("filePath");
        File pdfFile = new File(pdfPath);
        boolean success = pdfFile.exists();
        pdfFile.renameTo(new File(dovada + fileName));
        goHome.click();
        goBack.click();
        return success;
    }

    private void scrollAndDoClickOn(Button button) {
        button.ready(Duration.ofSeconds(10));
        RetryUtils.retry(15, () -> {
            boolean doClick = button.doClick();
            if (!doClick) {
                Utils.sleep(100);
                WebLocatorUtils.scrollToWebLocator(button);
            }
            return doClick;
        });
    }
}
