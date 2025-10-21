package ro.neo;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.sheet.GoogleSheet;

import java.util.List;

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

    @And("in NeoBT I save report from {string} month")
    public void inNeoBTISaveReportFromMonth(String month) {
        String location = location2025() + "CSV\\";
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
