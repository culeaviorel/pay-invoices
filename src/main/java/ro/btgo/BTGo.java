package ro.btgo;

import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.Operator;
import com.sdl.selenium.web.SearchText;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Slf4j
public class BTGo {
    private final Locale roLocale = new Locale("ro", "RO");
    //    private final Button nextButton = new Button().setId("moveForwardBtn");
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
    }

    public void transferFromDepozitIntoContCurent(int intValue, String contCurent, String contDeEconomii) {
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
            WebLocator container = new WebLocator().setTag("fba-transfer-accounts-container");
            WebLocator sourceAccount = new WebLocator().setId("sourceAccount");
            WebLocatorUtils.scrollToWebLocator(sourceAccount);
            Card cardEconomii = new Card(container, contDeEconomii);
            if (!cardEconomii.isSource()) {
                Button schimba = new Button(container).setClasses("exchange-icon");
                schimba.click();
            }
            Card cardContCurent = new Card(container, contCurent);
//            WebLocator openAccounts = new WebLocator(sourceAccount).setClasses("accounts-drd");
//            openAccounts.click();
//            WebLocator contEconomii = new WebLocator().setText(" Cont de economii ");
//            WebLocator contEconomiiEl = new WebLocator().setTag("fba-account-details").setChildNodes(contEconomii);
//            contEconomiiEl.click();
//            WebLocator targetAccount = new WebLocator().setId("targetAccount");
//            WebLocatorUtils.scrollToWebLocator(targetAccount);
//            WebLocator openTargetAccounts = new WebLocator(targetAccount).setClasses("accounts-drd");
//            RetryUtils.retry(2, openTargetAccounts::click);
//            WebLocator contCurentText = new WebLocator().setText(" Cont curent ");
//            WebLocator contCurentEl = new WebLocator().setTag("fba-account-details").setChildNodes(contCurentText);
//            contCurentEl.click();
//            TextField sumaEl = new TextField().setId("destinationAccountValueInput");
            cardContCurent.setValue(String.valueOf(intValue - sumaActuala + 5));
            WebLocator descriptionInput = new WebLocator().setId("descriptionInput");
            WebLocatorUtils.scrollToWebLocator(descriptionInput);
            Button nextButton = new Button(null, "Mergi mai departe", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(nextButton);
            Utils.sleep(1000);
            WebLocator description = new WebLocator().setText(" Descrierea tranzacției ");
            WebLocatorUtils.scrollToWebLocator(description);
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
        boolean utilitati = invoice.getCategory().equals("Apa")
//                || invoice.getCategory().equals("Gunoi")
                || invoice.getCategory().equals("Curent")
                || invoice.getCategory().equals("Gaz");
        if (utilitati) {
            WebLocator textEl = new WebLocator().setText(" Plată nouă ");
            WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setChildNodes(textEl);
            transfer.click();
            WebLocator platesteUtilitati = new WebLocator().setText(" Plătește utilități ");
            platesteUtilitati.click();
            TextField search = new TextField().setId("searchInput");
            WebLocatorUtils.scrollToWebLocator(search);
            search.setValue(invoice.getFurnizor());
            WebLocator list = new WebLocator().setId("providersList");
            WebLocator row = new WebLocator(list).setClasses("row").setText(invoice.getFurnizor(), SearchType.DEEP_CHILD_NODE_OR_SELF);
            row.click();
            Button maiDeparteButton = new Button(null, "Mergi mai departe", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(maiDeparteButton);
            TextField sumaEl = new TextField().setId("transferAmountInput");
            sumaEl.setValue(invoice.getValue());
            TextField codAbonatEl = new TextField().setId("paymentRef1Input");
            codAbonatEl.setValue(invoice.getCod());
            TextField facturaEl = new TextField().setId("paymentRef2Input");
            WebLocatorUtils.scrollToWebLocator(facturaEl);
            facturaEl.setValue(invoice.getNr());
            scrollAndDoClickOn(maiDeparteButton);
            Utils.sleep(1000);
            WebLocator nrFacturaEl = new WebLocator().setText(" Numar factura");
            WebLocatorUtils.scrollToWebLocator(nrFacturaEl);
            Button semneazaButton = new Button(null, "Semnează", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(semneazaButton);
        } else {
            WebLocator textEl = new WebLocator().setText(" Plată nouă ");
            WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setChildNodes(textEl);
            transfer.click();
            WebLocator transferBani = new WebLocator().setText(" Transferă bani ");
            transferBani.click();
            if (Strings.isNullOrEmpty(invoice.getIban())) {
                WebLink alegeBeneficiarul = new WebLink(null, "Alege beneficiarul", SearchType.TRIM);
                alegeBeneficiarul.click();
                TextField search = new TextField().setId("searchInput");
                search.setValue(invoice.getFurnizor());
                WebLocator nameEl = new WebLocator().setTag("span").setText(invoice.getFurnizor());
                WebLocator card = new WebLocator().setClasses("card", "flex-row").setChildNodes(nameEl);
                scrollAndDoClickOn(card);
            } else {
                WebLink destinatarNou = new WebLink(null, "Beneficiar nou", SearchType.TRIM);
                destinatarNou.click();
                TextField nume = new TextField().setId("partnerNameInput");
                nume.setValue(invoice.getFurnizor());
                TextField iban = new TextField().setId("ibanInput");
                iban.setValue(invoice.getIban());
                iban.sendKeys(Keys.ENTER);
                Utils.sleep(500);
                WebLocatorUtils.scrollToWebLocator(iban);
            }
            Button maiDeparteButton = new Button(null, "Mergi mai departe", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(maiDeparteButton);
            TextField sumaEL = new TextField().setId("transferAmountInput");
            sumaEL.setValue(invoice.getValue());
            TextField descriptionInput = new TextField().setId("descriptionInput");
            descriptionInput.setValue(Strings.isNullOrEmpty(invoice.getNr()) ? invoice.getDescription() : "factura " + invoice.getNr());
            WebLocatorUtils.scrollToWebLocator(descriptionInput);
            scrollAndDoClickOn(maiDeparteButton);            Utils.sleep(500);
            WebLocator description = new WebLocator().setText(" Descrierea tranzacției ");
            WebLocatorUtils.scrollToWebLocator(description);
            Utils.sleep(500);
            Button laSemnareButton = new Button(null, "Mergi la semnare", SearchType.TRIM).setId("moveForwardBtn");
            scrollAndDoClickOn(laSemnareButton);
        }
        Utils.sleep(10000); // wait for accept from BTGo
        WebLocatorUtils.scrollToWebLocator(goHome);
        Button download = new Button().setId("successPageActionBtn");
        scrollAndDoClickOn(download);
        Utils.sleep(1000);
        Files.walk(Paths.get(WebDriverConfig.getDownloadPath()))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String fileName = file.toString();
                    if (fileName.contains("pdf")) {
                        Storage.set("filePath", fileName);
                    }
                });
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String extra = getExtra(invoice, month);
        String fileName = "DovadaPlata" + extra + ".pdf";
        Storage.set("fileName", fileName);
        String pdfPath = Storage.get("filePath");
        File pdfFile = new File(pdfPath);
        boolean success = pdfFile.exists();
        pdfFile.renameTo(new File(dovada + fileName));
        goHome.click();
        Utils.sleep(1000);
        goBack.click();
        return success;
    }

    private static String getExtra(Invoice invoice, String month) {
        String extra;
        if (Strings.isNullOrEmpty(invoice.getFileName())) {
            extra = invoice.getCategory().replaceAll(" ", "");
            if (invoice.getCategory().equals("Sustinere Educatie") && !Strings.isNullOrEmpty(invoice.getFurnizor())) {
                extra = extra + invoice.getFurnizor().replaceAll(" ", "");
            }
            extra = extra + month;
        } else {
            if (Strings.isNullOrEmpty(invoice.getNr())) {
                extra = "";
            } else {
                extra = "Factura" + invoice.getNr();
            }
        }
//        return Strings.isNullOrEmpty(invoice.getFileName()) ? invoice.getCategory().replaceAll(" ", "") + month : (Strings.isNullOrEmpty(invoice.getNr()) ? "" : "Factura" + invoice.getNr());
        return extra;
    }

    private void scrollAndDoClickOn(WebLocator button) {
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

    public String saveReport(String contCurent, String firstDayOfMonth, String lastDayOfMonth, String location) {
        WebLocator detailsContainer = new WebLocator().setTag("fba-accounts-root").setClasses("ng-star-inserted");
        boolean ready = detailsContainer.ready(Duration.ofSeconds(10));
        WebLocator title = new WebLocator().setTag("p").setText(contCurent);
        WebLocator card = new WebLocator(detailsContainer).setClasses("card").setChildNodes(title);
        if (!card.isPresent()) {
            WebLocator selectAccount = new WebLocator().setId("selectAccountBtn");
            selectAccount.click();
            WebLocator modal = new WebLocator().setClasses("d-block", "modal", "fade", "show");
            Card contEl = new Card(modal, contCurent);
            contEl.click();
        }
        Button openFilter = new Button().setId("openOffcanvasFiltersBtn");
        scrollAndDoClickOn(openFilter);
        WebLocator filterWindow = new WebLocator().setClasses("offcanvas", "offcanvas-end", "show");
        boolean ready1 = filterWindow.ready(Duration.ofSeconds(10));
        Utils.sleep(1000);
        WebLocator period = new WebLocator(filterWindow).setId("periodOTHERRadioBtn");
        period.click();
        List<SearchText> searchTexts = List.of(new SearchText("mat-datepicker-0"), new SearchText("mat-datepicker-2"));
        TextField startDate = new TextField().setAttributes("data-mat-calendar", Operator.OR, searchTexts.toArray(new SearchText[0]));
        startDate.setValue(firstDayOfMonth);
        List<SearchText> searchTexts1 = List.of(new SearchText("mat-datepicker-1"), new SearchText("mat-datepicker-3"));
        TextField endDate = new TextField().setAttributes("data-mat-calendar", Operator.OR, searchTexts1.toArray(new SearchText[0]));
        endDate.setValue(lastDayOfMonth);
        Button viewTransactions = new Button(filterWindow, "Vezi tranzacții", SearchType.TRIM);
        viewTransactions.click();
        Button export = new Button().setId("exportBtn");
        export.click();
        List<Path> list = RetryUtils.retry(Duration.ofSeconds(25), () -> {
            List<Path> paths = Files.list(Paths.get(WebDriverConfig.getDownloadPath())).toList();
            if (!paths.isEmpty()) {
                return paths;
            } else {
                return null;
            }
        });
        Optional<Path> first = list.stream().filter(i -> !Files.isDirectory(i)).findFirst();
        String fileName = "";
        if (first.isPresent()) {
            Path path = first.get();
            File file = path.toFile();
            fileName = file.getName();
            file.renameTo(new File(location + fileName));
        }
        return fileName;
    }
}
