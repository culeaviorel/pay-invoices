package ro.anaf;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.sdl.selenium.Go;
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
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        openAnaf(days);
        Table table = new Table();
        int size = table.getCount();
        AppUtils appUtils = new AppUtils();
        Integer sheetId = appUtils.getFacturiSheetId("2025");
        List<String> excludeFiles = List.of("5390303826");
        boolean next;
        do {
            for (int i = 1; i <= size; i++) {
                Row tableRow = table.getRow(i);
                String actualName = tableRow.getCell(6).getText();
                String dateRow = tableRow.getCell(1).getText();
                if (dateRow.contains(".2025") && !files.contains(actualName) && !excludeFiles.contains(actualName)) {
                    Cell cell = tableRow.getCell(5);
                    WebLink downloadPDF = new WebLink(cell, "Descarca PDF");
                    downloadPDF.scrollIntoView(Go.CENTER);
                    downloadPDF.click();

                    File file = FileUtility.getFileFromDownload(actualName);
                    if (file != null) {
                        Result result = getValuesFromPDF(file);
                        if (result.value != null) {
                            Double finalValue = result.value();
                            Optional<RowRecord> first = list.stream().filter(f -> {
                                String data = f.data();
                                String dataEFactura = result.date();
                                boolean dataEqual = data.equals(dataEFactura);
                                LocalDate now = LocalDate.parse(dataEFactura, DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("ro", "RO")));
                                LocalDate dayMinus5 = now.minusDays(5);
                                LocalDate dayPlus1 = now.plusDays(1);
                                boolean between = isBetween(data, result.dataEmitere(), result.dataScadenta()) || isBetween(data, dayMinus5, dayPlus1);
                                Double val = Double.parseDouble(f.value().replace(".", "").replace(",", "."));
//                                log.info("val: {}, finalValue: {}, data: {}, dataEFactura: {}, dayMinus1: {}, dayPlus5: {}", val, finalValue, data, dataEFactura, dayMinus5, dayPlus1);
                                boolean isValue = val.equals(finalValue) || val.equals(finalValue + 0.01);
                                return (dataEqual || between) && isValue;
                            }).findFirst();
                            if (first.isPresent()) {
                                RowRecord findRow = first.get();
                                if (findRow.eFactura().isEmpty()) {
                                    int index = list.indexOf(findRow);
                                    String link = appUtils.uploadFileInDrive(file.getAbsolutePath(), folderId);
                                    List<Request> requests = new ArrayList<>();
                                    GoogleSheet.addItemForUpdate("eFactura", link, ";", index + 1, 7, sheetId, requests);
                                    BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                                    BatchUpdateSpreadsheetResponse response = appUtils.getSheetsService().spreadsheets().batchUpdate(appUtils.getFacturiSheetId(), batchUpdateRequest).execute();
                                    RowRecord rowRecord = new RowRecord(findRow.category(), findRow.method(), findRow.data(), findRow.value(), findRow.description(), findRow.link(), findRow.dovada(), "eFactura");
                                    list.set(index, rowRecord);
                                }
                            } else {
                                log.info("Nu am gasit un rand cu ce sa asociez: {}", result.content);
                            }
                        } else {
                            log.info("Nu se mai descarca fisierul cu numele, trebuie pus in lista de exludes: {}", actualName);
                        }
                    } else {
                        log.info("Nu am citit corect: {}", actualName);
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

    private static void openAnaf(int days) {
        WebLocator daysEl = new WebLocator().setId("form2:zile");
        Select selectDays = new Select(daysEl.getWebElement());
        selectDays.selectByValue(days + "");

        WebLocator cuiEl = new WebLocator().setId("form2:cui");
        Select selectCui = new Select(cuiEl.getWebElement());
        selectCui.selectByValue("26392200");

        InputButton facturi = new InputButton(null, "Obţine Răspunsuri");
        facturi.click();
        Utils.sleep(1000);
    }

    private static Result getValuesFromPDF(File file) throws IOException {
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
                try {
                    dataEmitere = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
                    date = dataEmitere.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("ro", "RO")));
                } catch (DateTimeParseException e) {
                    break;
                }
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
                if (value == null) {
                    Utils.sleep(1);
                }
            }
            if (!date.isEmpty() && value != null && dataScadenta != null) {
                break;
            }
        }
        if (value == null) {
            Utils.sleep(1);
        }
        Result result = new Result(dataEmitere, dataScadenta, date, value, pdfContent);
        return result;
    }

    private record Result(LocalDate dataEmitere, LocalDate dataScadenta, String date, Double value, String content) {
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
