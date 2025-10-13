package ro.btgo;

import com.google.common.base.Strings;
import com.sdl.selenium.Go;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.Operator;
import com.sdl.selenium.web.SearchText;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.CheckBox;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.table.Table;
import com.sdl.selenium.web.utils.Result;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fasttrackit.util.FileUtility;
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
    private final WebLink goHome = new WebLink().setId("homeScreenBtn");
    private final WebLocator goBack = new WebLocator().setId("historyBackBtn");
    private final TextField descriptionInput = new TextField().setId("descriptionInput");
    private final Button nextButton = new Button().setId("moveForwardBtn");
    private final WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setId("newPaymentBtn");

    public void login(String id, String password) {
        TextField idEl = new TextField().setId("user");
        TextField passwordEl = new TextField().setId("password");
        InputButton logIn = new InputButton().setText("Mergi mai departe");
        Result<Boolean> result = RetryUtils.retryUntilOneIs(Duration.ofSeconds(40),
                idEl::isPresent, //1
                transfer::isPresent //2
        );
        if (result.position() == 1) {
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
    }

    public void transferBetweenConts(int value, String fromCont, String toCont) {
        WebLocator accountDetails = new WebLocator().setTag("fba-account-details");
        accountDetails.ready(Duration.ofSeconds(10));
        List<String> list = accountDetails.getText().lines().toList();
        String sumaInCont = list.get(2).replaceAll(",", "");
        float tmpValue = Float.parseFloat(sumaInCont);
        int actualValue = (int) tmpValue;
        if (value == 0 || actualValue < value) {
            WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setId("transferInternalBtn");
            transfer.click();
            WebLocator container = new WebLocator().setTag("fba-transfer-accounts-container");
            container.scrollIntoView(Go.NEAREST);
            Card cardFromCont = new Card(container, fromCont);
            if (!cardFromCont.isSource()) {
                Button change = new Button(container).setClasses("exchange-icon");
                change.click();
            }
            Card cardToCont = new Card(container, toCont);
            String moveValue = String.valueOf(value == 0 ? actualValue : value - actualValue);
            cardToCont.setValue(moveValue);
            descriptionInput.scrollIntoView(Go.START);
            scrollAndDoClickOn(nextButton);
            cardToCont.scrollIntoView(Go.START);
            scrollAndDoClickOn(nextButton);
            goHomeAndBack();
        }
    }

    @SneakyThrows
    public boolean invoicePayment(Invoice invoice, String dovada) {
        boolean utilitati = invoice.getCategory().equals("Curent")
//                        || invoice.getCategory().equals("Apa")
//                || invoice.getCategory().equals("Gunoi")
                || invoice.getCategory().equals("Gaz");
        if (utilitati) {
            WebLocator textEl = new WebLocator().setText(" Plată nouă ");
            WebLocator transfer = new WebLocator().setTag("fba-dashboard-navigation-button").setChildNodes(textEl);
            transfer.click();
            WebLocator platesteUtilitati = new WebLocator().setText(" Plătește utilități ");
            platesteUtilitati.click();
            TextField search = new TextField().setId("searchInput");
            search.scrollIntoView(Go.NEAREST);
            search.setValue(invoice.getFurnizor());
            WebLocator list = new WebLocator().setId("providersList");
            WebLocator row = new WebLocator(list).setClasses("row").setText(invoice.getFurnizor(), SearchType.DEEP_CHILD_NODE_OR_SELF);
            row.click();
            scrollAndDoClickOn(nextButton);
            TextField sumaEl = new TextField().setId("transferAmountInput");
            sumaEl.setValue(invoice.getValue());
            TextField codAbonatEl = new TextField().setId("paymentRef1Input");
            codAbonatEl.setValue(invoice.getCod());
            TextField facturaEl = new TextField().setId("paymentRef2Input");
            facturaEl.scrollIntoView(Go.NEAREST);
            facturaEl.setValue(invoice.getNr());
            scrollAndDoClickOn(nextButton);
            Utils.sleep(1000);
            WebLocator nrFacturaEl = new WebLocator().setText(" Numar factura");
            nrFacturaEl.scrollIntoView(Go.NEAREST);
            scrollAndDoClickOn(nextButton);
        } else {
            transfer.click();
            WebLocator transferBani = new WebLocator().setId("selection0Btn");
            transferBani.click();
            WebLocator beneficiar = new WebLocator().setId("partnerSwitch");
            if (Strings.isNullOrEmpty(invoice.getIban())) {
                WebLink alegeBeneficiarul = new WebLink(beneficiar).setClasses("switch-item-0");
                alegeBeneficiarul.click();
                TextField search = new TextField().setId("searchInput");
                search.setValue(invoice.getFurnizor());
                WebLocator nameEl = new WebLocator().setTag("span").setText(invoice.getFurnizor());
                WebLocator card = new WebLocator().setClasses("card", "flex-row").setChildNodes(nameEl);
                scrollAndDoClickOn(card);
            } else {
                WebLink destinatarNou = new WebLink(beneficiar).setClasses("switch-item-1");
                destinatarNou.click();
                Utils.sleep(500);
                TextField nume = new TextField().setId("partnerNameInput");
                nume.setValue(invoice.getFurnizor());
                TextField iban = new TextField().setId("ibanInput");
                iban.setValue(invoice.getIban());
                iban.sendKeys(Keys.ENTER);
                iban.scrollIntoView(Go.START);
            }
            scrollAndDoClickOn(nextButton);
            TextField sumaEL = new TextField().setId("transferAmountInput");
            sumaEL.setValue(invoice.getValue());
            Utils.sleep(1000);
            descriptionInput.scrollIntoView(Go.CENTER);
            descriptionInput.setValue(Strings.isNullOrEmpty(invoice.getNr()) ? invoice.getDescription() : "factura " + invoice.getNr());
            Utils.sleep(500);
            scrollAndDoClickOn(nextButton);
            WebLocator ibanEl = new WebLocator().setTag("span").setText(invoice.getIban(), SearchType.TRIM);
            ibanEl.scrollIntoView(Go.START);
            Utils.sleep(500);
            scrollAndDoClickOn(nextButton);
        }
        Utils.sleep(2000); // wait for accept from BTGo
        Button download = new Button().setId("successPageActionBtn");
        goHome.scrollIntoView(Go.CENTER);
        scrollAndDoClickOn(download);
        Utils.sleep(1000);
        Files.walk(Paths.get(WebDriverConfig.getDownloadPath())).filter(Files::isRegularFile).forEach(file -> {
            String fileName = file.toString();
            if (fileName.contains("pdf")) {
                Storage.set("filePath", fileName);
            }
        });
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String extra = getExtra(invoice, month);
        String fileName = "DovadaPlata" + extra + ".pdf";
        log.info("File name: {}", fileName);
        Storage.set("fileName", fileName);
        String pdfPath = Storage.get("filePath");
        boolean success;
        if (!Strings.isNullOrEmpty(pdfPath)) {
            File pdfFile = new File(pdfPath);
            success = pdfFile.exists();
            if (success) {
                pdfFile.renameTo(new File(dovada + fileName));
                goHomeAndBack();
            } else {
                Utils.sleep(1);
            }
        } else {
            success = true;
        }
        return success;
    }

    private String getExtra(Invoice invoice, String month) {
        String extra = "";
        if (Strings.isNullOrEmpty(invoice.getFileName())) {
            if (invoice.getCategory().equals("Sustinere Educatie") && !Strings.isNullOrEmpty(invoice.getFurnizor())) {
                extra = invoice.getCategory().replaceAll(" ", "") + month;
                extra = extra + invoice.getFurnizor().replaceAll(" ", "") + invoice.getData();
            } else if (!Strings.isNullOrEmpty(invoice.getDecont())) {
                extra = extra + invoice.getDecont().split("\\.")[0];
            } else {
                extra = extra + invoice.getFurnizor().replaceAll(" ", "") + invoice.getData();
            }
        } else {
            if (Strings.isNullOrEmpty(invoice.getNr())) {
                extra = invoice.getCategory().replaceAll(" ", "");
                extra = extra + invoice.getData().getMonth().getDisplayName(TextStyle.FULL, roLocale);
            } else {
                extra = "Factura" + invoice.getNr();
            }
        }
//        return Strings.isNullOrEmpty(invoice.getFileName()) ? invoice.getCategory().replaceAll(" ", "") + month : (Strings.isNullOrEmpty(invoice.getNr()) ? "" : "Factura" + invoice.getNr());
        return extra;
    }

    private void scrollAndDoClickOn(WebLocator button) {
        Utils.sleep(300);
        boolean ready = button.ready(Duration.ofSeconds(10));
        log.info("scrollAndDoClickOnReady: {}", ready);
        button.scrollIntoView(Go.NEAREST);
        RetryUtils.retry(15, () -> {
            boolean doClick = button.doClick();
            if (!doClick) {
                Utils.sleep(200);
                button.scrollIntoView(Go.CENTER);
            }
            return doClick;
        });
        Utils.sleep(1000);
    }

    public String saveReport(String cont, String firstDayOfMonth, String lastDayOfMonth, String location) {
        WebLocator detailsContainer = new WebLocator().setTag("fba-accounts-root").setClasses("ng-star-inserted");
        boolean ready = detailsContainer.ready(Duration.ofSeconds(10));
        WebLocator title = new WebLocator().setTag("p").setText(cont);
        WebLocator card = new WebLocator(detailsContainer).setClasses("card").setChildNodes(title);
        if (!card.isPresent()) {
            WebLocator selectAccount = new WebLocator().setId("selectAccountBtn");
            selectAccount.click();
            WebLocator modal = new WebLocator().setClasses("d-block", "modal", "fade", "show");
            Card contEl = new Card(modal, cont);
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
        Button filtering = new Button(filterWindow, "Filtrează", SearchType.TRIM);
        filtering.click();
        Button export = new Button().setId("exportBtn");
        export.click();
        File file = FileUtility.getFileFromDownload();
        String fileName = file.getName();
        file.renameTo(new File(location + fileName));
        return fileName;
    }

    public String generateExtrasFromAll(String month, String location) {
        WebLocator extrase = new WebLocator().setAttribute("texttrans", "dashboard.header.operations.statements");
        extrase.click();
        Button calendar = new Button().setClasses("mdc-icon-button");
        WebLocator popUpCalendar = new WebLocator().setTag("mat-datepicker-content");
        Table table = new Table(popUpCalendar);
        Button generateExtras = new Button().setId("generateStatementsBtn");
        calendar.click();
        table.getCell(month, SearchType.DEEP_CHILD_NODE_OR_SELF, SearchType.TRIM).click();
        generateExtras.click();
        Utils.sleep(1000);
        WebLocator download = new WebLocator().setAttribute("data-mat-icon-name", "downloadBulk");
        download.ready(Duration.ofSeconds(50));
        download.click();

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
        Utils.sleep(1);
        return fileName;
    }

    public void createDepozit(String value) {
        WebLocator depoziteEl = new WebLocator().setId("savingsBtn");
        depoziteEl.click();
        Button openDepozit = new Button().setId("moveToOpenDepositPageBtn");
        openDepozit.click();
        WebLocator clasicDecpozit = new WebLocator().setId("classicDepositBtn");
        clasicDecpozit.click();
        WebLocator depozitClasicCointainer = new WebLocator().setTag("fba-deposit-saving-accounts-root");
        Card fromCont = new Card(depozitClasicCointainer);
        fromCont.setValue(value);
        WebLocator periodEl = new WebLocator().setId("slider-item-2");
        periodEl.scrollIntoView(Go.CENTER);
        periodEl.click();
        WebLocator autoRollover = new WebLocator().setId("autoRolloverSwitch");
        CheckBox automat = new CheckBox(autoRollover);
        automat.check(true);
        WebLocator termsAndConditionsEl = new WebLocator().setId("termsAndConditionsCheckbox");
        CheckBox termsAndConditions = new CheckBox(termsAndConditionsEl);
        termsAndConditions.check(true);
        Button nextButton = new Button(null, "Mergi mai departe", SearchType.TRIM).setId("moveForwardBtn");
        scrollAndDoClickOn(nextButton);
        Utils.sleep(1000);
        Button semneazaButton = new Button(null, "Semnează", SearchType.TRIM).setId("moveForwardBtn");
        scrollAndDoClickOn(semneazaButton);
        Utils.sleep(1); // wait for accept from BTGo
        goHomeAndBack();
    }

    private void goHomeAndBack() {
        Utils.sleep(1000);
        goHome.scrollIntoView(Go.NEAREST);
        goHome.ready(Duration.ofSeconds(10));
        goHome.doClick();
        Utils.sleep(1000);
        RetryUtils.retry(2, goBack::doClick);
    }
}
