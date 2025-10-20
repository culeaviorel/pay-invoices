package ro.oblio;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.sdl.selenium.Go;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.table.Cell;
import com.sdl.selenium.web.table.Row;
import com.sdl.selenium.web.table.Table;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.FileUtility;
import ro.sheet.GoogleSheet;
import ro.sheet.RowRecord;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class Oblio {
    private final AppUtils appUtils = new AppUtils();

    public void login(String email, String pass) {
        Button acceptCookie = new Button().setId("CybotCookiebotDialogBodyButtonAccept");
        if (acceptCookie.isPresent()) {
            acceptCookie.click();
        }
        TextField emailEl = new TextField().setId("username");
        RetryUtils.retry(2, () -> emailEl.setValue(email));
        TextField passEl = new TextField().setId("password");
        passEl.setValue(pass);
        Button login = new Button(null, "Intra in cont", SearchType.DEEP_CHILD_NODE_OR_SELF);
        login.click();
    }

    @SneakyThrows
    public void getInvoices(List<RowRecord> list) {
        Integer sheetId = appUtils.getFacturiSheetId("2025");
        Table table = new Table().setId("content-table");
        int count = table.getCount();
        for (int i = 1; i <= count; i++) {
            Row rowEl = table.getRow(i);
            rowEl.scrollIntoView(Go.CENTER);
            Cell cell2 = rowEl.getCell(2);
            WebLocator cell2El = new WebLocator(cell2).setTag("p").setClasses("text-sm");
            String dataEFactura = cell2El.getText();
            Cell cell4 = rowEl.getCell(4);
            String sumaString = cell4.getText().split(" RON")[0].trim();
            double suma = Double.parseDouble(sumaString);
            Optional<RowRecord> first = list.stream().filter(f -> {
                LocalDate dateInvoice = LocalDate.parse(dataEFactura, DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ro", "RO")));
                LocalDate dateRow = LocalDate.parse(f.data(), DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("ro", "RO")));
                boolean dataEqual = dateInvoice.equals(dateRow);
                if (dataEqual) {
                    Utils.sleep(1);
                }
//                LocalDate dayMinus5 = now.minusDays(5);
//                LocalDate dayPlus1 = now.plusDays(1);
//                boolean between = isBetween(data, result.dataEmitere(), result.dataScadenta()) || isBetween(data, dayMinus5, dayPlus1);
                Double val = Double.parseDouble(f.value().replace(".", "").replace(",", "."));
//                log.info("val: {}, finalValue: {}, data: {}, dataEFactura: {}, dayMinus1: {}, dayPlus5: {}", val, finalValue, data, dataEFactura, dayMinus5, dayPlus1);
                boolean isValue = val.equals(suma) || val.equals(suma + 0.01);
                return (dataEqual /*|| between*/) && isValue && f.eFactura().isEmpty();
            }).findFirst();
            if (first.isPresent()) {
                Cell cell = rowEl.getCell(6);
                WebLink download = new WebLink(cell);
                download.click();
                WebLink export = new WebLink(rowEl, "Vizualizeaza Document");
                export.click();
                Utils.sleep(2000);
                File pdfFile = FileUtility.getFileFromDownload();
                String content = FileUtility.getPDFContent(pdfFile);
                List<String> rows = content.lines().toList();
                String link = "";
                for (String row : rows) {
                    if (row.contains("Index descarcare: ")) {
                        String index = row.split("Index descarcare: ")[1].trim();
                        String name = pdfFile.getName();
                        String parent = pdfFile.getAbsolutePath().split(name)[0];
                        File file = new File(parent + index + ".pdf");
                        pdfFile.renameTo(file);
                        link = appUtils.uploadFileInDrive(file.getAbsolutePath(), appUtils.getEFacturaFolderId());
                        break;
                    }
                }
                RowRecord findRow = first.get();
                if (findRow.eFactura().isEmpty()) {
                    int index = list.indexOf(findRow);
                    List<Request> requests = new ArrayList<>();
                    GoogleSheet.addItemForUpdate("eFactura", link, ";", index + 1, 7, sheetId, requests);
                    BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                    BatchUpdateSpreadsheetResponse response = appUtils.getSheetsService().spreadsheets().batchUpdate(appUtils.getFacturiSheetId(), batchUpdateRequest).execute();
                    RowRecord rowRecord = new RowRecord(findRow.category(), findRow.method(), findRow.data(), findRow.value(), findRow.description(), findRow.link(), findRow.dovada(), "eFactura");
                    try {
                        list.set(index, rowRecord);
                    } catch (UnsupportedOperationException e) {
                        list.add(rowRecord);
                    }
                }
            }
        }
        Utils.sleep(1);
    }
}
