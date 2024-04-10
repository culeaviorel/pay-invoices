package ro.neo;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.base.Strings;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.sheet.GoogleSheet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class NeoSteps extends TestBase {

    private static final String contracteDeSponsorizareId = "13SphvJvXIInYDd1pzYc-gIa0pI1HxEWa5JAUgAqhXfI";
    private static final String facturiSheetId = "1SL4EGDDC3qf1X80s32OOEMxmVbvlL7WRbh5Kr88hPy0";
    private static final String membriCuCopiiiLaGradinitaId = "1uxtyl_NBBHTWnmN7FVF_N5uE43iiqHHG1tDKC4-7ANg";
    private final Neo neo = new Neo();
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
        ValueRange valueRange = sheetsService.spreadsheets().values().get(contracteDeSponsorizareId, "Donatii cu destinatie speciala" + "!B1:H").execute();
        List<List<Object>> values = valueRange.getValues();
        pays = values.stream().map(i -> new Pay(
                i.get(0).toString(),
                i.get(1).toString(),
                i.get(2).toString(),
                i.get(3).toString(),
                i.get(4).toString(),
                i.get(5).toString(),
                i.get(6).toString()
        )).toList();
    }

    @SneakyThrows
    @And("I prepare data for Sustinere educatie from google sheet")
    public void iPrepareDataForSustinereEducatieFromGoogleSheet() {
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(membriCuCopiiiLaGradinitaId, "2024" + "!A2:D").execute();
        List<List<Object>> values = valueRange.getValues();
        memberPays = values.stream().map(i -> new MemberPay(
                i.get(0).toString(),
                i.get(1).toString(),
                i.get(2).toString(),
                i.size() == 4 ? i.get(3).toString() : ""
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
                item.setDescription("donatie de la ");
                boolean successPayment = neo.makePayment(item, dovada());
                if (successPayment) {
                    changeMonthInSheet(item.getName());
                    String fileName = Storage.get("fileName");
                    double value = Double.parseDouble(item.getSum());
                    String category = item.getName().replaceAll(" ", "") + "Out";
                    uploadFileAndAddRowForItem(fileName, category, "plata", value);
                }
            }
        }
    }

    @SneakyThrows
    private void changeMonthInSheet(String name) {
        List<String> list = pays.get(0).toList();
        int columnIndex = list.indexOf(name) + 3;
        Integer sheetId = getSheetId(contracteDeSponsorizareId, "Donatii cu destinatie speciala");
        List<Request> requests = new ArrayList<>();
        String month = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        GoogleSheet.addItemForUpdate(month, 10, columnIndex, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(contracteDeSponsorizareId, batchUpdateRequest).execute();
    }

    @SneakyThrows
    private void changeStatusInSheet(MemberPay memberPay) {
        int row = memberPays.indexOf(memberPay) + 1;
        Integer sheetId = getSheetId(membriCuCopiiiLaGradinitaId, "Membri cu copiii la gradinita");
        List<Request> requests = new ArrayList<>();
        GoogleSheet.addItemForUpdate("Trimis", row, 3, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(membriCuCopiiiLaGradinitaId, batchUpdateRequest).execute();
    }

    private Payment preparePayment(PayCount payCount) {
        List<String> people = pays.stream().map(Pay::name).filter(Objects::nonNull).skip(1).limit(6).toList();
        Item somethingNewItem = new Item();
        Item teenChellangeItem = new Item();
        Item casaFilipItem = new Item();
        Item tanzaniaItem = new Item();
        Item caminulFelixItem = new Item();
        for (String person : people) {
            Optional<Pay> pay = pays.stream().filter(i -> i.name().equals(person)).findFirst();
            if (pay.isPresent()) {
                Pay payPerson = pay.get();
                if (!payPerson.somethingNew().equals("0") && payCount.somethingNew() > 0) {
                    String separator = Strings.isNullOrEmpty(somethingNewItem.getName()) ? "" : ", ";
                    somethingNewItem.setName("Something New");
                    String description = somethingNewItem.getDescription() + separator + payPerson.description();
                    somethingNewItem.setDescription(description);
                }
                if (!payPerson.teenChallenge().equals("0") && payCount.teenChellange() > 0) {
                    String separator = Strings.isNullOrEmpty(teenChellangeItem.getName()) ? "" : ", ";
                    teenChellangeItem.setName("Teen Challenge");
                    String description = teenChellangeItem.getDescription() + separator + payPerson.description();
                    teenChellangeItem.setDescription(description);
                }
                if (!payPerson.casaFilip().equals("0") && payCount.casaFilip() > 0) {
                    String separator = Strings.isNullOrEmpty(casaFilipItem.getName()) ? "" : ", ";
                    casaFilipItem.setName("Casa Filip");
                    String description = casaFilipItem.getDescription() + separator + payPerson.description();
                    casaFilipItem.setDescription(description);
                }
                if (!payPerson.tanzania().equals("0") && payCount.tanzania() > 0) {
                    String separator = Strings.isNullOrEmpty(tanzaniaItem.getName()) ? "" : ", ";
                    tanzaniaItem.setName("Tanzania");
                    String description = tanzaniaItem.getDescription() + separator + payPerson.description();
                    tanzaniaItem.setDescription(description);
                }
                if (!payPerson.caminulFelix().equals("0") && payCount.caminulFelix() > 0) {
                    String separator = Strings.isNullOrEmpty(caminulFelixItem.getName()) ? "" : ", ";
                    caminulFelixItem.setName("Caminul Felix");
                    String description = caminulFelixItem.getDescription() + separator + payPerson.description();
                    caminulFelixItem.setDescription(description);
                }
            }
        }
        List<Item> items = new ArrayList<>();
        if (payCount.somethingNew() > 0) {
            somethingNewItem.setSum(payCount.somethingNew() + "");
            items.add(somethingNewItem);
        }
        if (payCount.teenChellange() > 0) {
            teenChellangeItem.setSum(payCount.teenChellange() + "");
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
        return new Payment(items);
    }

    private static PayCount getPayCount(PayCount countMonths, Pay totalOnMonthPay) {
        int somethingNew = countMonths.somethingNew() * Integer.parseInt(totalOnMonthPay.somethingNew());
        int teenChallenge = countMonths.teenChellange() * Integer.parseInt(totalOnMonthPay.teenChallenge());
        int casaFilip = countMonths.casaFilip() * Integer.parseInt(totalOnMonthPay.casaFilip());
        int tanzania = countMonths.tanzania() * Integer.parseInt(totalOnMonthPay.tanzania());
        int caminulFelix = countMonths.caminulFelix() * Integer.parseInt(totalOnMonthPay.caminulFelix());
        return new PayCount(somethingNew, teenChallenge, casaFilip, tanzania, caminulFelix);
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
            if (somethingNewCount != -1 && teenChellangeCount != -1 && casaFilipCount != -1 && tanzaniaCount != -1 && caminulFelixCount != -1) {
                break;
            }
        }
        return new PayCount(somethingNewCount, teenChellangeCount, casaFilipCount, tanzaniaCount, caminulFelixCount);
    }

    @SneakyThrows
    @And("in NeoBT I upload file in google sheet")
    public void inNeoBTIUploadFileInGoogleSheet() {
        uploadFileAndAddRowForItem("DovadaPlataCasaFilipAprilie.pdf", "CasaFilipOut", "plata", 200.00);
    }

    @SneakyThrows
    private void uploadFileAndAddRowForItem(String fileName, String category, String description, double value) {
        String link = uploadFileInDrive(dovada(), fileName, "1mh2XGLQxiqIyAlkhjMLjlCGkryG1VfS_");
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(facturiSheetId, "2024" + "!A1:G").execute();
        List<List<Object>> values = valueRange.getValues();
        int id = values.size();
        List<Request> requests = new ArrayList<>();
        Integer sheetId = getSheetId(facturiSheetId, "2024");
        GoogleSheet.addItemForUpdate(category, id, 0, sheetId, requests);
        GoogleSheet.addItemForUpdate("Cont", id, 1, sheetId, requests);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        GoogleSheet.addItemForUpdateDate(date, id, 2, sheetId, requests);
        GoogleSheet.addItemForUpdate(value, id, 3, sheetId, requests);
        GoogleSheet.addItemForUpdate(description, id, 4, sheetId, requests);
        GoogleSheet.addItemForUpdate("Dovada", link, ";", id, 5, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(facturiSheetId, batchUpdateRequest).execute();
        Utils.sleep(1);
    }

    @SneakyThrows
    private String uploadFileInDrive(String location, String fileName, String driveFolderId) {
        java.io.File filePath = new java.io.File(location + fileName);
        String name = filePath.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);
        String type = switch (extension) {
            case "pdf" -> "application/pdf";
            case "csv" -> "text/csv";
            default -> null;
        };
        Drive driveService = GoogleSheet.getDriveService();
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(Collections.singletonList(driveFolderId));
        FileContent mediaContent = new FileContent(type, filePath);
        File file = driveService.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
        return driveService.files().get(file.getId()).setFields("webViewLink").execute().getWebViewLink();
    }

    private static String dovada() {
        return location() + "Facturi\\Dovada\\";
    }

    private static String location() {
        return "C:\\Users\\vculea\\Desktop\\Biserica\\2024\\";
    }

    private Integer getSheetId(String spreadsheetId, String number) {
        Integer sheetId = Storage.get(spreadsheetId + "sheetId");
        if (sheetId == null) {
            SheetProperties sheet = GoogleSheet.getSheet(spreadsheetId, number);
            sheetId = sheet.getSheetId();
            Storage.set(spreadsheetId + "sheetId", sheetId);
        }
        return sheetId;
    }

    @And("in NeoBT I send Sustinere educatie from google sheet")
    public void inNeoBTISendSustinereEducatieFromGoogleSheet() {
        List<MemberPay> memberPayList = memberPays.stream().filter(i -> Strings.isNullOrEmpty(i.status())).toList();
        int total = memberPayList.stream().flatMapToInt(i -> IntStream.of(Integer.parseInt(i.sum()))).sum();
        neo.transferFromDepozitIntoContCurent(total);
        for (MemberPay memberPay : memberPayList) {
            boolean success = neo.makePayment(new Item(memberPay.name(), memberPay.sum(), memberPay.description()), dovada());
            if (success) {
                changeStatusInSheet(memberPay);
                String fileName = Storage.get("fileName");
                double value = Double.parseDouble(memberPay.sum());
                uploadFileAndAddRowForItem(fileName, "Sustinere Educatie", "pentru " + memberPay.name(), value);
            }
        }
    }

    @And("in NeoBT I save report from {string} month")
    public void inNeoBTISaveReportFromMonth(String month) {
        String location = location() + "CSV\\";
        neo.saveReportFrom("|Cont curent|RON", month, location);
        String fileName = Storage.get("fileName");
        uploadFileInDrive(location, fileName, "1Uc2IebVqTxFSYJSDcnBXdjHCw9ioHDmR");
        neo.goToDashboard();
        neo.saveReportFrom("|Cont de economii|RON", month, location);
        fileName = Storage.get("fileName").toString().replaceAll("xls", "csv");
        Utils.sleep(1); // Convert manually xls file to csv
        uploadFileInDrive(location, fileName, "1Uc2IebVqTxFSYJSDcnBXdjHCw9ioHDmR");
    }
}
