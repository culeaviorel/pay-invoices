package ro.btgo;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.base.Strings;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.FileUtility;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.neo.Invoice;
import ro.neo.MemberPay;
import ro.neo.Storage;
import ro.sheet.GoogleSheet;
import ro.sheet.ItemTO;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class BTGoSteps extends TestBase {

    private static final String contracteDeSponsorizareId = "13SphvJvXIInYDd1pzYc-gIa0pI1HxEWa5JAUgAqhXfI";
    private static final String facturiSheetId = "1SL4EGDDC3qf1X80s32OOEMxmVbvlL7WRbh5Kr88hPy0";
    private static final String membriCuCopiiiLaGradinitaId = "1uxtyl_NBBHTWnmN7FVF_N5uE43iiqHHG1tDKC4-7ANg";
    private static final String beneficiariId = "1ctmU3ZKJm9u44fvHsfn2IjS1XXNRU6YEFMT6TV_cCvU";
    private static final String csvFolderId = "1Uc2IebVqTxFSYJSDcnBXdjHCw9ioHDmR"; //2024/CSV
    private static final String csv2025FolderId = "1lCbGDNT0uY833xkKoMEXLezsa2mDwkqD"; //2025/CSV
    private final BTGo btGo = new BTGo();
    private final AppUtils appUtils = new AppUtils();
    private static List<Pay> pays;
    private static List<MemberPay> memberPays;
    private static Sheets sheetsService;
    private final Locale roLocale = new Locale("ro", "RO");
    private final UserCredentials credentials = new UserCredentials();

    @And("I login in BTGo")
    public void iLoginInBTGo() {
        btGo.login(credentials.getBTGoID(), credentials.getBTGoPassword());
        Utils.sleep(1); //wait for accept from BTGo
    }

    @SneakyThrows
    @And("in BTGo I pay invoices:")
    public void inBTGoIPayInvoices(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            if (!Strings.isNullOrEmpty(invoice.getFileName()) && invoice.getFileName().contains(".pdf")) {
                File file = new File(facturi2025() + invoice.getFileName());
                String text = FileUtility.getPDFContent(file);
                List<String> list = text.lines().toList();
                switch (invoice.getCategory()) {
                    case "Apa" -> appUtils.collectForApa(invoice, list);
                    case "Gunoi" -> appUtils.collectForGunoi(invoice, list);
                    case "Curent" -> appUtils.collectForCurent(invoice, list);
                    case "Gaz" -> appUtils.collectForGas(invoice, list);
                }
            }
            double doubleValue = Double.parseDouble(invoice.getValue());
            int extraValue = getExtraValue(invoice);
            int intValue = (int) doubleValue + extraValue;
            btGo.transferBetweenConts(intValue, credentials.getContDeEconomii(), credentials.getContCurent());

            boolean success = btGo.invoicePayment(invoice, dovada2025());
            if (success) {
                String fileName = Storage.get("fileName");
                String facturaFilePath = invoice.getFileName() == null || fileName == null ? null : facturi2025() + invoice.getFileName();
                String dovadaFilePath = dovada2025() + fileName;
                String deciziaFilePath = invoice.getDecizia() == null ? null : deciziile2025() + invoice.getDecizia();
                double value = Double.parseDouble(invoice.getValue());
                appUtils.uploadFileAndAddRowInFacturiAndContForItem(facturaFilePath, dovadaFilePath, deciziaFilePath, invoice.getCategory(), invoice.getDescription(), value, invoice.getData());
            }
        }
    }

    private static int getExtraValue(Invoice invoice) {
        return invoice.getIban().contains("RNCB") || invoice.getIban().contains("CECE")
                || invoice.getIban().contains("BACX") || invoice.getIban().contains("RZBR") ? 5 : 1;
    }

    @And("in BTGo I save report from {string} month")
    public void inBTGoISaveReportFromCurrentMonth(String selectedMonth) {
        String actualMonth;
        LocalDate now = null;
        for (int i = 0; i < 12; i++) {
            now = LocalDate.now().minusMonths(i);
            actualMonth = now.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, roLocale);
            if (actualMonth.equalsIgnoreCase(selectedMonth)) {
                break;
            }
        }
        String firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        WebLink maiMulte = new WebLink(null, "Mai multe", SearchType.TRIM);
        RetryUtils.retry(2, () -> {
            boolean ready = maiMulte.ready(Duration.ofSeconds(15));
            if (!ready) {
                WebDriverConfig.getDriver().navigate().refresh();
            }
            return ready;
        });
        maiMulte.click();
        String fileName = btGo.saveReport(credentials.getContCurent(), firstDayOfMonth, lastDayOfMonth, csv());

        appUtils.uploadFileInDrive(csv() + fileName, csv2025FolderId);

        fileName = btGo.saveReport(credentials.getContDeEconomii(), firstDayOfMonth, lastDayOfMonth, csv());
        appUtils.uploadFileInDrive(csv() + fileName, csv2025FolderId);
        Utils.sleep(1);
    }

    @SneakyThrows
    @And("I prepare data for Donatii cu destinatie speciala New from google sheet")
    public void iPrepareDataForDonatiiCuDestinatieSpecialaNewFromGoogleSheet() {
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(contracteDeSponsorizareId, "Donatii cu destinatie speciala New" + "!B2:F").execute();
        List<List<Object>> values = valueRange.getValues();
        pays = values.stream().map(i -> new Pay(
                        i.get(0).toString(),
                        i.get(1).toString(),
                        i.get(2).toString(),
                        i.get(3).toString(),
                        i.get(4).toString()
                )).filter(i -> !Strings.isNullOrEmpty(i.destination()))
                .filter(i -> Integer.parseInt(i.suma()) > 0)
                .toList();
    }

    @And("in BTGo I send Donatii cu destinatie speciala from google sheet")
    public void inBTGoISendDonatiiCuDestinatieSpecialaFromGoogleSheet() {
        LocalDate now = LocalDate.now();
        List<Pay> paysResult = new ArrayList<>();
        for (Pay pay : pays) {
            for (int i = 0; i < 12; i++) {
                String month = now.minusMonths(i).getMonth().getDisplayName(TextStyle.FULL, roLocale);
                if (pay.month().equalsIgnoreCase(month)) {
                    break;
                } else {
                    paysResult.add(pay);
                }
            }
        }
        int sum = paysResult.stream().mapToInt(n -> Integer.parseInt(n.suma())).sum();
        if (sum > 0) {
            List<List<Object>> values = appUtils.getValues(beneficiariId, "Beneficiari!A2:D");
            List<Beneficiar> beneficiars = values.stream().map(i -> {
                Beneficiar beneficiar = new Beneficiar(
                        i.get(0).toString(),
                        i.get(1).toString(),
                        i.get(2).toString(),
                        i.size() == 4 ? i.get(3).toString() : "");
                return beneficiar;
            }).toList();
            btGo.transferBetweenConts(sum, credentials.getContDeEconomii(), credentials.getContCurent());
            Map<String, List<Pay>> listMap = paysResult.stream().collect(Collectors.groupingBy(Pay::destination));
            Integer sheetId = appUtils.getSheetId(contracteDeSponsorizareId, "Donatii cu destinatie speciala New");
            String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
            for (Map.Entry<String, List<Pay>> stringListEntry : listMap.entrySet()) {
                String key = stringListEntry.getKey();
                List<Pay> payList = stringListEntry.getValue();
                int sumForDestination = payList.stream().mapToInt(n -> Integer.parseInt(n.suma())).sum();
                List<Pay> payDistinct = payList.stream().distinct().toList();
                List<String> descriptions = payDistinct.stream().map(Pay::description).toList();
                String descriptionString = String.join(", ", descriptions);
                Beneficiar beneficiar = beneficiars.stream().filter(i -> i.name().equals(key)).findFirst().orElseGet(() -> new Beneficiar(key, "", "", ""));
                LocalDate localDate = LocalDate.now();
                localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                Invoice invoice = new Invoice(null, null, null, key, String.valueOf(sumForDestination), "Donatie de la " + descriptionString, null, null, beneficiar.beneficiar(), beneficiar.iban(), localDate);
                boolean successPayment = btGo.invoicePayment(invoice, dovada2025());
                if (successPayment) {
                    changeMonthInSheetNew(month, payDistinct, pays, sheetId);
                    String fileName = Storage.get("fileName");
                    double value = Double.parseDouble(String.valueOf(sumForDestination));
                    String category = invoice.getCategory().replaceAll(" ", "") + "Out";
                    appUtils.uploadFileAndAddRowInFacturiAndContForItem(null, dovada2025() + fileName, null, category, "plata", value, localDate);
                }
            }
        }
    }

    @SneakyThrows
    private void changeMonthInSheetNew(String month, List<Pay> payList, List<Pay> pays, Integer sheetId) {
        int columnIndex = 5;
        for (Pay pay : payList) {
            List<Request> requests = new ArrayList<>();
            int rowIndex = pays.indexOf(pay) + 1;
            GoogleSheet.addItemForUpdate(month, rowIndex, columnIndex, sheetId, requests);
            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(contracteDeSponsorizareId, batchUpdateRequest).execute();
            log.info("add month: {} for category: {}", month, pay.destination());
        }
    }

    @SneakyThrows
    @And("I prepare data for Sustinere educatie from google sheet")
    public void iPrepareDataForSustinereEducatieFromGoogleSheet() {
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(membriCuCopiiiLaGradinitaId, "2025!A2:E").execute();
        List<List<Object>> values = valueRange.getValues();
        memberPays = values.stream().map(i -> new MemberPay(
                i.get(0).toString(),
                i.get(1).toString(),
                i.get(2).toString(),
                i.get(3).toString(),
                i.size() == 5 ? i.get(4).toString() : ""
        )).toList();
        Utils.sleep(1);
    }

    @And("in BTGo I send Sustinere educatie from google sheet")
    public void inBTGoISendSustinereEducatieFromGoogleSheet() {
        LocalDate now = LocalDate.now();
        now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        List<MemberPay> memberPayList = memberPays.stream().filter(i -> Strings.isNullOrEmpty(i.status())).toList();
        int total = memberPayList.stream().flatMapToInt(i -> IntStream.of(Integer.parseInt(i.sum()))).sum();
        btGo.transferBetweenConts(total, credentials.getContDeEconomii(), credentials.getContCurent());
        for (MemberPay memberPay : memberPayList) {
            Invoice invoice = new Invoice(null, null, null, "Sustinere Educatie", memberPay.sum(), memberPay.description(), null, null, memberPay.name(), memberPay.iban(), now);
            boolean success = btGo.invoicePayment(invoice, dovada2025());
            if (success) {
                changeStatusInSheet(memberPay);
                String fileName = Storage.get("fileName");
                double value = Double.parseDouble(memberPay.sum());
                appUtils.uploadFileAndAddRowInFacturiAndContForItem(null, dovada2025() + fileName, null, "Sustinere Educatie", "pentru " + memberPay.name(), value, now);
            }
        }
    }

    @SneakyThrows
    private void changeStatusInSheet(MemberPay memberPay) {
        int row = memberPays.indexOf(memberPay) + 1;
        Integer sheetId = appUtils.getSheetId(membriCuCopiiiLaGradinitaId, "Membri cu copiii la gradinita");
        List<Request> requests = new ArrayList<>();
        GoogleSheet.addItemForUpdate("Trimis", row, 4, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(membriCuCopiiiLaGradinitaId, batchUpdateRequest).execute();
    }

    @And("I move all from Cont Current to Depozit in BTGo")
    public void iMoveAllFromContCurrentToDepozitInBTGo() {
        int sum = 0;
        btGo.transferBetweenConts(sum, credentials.getContCurent(), credentials.getContDeEconomii());
    }

    @And("I generate extras from all in BTGo")
    public void iGenerateExtrasFromAllInBTGo() {
        List<String> months = List.of(
                "IAN"
//                ,"FEB"
//                ,"MART"
//                ,"APR"
//                ,"MAI"
//                ,"IUN"
//                ,"IUL"
//                ,"AUG"
//                ,"SEPT"
//                ,"OCT"
        );
        for (String month : months) {
            btGo.generateExtrasFromAll(month, extrase());
        }
    }

    @And("I create depozit from Cont de Economii in BTGo")
    public void iCreateDepozitFromContDeEconomiiInBTGo() {
        int value = 200000;
        btGo.transferBetweenConts(value, credentials.getContDeEconomii(), credentials.getContCurent());
        btGo.createDepozit(value + "");
    }

    @SneakyThrows
    @And("in BTGo I pay deconts:")
    public void inBTGoIPayDeconts(List<ItemTO> deconts) {
        for (ItemTO item : deconts) {
            String decontPath = decont2025() + item.getDecont();
            File file = new File(decontPath);
            Invoice invoice = new Invoice();
            invoice.setCategory(item.getCategory());
            if (file.exists()) {
                String text = FileUtility.getPDFContent(file);
                List<String> list = text.lines().toList();
                invoice = appUtils.collectForDecont(invoice, list);
                invoice.setDecont(item.getDecont());
                double doubleValue = Double.parseDouble(invoice.getValue());
                int intValue = (int) doubleValue + getExtraValue(invoice);
                btGo.transferBetweenConts(intValue, credentials.getContDeEconomii(), credentials.getContCurent());
                boolean success = btGo.invoicePayment(invoice, dovada2025());
                if (success) {
                    String fileName = Storage.get("fileName");
                    appUtils.uploadFileAndAddRowInFacturiAndCont(dovada2025() + fileName, item.getDecont());
                }
            }
        }
    }

    @And("I upload file {string}")
    public void iUploadFile(String fileName) {
        String dovadaFilePath = dovada2025() + fileName;
        appUtils.uploadFileAndAddRowInFacturiAndCont(dovadaFilePath, "Decont5.pdf");
    }

    @And("I read pdf {string}")
    public void iReadPdf(String filePath) {
        File file = new File(filePath);
        FileUtility.getPDFContentV2(file);
    }
}
