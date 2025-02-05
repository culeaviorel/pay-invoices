package ro.neo;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.TextArea;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.FileUtils;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.zh_cn.假如;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fasttrackit.util.XlsxUtility;
import org.openqa.selenium.Keys;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Slf4j
public class Neo {
    private final WebLink nextWebLink = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_btnNextFlowItem");
    private final WebLink dashboard = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_linkGoDashboard");
    private final Locale roLocale = new Locale("ro", "RO");

    public void login(String id, String password) {
        TextField idEl = new TextField().setId("MainContentFull_ebLoginControl_txtUserName_txField");
        TextField passwordEl = new TextField().setId("MainContentFull_ebLoginControl_txtCredential_txField");
        InputButton logIn = new InputButton().setText("MERG MAI DEPARTE");
        idEl.ready(Duration.ofSeconds(40));
        RetryUtils.retry(40, () -> idEl.setValue(id));
        passwordEl.setValue(password);
        logIn.click();
        acceptAll();
        Utils.sleep(1); //wait for SMS code
        logIn.click();
    }

    private void acceptAll() {
        Button acceptAllButton = new Button().setClasses("gdprIntro_acceptAll");
        acceptAllButton.doClick();
    }

    public void selectProfile(String profile) {
        WebLink profileEl = new WebLink(new WebLocator(), profile, SearchType.DEEP_CHILD_NODE_OR_SELF, SearchType.TRIM);
        profileEl.ready(Duration.ofSeconds(10));
        profileEl.click();
        Utils.sleep(1000);
    }

    public void transferFromDepozitIntoContCurent(int sum) {
        WebLocator balance = new WebLocator().setId("MainContent_TransactionMainContent_BTLandingProductsControl_rptProductCurrentAccount_balance_0");
        String balanceValue = RetryUtils.retry(20, balance::getText);
        balanceValue = balanceValue.replaceAll("RON", "").replaceAll(",", "").trim();
        float tmpValue = Float.parseFloat(balanceValue);
        int intValue = (int) tmpValue;
        int finalSum = sum - intValue;
        if (finalSum > 0) {
            WebLink transferIntreConturi = new WebLink().setId("MainContent_TransactionMainContent_LandingQuickActionButtonsControl_rptShortcutsFiveItems_linkShortcutAction_2");
            transferIntreConturi.click();
            WebLocator contDeEconomiiEl = new WebLocator().setId("MainContent_TransactionMainContent_accControl_rptCurrentAccounts_divAccountType_1");
            RetryUtils.retry(2, contDeEconomiiEl::click);
            Button selectBtn = new Button().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwDataOwnAccounts_btnSuppliers");
            RetryUtils.retry(2, selectBtn::click);
            WebLocator contCurrentItemEl = new WebLocator().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwDataOwnAccounts_repeaterAccounts_liItens_0");
            RetryUtils.retry(2, contCurrentItemEl::click);
            Utils.sleep(1000);
            TextField sumaEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_FlowInnerContainerAmount_txtSourceAmount_txField");
            RetryUtils.retry(2, () -> {
                sumaEl.setValue(finalSum + "");
                sumaEl.sendKeys(Keys.TAB);
                String value = sumaEl.getValue().replaceAll(",", "");
                int actualSum = (int) Float.parseFloat(value);
                return finalSum == actualSum;
            });
            TextField descriptionEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_FlowInnerContainerAmount_txtPaymentReference_txField");
            descriptionEl.setValue("rAuto");
            nextWebLink.click();
            Utils.sleep(1000);
            nextWebLink.click();
            RetryUtils.retry(2, dashboard::click);
        }
    }

    public boolean makePayment(Item item, String folder) {
        plataNoua();
        Button beneficiaries = new Button().setAttribute("data-target", "#modalBeneficiaries");
        RetryUtils.retry(2, beneficiaries::click);
        TextField searchBeneficiary = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_FlowInnerContainer3_BTListBeneficiaries_txtSearch");
        RetryUtils.retry(2, () -> searchBeneficiary.setValue(item.getName()));
        Utils.sleep(1000);
        WebLocator titleEl = new WebLocator().setClasses("title").setText(item.getName(), SearchType.TRIM);
        WebLocator liEl = new WebLocator().setChildNodes(titleEl);
        RetryUtils.retry(10, liEl::click);
        Utils.sleep(1000);
        TextField sumEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwTransferDetails_txtAmount_txField");
        sumEl.ready(Duration.ofSeconds(40));
        RetryUtils.retry(2, () -> sumEl.setValue(item.getSum()));
        TextArea textAreaEl = new TextArea().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwTransferDetails_txtDescription_txField");
        textAreaEl.setValue(item.getDescription());
        nextWebLink.click();
        WebLink addInPacketButton = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_btnCartFlowItem");
        addInPacketButton.ready(Duration.ofSeconds(10));
        nextWebLink.click();
        Utils.sleep(1); //wait for SMS code
        nextWebLink.click();
        WebLocator message = new WebLocator().setId("MainContent_TransactionMainContent_divMessage");
        String text = RetryUtils.retry(20, message::getText);
        boolean success = text.equals("Tranzacție în curs de procesare. Verifică starea finală a acesteia în secțiunea Activitatea mea sau în secțiuneaTranzacții, disponibilă la nivelul contului");
        WebLink salvezPDF = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_proofControl_a1");
        String filePath = WebDriverConfig.getDownloadPath() + File.separator + "Plată.pdf";
        File pdfFile = new File(filePath);
        RetryUtils.retry(4, () -> {
            salvezPDF.click();
            return FileUtils.waitFileIfIsEmpty(pdfFile, 7000);
        });
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String fileName = "DovadaPlata" + item.getName().replaceAll(" ", "") + month + ".pdf";
        Storage.set("fileName", fileName);
        pdfFile.renameTo(new File(folder + fileName));
        RetryUtils.retry(2, dashboard::click);
        return success;
    }

    public boolean invoicePayment(Invoice invoice, String folder) {
        boolean success;
        boolean utilitati = invoice.getCategory().equals("Apa")
                || invoice.getCategory().equals("Gunoi")
                || invoice.getCategory().equals("Curent");
        if (utilitati) {
            WebLink platescUtilitatileEl = new WebLink().setId("MainContent_TransactionMainContent_LandingQuickActionButtonsControl_rptShortcutsFiveItems_linkShortcutAction_1");
            platescUtilitatileEl.ready(Duration.ofSeconds(40));
            RetryUtils.retry(3, platescUtilitatileEl::click);
            if (invoice.getCategory().equals("Apa")) {
                WebLocator apaEl = new WebLocator().setId("MainContent_TransactionMainContent_favoritsWithoutBeneficiariesControl_rptFavorits_aFavOper_1");
                apaEl.click();
            } else if (invoice.getCategory().equals("Curent")) {
                WebLocator curentEl = new WebLocator().setId("MainContent_TransactionMainContent_favoritsWithoutBeneficiariesControl_rptFavorits_aFavOper_3");
                curentEl.click();
            }
            TextField sumEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwData1_txtAmount_txField");
            sumEl.ready(Duration.ofSeconds(40));
            RetryUtils.retry(2, () -> sumEl.setValue(invoice.getValue()));
            if (invoice.getCategory().equals("Apa")) {
                TextField nrFacturiEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwData1_DynamicTooltip1_txField");
                nrFacturiEl.setValue(invoice.getNr());
            } else if (invoice.getCategory().equals("Curent")) {
                TextField codIncasareEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwData1_DynamicTooltip0_txField");
                codIncasareEl.setValue(invoice.getCod());
            }
            nextWebLink.click();
            nextWebLink.click();
            WebLocator message = new WebLocator().setId("MainContent_TransactionMainContent_divMessage");
            String text = RetryUtils.retry(20, message::getText);
            success = text.equals("Plata de utilități a fost procesată cu succes!");
        } else {
            plataNoua();
            TextField nameEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_FlowInnerContainer3_txtBeneficiaryName_txField");
            RetryUtils.retry(2, () -> nameEl.setValue(invoice.getFurnizor()));
            TextField ibanEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_FlowInnerContainer3_txtBeneficiaryIban_txField");
            ibanEl.setValue(invoice.getIban());
            ibanEl.sendKeys(Keys.ENTER);
            TextField sumaEl = new TextField().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwTransferDetails_txtAmount_txField");
            sumaEl.ready(Duration.ofSeconds(40));
            sumaEl.setValue(invoice.getValue());
            TextArea descriptionEl = new TextArea().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwTransferDetails_txtDescription_txField");
            descriptionEl.setValue("factura " + invoice.getNr());
            nextWebLink.click();
            WebLink addInPacketButton = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_btnCartFlowItem");
            addInPacketButton.ready(Duration.ofSeconds(10));
            nextWebLink.click();
            Utils.sleep(1); //wait for SMS code
            nextWebLink.click();
            WebLocator message = new WebLocator().setId("MainContent_TransactionMainContent_divMessage");
            String text = RetryUtils.retry(20, message::getText);
            success = text.equals("Plată inițiată cu succes. Verifică starea finală a acesteia în secțiunea Activitatea mea sau în secțiunea Tranzacții, disponibilă la nivelul contului.");
        }
        WebLink salvezPDF = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_proofControl_a1");
        String name = utilitati ? "Ordin de plată.pdf" : "Plată.pdf";
        String filePath = WebDriverConfig.getDownloadPath() + File.separator + name;
        File pdfFile = new File(filePath);
        RetryUtils.retry(4, () -> {
            salvezPDF.click();
            return FileUtils.waitFileIfIsEmpty(pdfFile, 7000);
        });
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String extra = (utilitati ? invoice.getCategory() : invoice.getNr()).replaceAll(" ", "");
        String fileName = "DovadaPlata" + extra + month + ".pdf";
        Storage.set("fileName", fileName);
        pdfFile.renameTo(new File(folder + fileName));
        RetryUtils.retry(2, dashboard::click);
        return success;
    }

    private static void plataNoua() {
        WebLink plataNouaEl = new WebLink().setId("MainContent_TransactionMainContent_LandingQuickActionButtonsControl_rptShortcutsFiveItems_linkShortcutAction_0");
        plataNouaEl.ready(Duration.ofSeconds(40));
        RetryUtils.retry(3, plataNouaEl::click);
    }

    @SneakyThrows
    public void saveReportFrom(String identify, String month, String location) {
        org.apache.commons.io.FileUtils.cleanDirectory(new File(WebDriverConfig.getDownloadPath()));
        String actualMonth = "";
        LocalDate now = null;
        for (int i = 0; i < 12; i++) {
            now = LocalDate.now().minusMonths(i);
            actualMonth = now.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, roLocale);
            if (actualMonth.equalsIgnoreCase(month)) {
                break;
            }
        }
        String firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        WebLocator productList = new WebLocator().setClasses("productListLP");
        WebLocator child = new WebLocator().setText(identify);
        WebLocator row = new WebLocator(productList).setClasses("row", "product-container").setChildNodes(child);
        WebLocator span = new WebLocator(row).setClasses("primary-description");
        span.click();
        WebLink transaction = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwContainer_goTransaction");
        transaction.click();
        filter(firstDayOfMonth, lastDayOfMonth);
        boolean contCurrent = identify.contains("Cont curent");
        WebLink exportCSV = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_proofControl_a8");
        if (exportCSV.isPresent()) {
            RetryUtils.retry(2, exportCSV::click);
        } else {
            WebLink exportExcel = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_proofControl_a6");
            RetryUtils.retry(2, exportExcel::click);
        }
        List<Path> list = RetryUtils.retry(Duration.ofSeconds(25), () -> {
            List<Path> paths = Files.list(Paths.get(WebDriverConfig.getDownloadPath())).toList();
            if (!paths.isEmpty()) {
                return paths;
            } else {
                return null;
            }
        });
        Optional<Path> first = list.stream().filter(i -> !Files.isDirectory(i)).findFirst();
        if (first.isPresent()) {
            Path path = first.get();
            File file = path.toFile();
            String fileName = file.getName();
            if (!contCurrent) {
                String extension = fileName.substring(fileName.lastIndexOf("."));
                fileName = StringUtils.capitalize(actualMonth) + extension;
            }
            Storage.set("fileName", fileName);
            file.renameTo(new File(location + fileName));
        }
    }

    @SneakyThrows
    public void saveCardReportFrom(String identify, String month, String location) {
        String actualMonth = "";
        LocalDate now = null;
        for (int i = 0; i < 12; i++) {
            now = LocalDate.now().minusMonths(i);
            actualMonth = now.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, roLocale);
            if (actualMonth.equalsIgnoreCase(month)) {
                break;
            }
        }
        String firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        WebLink cardEl = new WebLink(null, identify).setAttribute("id", "MainContent_TransactionMainContent_BTLandingProductsControl_rptProductListCards_primarydescription", SearchType.CONTAINS);
        cardEl.click();
        WebLink tranzactiiEl = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwContainer_goTransaction");
        tranzactiiEl.click();
        filter(firstDayOfMonth, lastDayOfMonth);
        WebLink exportExcel = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_flwData1_proofControl_a6");
        RetryUtils.retry(2, exportExcel::click);
        List<Path> list = RetryUtils.retry(Duration.ofSeconds(45), () -> {
            List<Path> paths = Files.list(Paths.get(WebDriverConfig.getDownloadPath())).toList();
            Optional<Path> xls = paths.stream().filter(i -> !i.getFileName().toString().endsWith("xls")).findAny();
            if (!paths.isEmpty() && xls.isEmpty()) {
                return paths;
            } else {
                return null;
            }
        });
        Optional<Path> first = list.stream().filter(i -> !Files.isDirectory(i)).findFirst();
        if (first.isPresent()) {
            Path path = first.get();
            File file = path.toFile();
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String extra = formatName(identify);
            fileName = StringUtils.capitalize(actualMonth) + extra + extension;
            Storage.set("fileName", fileName);
            file.renameTo(new File(location + fileName));
        }
    }

    public static String formatName(String name) {
        String[] words = name.split(" ");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }

        return formattedName.toString();
    }

    private static void filter(String firstDayOfMonth, String lastDayOfMonth) {
        WebLocator filterEl = new WebLocator().setId("lblClose");
        WebLocatorUtils.scrollToWebLocator(filterEl, -200);
        filterEl.click();
        WebLocator fromEl = new WebLocator().setTag("input").setId("MainContent_TransactionMainContent_txpTransactions_ctl01_dpFromTo_dateFromPicker_txField");
        fromEl.clear();
        fromEl.sendKeys(firstDayOfMonth);
        fromEl.sendKeys(Keys.ENTER);
        WebLocator toEl = new WebLocator().setTag("input").setId("MainContent_TransactionMainContent_txpTransactions_ctl01_dpFromTo_dateToPicker_txField");
        toEl.clear();
        toEl.sendKeys(lastDayOfMonth);
        toEl.sendKeys(Keys.ENTER);
        WebLocator aplayFilter = new WebLocator().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_btnSearch");
        aplayFilter.click();
        Utils.sleep(1000);
    }

    public void goToDashboard() {
        WebLocator dashboard = new WebLocator().setTag("img").setClasses("logo", "img-responsive");
        dashboard.click();
    }

    public void convertToCSV(String path) {
        File file = new File(path);
        String fileName = file.getName();
        String csvFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".csv";
        Storage.set("fileName", csvFileName);
        File csvFile = new File(file.getParent() + File.separator + csvFileName);
        new XlsxUtility(file).convertToCSV(csvFile);
    }
}
