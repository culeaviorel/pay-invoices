package ro.anaf;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.table.Cell;
import com.sdl.selenium.web.table.Row;
import com.sdl.selenium.web.table.Table;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.FileUtility;
import org.openqa.selenium.support.ui.Select;
import ro.sheet.GoogleSheet;
import ro.sheet.RowRecord;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ANAF {
    private final Button autentificareEFactura = new Button().setId("cautare");

    public void login() {
        autentificareEFactura.click();
        WebLocator card2 = new WebLocator().setClasses("card2");
        WebLocator cardFront2 = new WebLocator(card2).setClasses("card-front2");
        cardFront2.mouseOver();
        WebLocator cardBack2 = new WebLocator(card2).setClasses("card-back2");
        WebLink raspunsuri = new WebLink(cardBack2, "Răspunsuri factură", SearchType.TRIM);
        Utils.sleep(500);
        raspunsuri.click();
        List<String> windows = WebDriverConfig.getDriver().getWindowHandles().stream().toList();
        WebDriverConfig.getDriver().switchTo().window(windows.get(1));
    }

    @SneakyThrows
    public void getAllInvoices(int days, List<RowRecord> list, String folderId, List<String> files) {
        WebLocator daysEl = new WebLocator().setId("form2:zile");
        Select selectDays = new Select(daysEl.getWebElement());
        selectDays.selectByValue(days + "");

        WebLocator cuiEl = new WebLocator().setId("form2:cui");
        Select selectCui = new Select(cuiEl.getWebElement());
        selectCui.selectByValue("26392200");

        InputButton facturi = new InputButton(null, "Obţine Răspunsuri");
        facturi.click();
        Utils.sleep(1000);
        Table table = new Table();
        int size = table.getCount();
        AppUtils appUtils = new AppUtils();
        Integer sheetId = appUtils.getFacturiSheetId("2025");
        for (int i = 1; i <= size; i++) {
            Row tableRow = table.getRow(i);
            String actualName = tableRow.getCell(6).getText();
            if (!files.contains(actualName)) {
                Cell cell = tableRow.getCell(5);
                WebLink downloadPDF = new WebLink(cell, "Descarca PDF");
                WebLocatorUtils.scrollToWebLocator(downloadPDF);
                downloadPDF.click();

                File file = FileUtility.getFileFromDownload();
                String pdfContent = FileUtility.getPDFContent(file);
                List<String> rows = pdfContent.lines().toList();
                String date = "";
                String value = "";
                for (int j = 0; j < rows.size(); j++) {
                    String row = rows.get(j);
                    if (row.contains("Data emitere")) {
                        date = row.split("Data emitere")[1].trim();
                        LocalDate datetime = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
                        date = datetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("ro", "RO")));
                    }
                    if (row.contains("Codul tipului")) {
                        value = rows.get(j + 1).split(" ")[2];
                        value = value.replaceAll("\\.", ",");
                    }
                    if (!date.isEmpty() && !value.isEmpty()) {
                        break;
                    }
                }
                String finalDate = date;
                String finalValue = value;
                Optional<RowRecord> first = list.stream().filter(f -> f.data().equals(finalDate) && f.value().equals(finalValue)).findFirst();
                int index;
                if (first.isPresent()) {
                    RowRecord findRow = first.get();
                    if (findRow.eFactura().isEmpty()) {
                        index = list.indexOf(findRow);
                        String link = appUtils.uploadFileInDrive(file.getAbsolutePath(), folderId);
                        List<Request> requests = new ArrayList<>();
                        GoogleSheet.addItemForUpdate("eFactura", link, ";", index, 7, sheetId, requests);
                        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                        BatchUpdateSpreadsheetResponse response = appUtils.getSheetsService().spreadsheets().batchUpdate(appUtils.getFacturiSheetId(), batchUpdateRequest).execute();
                        RowRecord rowRecord = new RowRecord(findRow.category(), findRow.method(), findRow.data(), findRow.value(), findRow.description(), findRow.link(), findRow.dovada(), "eFactura");
                        list.set(index, rowRecord);
                    }
                }
                Utils.sleep(1);
            }
        }

    }
}
