package ro.neo;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.base.Strings;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.sheet.GoogleSheet;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class NeoSteps extends TestBase {

    private static final String contracteDeSponsorizareId = "13SphvJvXIInYDd1pzYc-gIa0pI1HxEWa5JAUgAqhXfI";
    private static final String membriCuCopiiiLaGradinitaId = "1uxtyl_NBBHTWnmN7FVF_N5uE43iiqHHG1tDKC4-7ANg";
    private final Neo neo = new Neo();
    private final AppUtils appUtils = new AppUtils();
    private static List<Pay> pays;
    private static List<MemberPay> memberPays;
    private static Sheets sheetsService;

    @And("I login in Neo")
    public void iLoginInNeo() {
        UserCredentials credentials = new UserCredentials();
        neo.login(credentials.getNeoID(), credentials.getNeoPassword());
    }

    @SneakyThrows
    @And("I prepare data for Donatii cu destinatie speciala from google sheet")
    public void iPrepareDataForDonatiiCuDestinatieSpecialaFromGoogleSheet() {
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(contracteDeSponsorizareId, "Donatii cu destinatie speciala" + "!B1:J").execute();
        List<List<Object>> values = valueRange.getValues();
        pays = values.stream().map(i -> new Pay(
                i.get(0).toString(),
                i.get(1).toString(),
                i.get(2).toString(),
                i.get(3).toString(),
                i.get(4).toString(),
                i.get(5).toString(),
                i.get(6).toString(),
                i.get(7).toString(),
                i.get(8).toString()
        )).toList();
        Utils.sleep(1);
    }

    @And("in NeoBT I select profile {string}")
    public void inNeoBTISelectProfile(String profile) {
        neo.selectProfile(profile);
    }

    @SneakyThrows
    @And("in NeoBT I send Donatii cu destinatie speciala from google sheet")
    public void inNeoBTISendDonatiiCuDestinatieSpecialaFromGoogleSheet() {
        Optional<Pay> totalOnMonth = pays.stream().filter(i -> i.name().equals("Total pe luna")).findFirst();
        Pay totalOnMonthPay = totalOnMonth.orElse(null);
        PayCount countMonths = countMonths();
        if (totalOnMonthPay != null && countMonths != null) {
            PayCount payCount = getPayCount(countMonths, totalOnMonthPay);
            int total = payCount.total();
            neo.transferFromDepozitIntoContCurent(total);
            Payment payment = preparePayment(payCount);
            List<Item> items = payment.getItems();
            items.forEach(i -> log.info("item: {}", i));
            for (Item item : items) {
                item.setDescription("donatie de la " + item.getDescription());
                boolean successPayment = neo.makePayment(item, dovada());
                if (successPayment) {
                    changeMonthInSheet(item.getName());
                    String fileName = Storage.get("fileName");
                    double value = Double.parseDouble(item.getSum());
                    String category = item.getName().replaceAll(" ", "") + "Out";
                    new AppUtils().uploadFileAndAddRowInFacturiAndContForItem(null, dovada() + fileName, category, "plata", value);
                }
            }
        }
    }

    @SneakyThrows
    private void changeMonthInSheet(String name) {
        List<String> list = pays.get(0).toList();
        int columnIndex = list.indexOf(name) + 3;
        Integer sheetId = appUtils.getSheetId(contracteDeSponsorizareId, "Donatii cu destinatie speciala");
        List<Request> requests = new ArrayList<>();
        String month = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        GoogleSheet.addItemForUpdate(month, 12, columnIndex, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(contracteDeSponsorizareId, batchUpdateRequest).execute();
        log.info("add month: {} for category: {}", month, name);
    }

    private Payment preparePayment(PayCount payCount) {
        List<String> people = pays.stream().map(Pay::name).filter(Objects::nonNull).skip(1).limit(7).toList();
        Item somethingNewItem = new Item();
        Item teenChellangeItem = new Item();
        Item casaFilipItem = new Item();
        Item tanzaniaItem = new Item();
        Item caminulFelixItem = new Item();
        Item alegeViataItem = new Item();
        Item apmeItem = new Item();
        for (String person : people) {
            Optional<Pay> pay = pays.stream().filter(i -> i.name().equals(person)).findFirst();
            if (pay.isPresent()) {
                Pay payPerson = pay.get();
                if (!payPerson.somethingNew().equals("0") && payCount.somethingNew() > 0) {
                    String separator = Strings.isNullOrEmpty(somethingNewItem.getName()) ? "" : ", ";
                    somethingNewItem.setName("Something New");
                    String description = (Strings.isNullOrEmpty(somethingNewItem.getDescription()) ? "" : somethingNewItem.getDescription()) + separator + payPerson.description();
                    somethingNewItem.setDescription(description);
                }
                if (!payPerson.teenChallenge().equals("0") && payCount.teenChallenge() > 0) {
                    String separator = Strings.isNullOrEmpty(teenChellangeItem.getName()) ? "" : ", ";
                    teenChellangeItem.setName("Teen Challenge");
                    String description = (Strings.isNullOrEmpty(teenChellangeItem.getDescription()) ? "" : teenChellangeItem.getDescription()) + separator + payPerson.description();
                    teenChellangeItem.setDescription(description);
                }
                if (!payPerson.casaFilip().equals("0") && payCount.casaFilip() > 0) {
                    String separator = Strings.isNullOrEmpty(casaFilipItem.getName()) ? "" : ", ";
                    casaFilipItem.setName("Casa Filip");
                    String description = (Strings.isNullOrEmpty(casaFilipItem.getDescription()) ? "" : casaFilipItem.getDescription()) + separator + payPerson.description();
                    casaFilipItem.setDescription(description);
                }
                if (!payPerson.tanzania().equals("0") && payCount.tanzania() > 0) {
                    String separator = Strings.isNullOrEmpty(tanzaniaItem.getName()) ? "" : ", ";
                    tanzaniaItem.setName("Tanzania");
                    String description = (Strings.isNullOrEmpty(tanzaniaItem.getDescription()) ? "" : tanzaniaItem.getDescription()) + separator + payPerson.description();
                    tanzaniaItem.setDescription(description);
                }
                if (!payPerson.caminulFelix().equals("0") && payCount.caminulFelix() > 0) {
                    String separator = Strings.isNullOrEmpty(caminulFelixItem.getName()) ? "" : ", ";
                    caminulFelixItem.setName("Caminul Felix");
                    String description = (Strings.isNullOrEmpty(caminulFelixItem.getDescription()) ? "" : caminulFelixItem.getDescription()) + separator + payPerson.description();
                    caminulFelixItem.setDescription(description);
                }
                if (!payPerson.alegeViata().equals("0") && payCount.alegeViata() > 0) {
                    String separator = Strings.isNullOrEmpty(alegeViataItem.getName()) ? "" : ", ";
                    alegeViataItem.setName("Alege Viata");
                    String description = (Strings.isNullOrEmpty(alegeViataItem.getDescription()) ? "" : alegeViataItem.getDescription()) + separator + payPerson.description();
                    alegeViataItem.setDescription(description);
                }
                if (!payPerson.apme().equals("0") && payCount.apme() > 0) {
                    String separator = Strings.isNullOrEmpty(apmeItem.getName()) ? "" : ", ";
                    apmeItem.setName("APME");
                    String description = (Strings.isNullOrEmpty(apmeItem.getDescription()) ? "" : apmeItem.getDescription()) + separator + payPerson.description();
                    apmeItem.setDescription(description);
                }
            }
        }
        List<Item> items = new ArrayList<>();
        if (payCount.somethingNew() > 0) {
            somethingNewItem.setSum(payCount.somethingNew() + "");
            items.add(somethingNewItem);
        }
        if (payCount.teenChallenge() > 0) {
            teenChellangeItem.setSum(payCount.teenChallenge() + "");
            items.add(teenChellangeItem);
        }
        if (payCount.casaFilip() > 0) {
            casaFilipItem.setSum(payCount.casaFilip() + "");
            items.add(casaFilipItem);
        }
        if (payCount.tanzania() > 0) {
            tanzaniaItem.setSum(payCount.tanzania() + "");
            items.add(tanzaniaItem);
        }
        if (payCount.caminulFelix() > 0) {
            caminulFelixItem.setSum(payCount.caminulFelix() + "");
            items.add(caminulFelixItem);
        }
        if (payCount.alegeViata() > 0) {
            alegeViataItem.setSum(payCount.alegeViata() + "");
            items.add(alegeViataItem);
        }
        if (payCount.apme() > 0) {
            apmeItem.setSum(payCount.apme() + "");
            items.add(apmeItem);
        }
        return new Payment(items);
    }

    private static PayCount getPayCount(PayCount countMonths, Pay totalOnMonthPay) {
        int somethingNew = countMonths.somethingNew() * Integer.parseInt(totalOnMonthPay.somethingNew());
        int teenChallenge = countMonths.teenChallenge() * Integer.parseInt(totalOnMonthPay.teenChallenge());
        int casaFilip = countMonths.casaFilip() * Integer.parseInt(totalOnMonthPay.casaFilip());
        int tanzania = countMonths.tanzania() * Integer.parseInt(totalOnMonthPay.tanzania());
        int caminulFelix = countMonths.caminulFelix() * Integer.parseInt(totalOnMonthPay.caminulFelix());
        int alegeViata = countMonths.alegeViata() * Integer.parseInt(totalOnMonthPay.alegeViata());
        int ampe = countMonths.apme() * Integer.parseInt(totalOnMonthPay.apme());
        return new PayCount(somethingNew, teenChallenge, casaFilip, tanzania, caminulFelix, alegeViata, ampe);
    }

    private PayCount countMonths() {
        Optional<Pay> payedMonth = pays.stream().filter(i -> i.name().equals("Luna platita")).findFirst();
        if (payedMonth.isEmpty()) {
            return null;
        }
        Pay payedMonths = payedMonth.get();
        LocalDate now = LocalDate.now();
        int somethingNewCount = -1;
        int teenChellangeCount = -1;
        int casaFilipCount = -1;
        int tanzaniaCount = -1;
        int caminulFelixCount = -1;
        int alegeViataCount = -1;
        int apmeCount = -1;
        for (int i = 0; i < 12; i++) {
            String month = now.minusMonths(i).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            if (payedMonths.somethingNew().equals(month)) {
                somethingNewCount = i;
            }
            if (payedMonths.teenChallenge().equals(month)) {
                teenChellangeCount = i;
            }
            if (payedMonths.casaFilip().equals(month)) {
                casaFilipCount = i;
            }
            if (payedMonths.tanzania().equals(month)) {
                tanzaniaCount = i;
            }
            if (payedMonths.caminulFelix().equals(month)) {
                caminulFelixCount = i;
            }
            if (payedMonths.alegeViata().equals(month)) {
                alegeViataCount = i;
            }
            if (payedMonths.apme().equals(month)) {
                apmeCount = i;
            }
            if (somethingNewCount != -1
                    && teenChellangeCount != -1
                    && casaFilipCount != -1
                    && tanzaniaCount != -1
                    && caminulFelixCount != -1
                    && alegeViataCount != -1
                    && apmeCount != -1
            ) {
                break;
            }
        }
        return new PayCount(
                somethingNewCount,
                teenChellangeCount,
                casaFilipCount,
                tanzaniaCount,
                caminulFelixCount,
                alegeViataCount,
                apmeCount
        );
    }

    @SneakyThrows
    @And("in NeoBT I upload file in google sheet")
    public void inNeoBTIUploadFileInGoogleSheet() {
        new AppUtils().uploadFileAndAddRowInFacturiAndContForItem(null, dovada() + "DovadaPlataCasaFilipAprilie.pdf", "CasaFilipOut", "plata", 200.00);
    }


    @And("in NeoBT I send Sustinere educatie from google sheet")
    public void inNeoBTISendSustinereEducatieFromGoogleSheet() {
        List<MemberPay> memberPayList = memberPays.stream().filter(i -> Strings.isNullOrEmpty(i.status())).toList();
        int total = memberPayList.stream().flatMapToInt(i -> IntStream.of(Integer.parseInt(i.sum()))).sum();
        neo.transferFromDepozitIntoContCurent(total);
        for (MemberPay memberPay : memberPayList) {
            boolean success = neo.makePayment(new Item(memberPay.name(), memberPay.sum(), memberPay.description()), dovada());
            if (success) {
//                changeStatusInSheet(memberPay);
                String fileName = Storage.get("fileName");
                double value = Double.parseDouble(memberPay.sum());
                new AppUtils().uploadFileAndAddRowInFacturiAndContForItem(null, dovada() + fileName, "Sustinere Educatie", "pentru " + memberPay.name(), value);
            }
        }
    }

    @And("in NeoBT I save report from {string} month")
    public void inNeoBTISaveReportFromMonth(String month) {
        String location = location() + "CSV\\";
        neo.saveReportFrom("RO46BTRL06701205T61531XX", month, location);
        String fileName = Storage.get("fileName");
        String csvFolderId = "1Uc2IebVqTxFSYJSDcnBXdjHCw9ioHDmR"; //2024/CSV
        appUtils.uploadFileInDrive(location + fileName, csvFolderId);
        neo.goToDashboard();
        neo.saveReportFrom("RO38BTRLRONECON0T6153101", month, location);
        fileName = Storage.get("fileName").toString().replaceAll("xls", "csv");
        Utils.sleep(1); // Convert manually xls file to csv in CSV folder
        appUtils.uploadFileInDrive(location + fileName, csvFolderId);
    }

    @And("in NeoBT I save card report local from {list} month")
    public void inNeoBTISaveReportLocalFromMonth(List<String> months) {
        String location = bt();
        for (String month : months) {
            neo.saveCardReportFrom("CULEA VIOREL", month, location);
            neo.goToDashboard();
        }
        for (String month : months) {
            neo.saveCardReportFrom("CAMELIA CULEA", month, location);
            neo.goToDashboard();
        }
    }

//    @SneakyThrows
//    @And("in NeoBT I pay invoices:")
//    public void inNeoBTIPayInvoices(List<Invoice> invoices) {
////        double total = invoices.stream().flatMapToDouble(i -> DoubleStream.of(Double.parseDouble(i.getValue()))).sum();
//        for (Invoice invoice : invoices) {
//            if (invoice.getFileName().contains(".pdf")) {
//                PDDocument document = PDDocument.load(new java.io.File(facturi() + invoice.getFileName()));
//                PDFTextStripper pdfStripper = new PDFTextStripper();
//                String text = pdfStripper.getText(document);
//                document.close();
//                List<String> list = text.lines().toList();
//                switch (invoice.getCategory()) {
//                    case "Apa" -> appUtils.collectForApa(invoice, list);
//                    case "Gunoi" -> appUtils.collectForGunoi(invoice, list);
//                    case "Curent" -> appUtils.collectForCurent(invoice, list);
//                }
//            }
//            double doubleValue = Double.parseDouble(invoice.getValue());
//            int intValue = (int) doubleValue + 1;
//            neo.transferFromDepozitIntoContCurent(intValue);
//            boolean success = neo.invoicePayment(invoice, dovada());
//            if (success) {
//                String fileName = Storage.get("fileName");
//                double value = Double.parseDouble(invoice.getValue());
//                appUtils.uploadFileAndAddRowInFacturiAndContForItem(facturi() + invoice.getFileName(), dovada() + fileName, invoice.getCategory(), invoice.getDescription(), value);
//            }
//        }
//    }

    @And("in NeoBT I convert to CSV, the file {string}")
    public void inNeoBTIConvertToCSVTheFile(String path) {
        neo.convertToCSV(path);
    }
}
