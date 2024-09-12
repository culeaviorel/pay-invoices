package ro.casomes;

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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import ro.neo.Item;
import ro.neo.Storage;

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
public class CaSomes {
    private final WebLink nextWebLink = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_btnNextFlowItem");
    private final WebLink dashboard = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_linkGoDashboard");
    private final Locale roLocale = new Locale("ro", "RO");

    public void login(String email, String password) {
        WebLink googleLogin = new WebLink().setClasses("btn", "btn-google");
        googleLogin.click();

        TextField emailEl = new TextField().setId("identifierId");
        emailEl.ready(Duration.ofSeconds(40));
        RetryUtils.retry(40, () -> emailEl.setValue(email));
        WebLocator nextButton = new WebLocator().setTag("button").setText("Next", SearchType.DEEP_CHILD_NODE_OR_SELF);
        nextButton.click();
        TextField passwordEl = new TextField().setName("password");
        InputButton logIn = new InputButton().setText("MERG MAI DEPARTE");
        passwordEl.setValue(password);
        logIn.click();
        acceptAll();
        Utils.sleep(1); //wait for SMS code
        logIn.click();
    }

    private void acceptAll() {
        Button acceptAllButton = new Button().setClasses("gdprIntro_acceptAll");
        acceptAllButton.click();
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
                return sum == actualSum;
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
        WebLink plataNouaEl = new WebLink().setId("MainContent_TransactionMainContent_LandingQuickActionButtonsControl_rptShortcutsFiveItems_linkShortcutAction_0");
        plataNouaEl.ready(Duration.ofSeconds(40));
        RetryUtils.retry(3, plataNouaEl::click);
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
        boolean success = text.equals("Plată inițiată cu succes. Verifică starea finală a acesteia în secțiunea Activitatea mea sau în secțiunea Tranzacții, disponibilă la nivelul contului.");
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
        WebLocator panelConturi = new WebLocator().setClasses("productListLP");
        WebLocator child = new WebLocator().setText(identify, SearchType.CONTAINS_ALL);
        WebLocator row = new WebLocator(panelConturi).setClasses("row").setChildNodes(child);
        WebLink istoricLink = new WebLink(row, "Istoric", SearchType.DEEP_CHILD_NODE_OR_SELF, SearchType.TRIM);
        istoricLink.click();
        WebLocator filterEl = new WebLocator().setId("lblClose");
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
        boolean contCurrent = identify.contains("Cont curent");
        if (contCurrent) {
            WebLink exportCSV = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_proofControl_a8");
            RetryUtils.retry(2, () -> exportCSV.click());
        } else {
            WebLink exportExcel = new WebLink().setId("MainContent_TransactionMainContent_txpTransactions_ctl01_proofControl_a6");
            RetryUtils.retry(2, () -> exportExcel.click());
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

    public void goToDashboard() {
        WebLocator dashboard = new WebLocator().setTag("img").setClasses("logo", "img-responsive");
        dashboard.click();
    }
}
