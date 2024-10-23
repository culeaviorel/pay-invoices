package ro.btgo;

import com.google.api.services.sheets.v4.Sheets;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.neo.Invoice;
import ro.neo.MemberPay;
import ro.neo.Pay;
import ro.neo.Storage;
import ro.sheet.GoogleSheet;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
public class BTGoSteps extends TestBase {

    private static final String contracteDeSponsorizareId = "13SphvJvXIInYDd1pzYc-gIa0pI1HxEWa5JAUgAqhXfI";
    private static final String facturiSheetId = "1SL4EGDDC3qf1X80s32OOEMxmVbvlL7WRbh5Kr88hPy0";
    private static final String membriCuCopiiiLaGradinitaId = "1uxtyl_NBBHTWnmN7FVF_N5uE43iiqHHG1tDKC4-7ANg";
    private final BTGo btGo = new BTGo();
    private final AppUtils appUtils = new AppUtils();
    private static List<Pay> pays;
    private static List<MemberPay> memberPays;
    private static Sheets sheetsService;

    @And("I login in BTGo")
    public void iLoginInBTGo() {
        UserCredentials credentials = new UserCredentials();
        btGo.login(credentials.getBTGoID(), credentials.getBTGoPassword());
    }

    @SneakyThrows
    @And("in BTGo I pay invoices:")
    public void inBTGoIPayInvoices(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            if (invoice.getFileName().contains(".pdf")) {
                PDDocument document = PDDocument.load(new java.io.File(facturi() + invoice.getFileName()));
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                document.close();
                List<String> list = text.lines().toList();
                switch (invoice.getCategory()) {
                    case "Apa" -> appUtils.collectForApa(invoice, list);
                    case "Gunoi" -> appUtils.collectForGunoi(invoice, list);
                    case "Curent" -> appUtils.collectForCurent(invoice, list);
                }
            }
            double doubleValue = Double.parseDouble(invoice.getValue());
            int intValue = (int) doubleValue + 1;
            btGo.transferFromDepozitIntoContCurent(intValue);
//            invoice.setFurnizor("SUPERCOM SA");
//            invoice.setIban("RO85CECEB00030RON2670130");
            boolean success = btGo.invoicePayment(invoice, dovada());
            if (success) {
                String fileName = Storage.get("fileName");
                double value = Double.parseDouble(invoice.getValue());
                appUtils.uploadFileAndAddRowInFacturiAndContForItem(facturi() + invoice.getFileName(), dovada() + fileName, invoice.getCategory(), invoice.getDescription(), value);
            }
        }
    }

    @And("in BTGo I save report from current month")
    public void inBTGoISaveReportFromCurrentMonth() {
        final Locale roLocale = new Locale("ro", "RO");
        String month = StringUtils.capitalize(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, roLocale));
        String fileId = GoogleSheet.createFile(month);
    }
}
