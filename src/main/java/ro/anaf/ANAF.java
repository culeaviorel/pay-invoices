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
import lombok.extern.slf4j.Slf4j;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
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
    public void getAllInvoices(int days, List<RowRecord> rowsList, String folderId, List<String> files) {
        List<RowRecord> list = new ArrayList<>(rowsList);
        list.remove(0);
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
        List<String> excludeFiles = List.of("4337628200", "4378615385");
        boolean next;
        do {
            for (int i = 1; i <= size; i++) {
                Row tableRow = table.getRow(i);
                String actualName = tableRow.getCell(6).getText();
                String dateRow = tableRow.getCell(1).getText();
                if (dateRow.contains(".2025") && !files.contains(actualName) && !excludeFiles.contains(actualName)) {
                    Cell cell = tableRow.getCell(5);
                    WebLink downloadPDF = new WebLink(cell, "Descarca PDF");
                    WebLocatorUtils.scrollToWebLocator(downloadPDF);
                    downloadPDF.click();

                    File file = FileUtility.getFileFromDownload(actualName);
                    if (file != null) {
                        String pdfContent = FileUtility.getPDFContent(file);
                        List<String> rows = pdfContent.lines().toList();
                        LocalDate dataEmitere = null;
                        LocalDate dataScadenta = null;
                        String date = "";
                        Double value = null;
                        for (int j = 0; j < rows.size(); j++) {
                            String row = rows.get(j);
                            if (row.contains("Data emitere")) {
                                date = row.split("Data emitere")[1].trim();
                                dataEmitere = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
                                date = dataEmitere.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("ro", "RO")));
                            } else if (row.contains("Data scadenta")) {
                                try {
                                    String dateTMP = row.split("Data scadenta")[1].trim();
                                    dataScadenta = LocalDate.parse(dateTMP, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    Utils.sleep(1);
                                }
                            } else if (row.endsWith("Codul tipului")) {
                                String rowValue = rows.get(j + 1);
                                String valueString = rowValue.split(" ")[2];
                                value = Double.parseDouble(valueString);
                            }
                            if (!date.isEmpty() && value != null && dataScadenta != null) {
                                break;
                            }
                        }
                        Double expected = Double.parseDouble("81.00");
                        if (value.equals(expected)) {
                            Utils.sleep(1);
                        }
                        String finalDate = date;
                        Double finalValue = value;
                        LocalDate finalDataEmitere = dataEmitere;
                        LocalDate finalDataScadenta = dataScadenta;
                        Optional<RowRecord> first = list.stream().filter(f -> {
                            boolean data1 = f.data().equals(finalDate);
                            boolean between = isBetween(f.data(), finalDataEmitere, finalDataScadenta);
                            Double tmpDouble = Double.parseDouble(f.value().replace(".", "").replace(",", "."));
                            if (tmpDouble.equals(Double.parseDouble("1075.17"))) {
                                Utils.sleep(1);
                            }
                            boolean isValue = tmpDouble.equals(finalValue) || tmpDouble.equals(finalValue + 0.01);
                            return (data1 || between) && isValue;
                        }).findFirst();
                        int index;
                        if (first.isPresent()) {
                            RowRecord findRow = first.get();
                            if (findRow.eFactura().isEmpty()) {
                                index = list.indexOf(findRow);
                                String link = appUtils.uploadFileInDrive(file.getAbsolutePath(), folderId);
                                List<Request> requests = new ArrayList<>();
                                GoogleSheet.addItemForUpdate("eFactura", link, ";", index + 1, 7, sheetId, requests);
                                BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                                BatchUpdateSpreadsheetResponse response = appUtils.getSheetsService().spreadsheets().batchUpdate(appUtils.getFacturiSheetId(), batchUpdateRequest).execute();
                                RowRecord rowRecord = new RowRecord(findRow.category(), findRow.method(), findRow.data(), findRow.value(), findRow.description(), findRow.link(), findRow.dovada(), "eFactura");
                                list.set(index, rowRecord);
                            }
                        } else {
                            log.info("Nu am gasit un rand cu ce sa asociez:");
                        }
                    } else {
                        log.info("Nu se mai descarca fisierul cu numele, trebuie pus in lista de exludes: {}", actualName);
                    }
                }
            }
            WebLocator state = new WebLocator().setClasses("ui-paginator-current");
            String text = state.getText();
            next = hasNextPage(text);
            WebLocator nextPage = new WebLocator().setClasses("ui-paginator-next", "ui-state-default", "ui-corner-all");
            nextPage.click();
            Utils.sleep(800);
            size = table.getCount();
        } while (next);
    }

    public static boolean hasNextPage(String input) {
        String regex = "\\((\\d+) of (\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            int first = Integer.parseInt(matcher.group(1));
            int second = Integer.parseInt(matcher.group(2));
            return first != second;
        } else {
            log.info("String-ul nu este în formatul corect: (numar of numar)");
            return false;
        }
    }

    private boolean isBetween(String date, LocalDate start, LocalDate end) {
//        log.info("-----------------------------------------");
        if (end == null) {
            return false;
        }
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("ro", "RO")));
//        log.info("date: {}", localDate.toString());
//        log.info("start: {}", start.toString());
//        log.info("end: {}", end.toString());
        return (localDate.isEqual(start) || localDate.isEqual(end)) || localDate.isAfter(start) && localDate.isBefore(end);
    }
}
