package org.fasttrackit.util;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.neo.Invoice;
import ro.neo.Storage;
import ro.sheet.GoogleSheet;
import ro.sheet.ItemTO;
import ro.sheet.RowTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AppUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppUtils.class);

    private static Sheets sheetsService;
    private static final String facturiSheetId = "1SL4EGDDC3qf1X80s32OOEMxmVbvlL7WRbh5Kr88hPy0";

    public static void openUrl(String url) {
        LOGGER.info("open {}", url);
        WebDriverConfig.getDriver().get(url);
    }

    @SneakyThrows
    public void uploadFileAndAddRowForItem(String fileName, String category, String description, double value, String dovada) {
        String link = uploadFileInDrive(dovada, fileName, "1mh2XGLQxiqIyAlkhjMLjlCGkryG1VfS_");
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
    public void uploadFileAndAddRowForItem(ItemTO item, String location) {
        String dataValue = item.getData();
        LocalDate localDate = LocalDate.parse(dataValue, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        int year = localDate.getYear();
        String link = uploadFileInDrive(location, item.getFileName(), "1mh2XGLQxiqIyAlkhjMLjlCGkryG1VfS_");
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(facturiSheetId, year + "!A1:G").execute();
        List<List<Object>> values = valueRange.getValues();
        List<RowTO> list = values.stream().map(i -> new RowTO((String) i.get(0), (String) i.get(1), (String) i.get(2), (String) i.get(3), (String) i.get(4), (String) i.get(5))).toList();
        Optional<RowTO> firstRow = list.stream().filter(i -> {
            try {
                LocalDate date = LocalDate.parse(i.getData(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                boolean before = localDate.isBefore(date);
                if (before) {
                    Utils.sleep(1);
                }
                return before;
            } catch (DateTimeParseException e) {
                return false;
            }
        }).findFirst();
        int id = list.size();
        if (firstRow.isPresent()) {
            id = list.indexOf(firstRow.get());
        }
        List<Request> insertRequests = new ArrayList<>();
        Integer sheetId = getSheetId(facturiSheetId, year + "");
        GoogleSheet.insertItem(id, sheetId, insertRequests);
        BatchUpdateSpreadsheetRequest batchInsertRequest = new BatchUpdateSpreadsheetRequest().setRequests(insertRequests);
        BatchUpdateSpreadsheetResponse insertResponse = sheetsService.spreadsheets().batchUpdate(facturiSheetId, batchInsertRequest).execute();

        List<Request> requests = new ArrayList<>();
        GoogleSheet.addItemForUpdate(item.getCategory(), id, 0, sheetId, requests);
        GoogleSheet.addItemForUpdate("Cont", id, 1, sheetId, requests);
        String date = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        GoogleSheet.addItemForUpdateDate(date, id, 2, sheetId, requests);
        String tmp = item.getValue();
        double value = Double.parseDouble(tmp);
        GoogleSheet.addItemForUpdate(value, id, 3, sheetId, requests);
        GoogleSheet.addItemForUpdate(item.getDescription(), id, 4, sheetId, requests);
        GoogleSheet.addItemForUpdate(item.getType(), link, ";", id, 5, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(facturiSheetId, batchUpdateRequest).execute();
        Utils.sleep(1);
    }

    @SneakyThrows
    public String uploadFileInDrive(String location, String fileName, String driveFolderId) {
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

    public Integer getSheetId(String spreadsheetId, String number) {
        Integer sheetId = Storage.get(spreadsheetId + "sheetId");
        if (sheetId == null) {
            SheetProperties sheet = GoogleSheet.getSheet(spreadsheetId, number);
            sheetId = sheet.getSheetId();
            Storage.set(spreadsheetId + "sheetId", sheetId);
        }
        return sheetId;
    }

    public void collectForCurent(Invoice invoice, List<String> list) {
        String total = "";
        String nrFacturii = "";
        String codAbonat = "";
        for (String row : list) {
            if (row.contains("Total de plată")) {
                total = row.split("Total de plată")[1].trim().split("lei")[0].trim();
            } else if (row.contains("ID factură:")) {
                nrFacturii = row.split("ID factură:")[1].trim();
            } else if (row.contains("Cod încasare:")) {
                codAbonat = row.split("Cod încasare:")[1].trim();
            }
            if (!total.isEmpty() && !nrFacturii.isEmpty() && !codAbonat.isEmpty()) {
                invoice.setValue(total.replaceAll(",", "."));
                invoice.setNr(nrFacturii.replaceAll("\\s+", ""));
                invoice.setCod(codAbonat);
                invoice.setDescription("factura de Curent");
                break;
            }
        }
    }

    public void collectForGunoi(Invoice invoice, List<String> list) {
        String total = "";
        String nrFacturii = "";
        String codAbonat = "";
        for (int i = 0; i < list.size(); i++) {
            String row = list.get(i);
            if (row.contains("Total factura")) {
                total = list.get(i + 4).trim();
            } else if (row.contains("Numar:")) {
                nrFacturii = row.split("Numar:")[1].trim();
            } else if (row.contains("Cod client:")) {
                codAbonat = row.split("Cod client:")[1].trim();
            }
            if (!total.isEmpty() && !nrFacturii.isEmpty() && !codAbonat.isEmpty()) {
                invoice.setValue(total);
                invoice.setNr(nrFacturii.replaceAll("\\s+", ""));
                invoice.setCod(codAbonat);
                invoice.setDescription("factura de Gunoi");
                break;
            }
        }
    }

    public void collectForApa(Invoice invoice, List<String> list) {
        String total = "";
        String nrFacturii = "";
        String codAbonat = "";
        for (String row : list) {
            if (row.contains("Total factura curenta:")) {
                total = row.split("Total factura curenta:")[1].trim();
            } else if (row.contains("Numar:")) {
                nrFacturii = row.split("Numar:")[1].trim();
            } else if (row.contains("Cod abonat:")) {
                codAbonat = row.split("Cod abonat:")[1].trim();
            }
            if (!total.isEmpty() && !nrFacturii.isEmpty() && !codAbonat.isEmpty()) {
                invoice.setValue(total);
                invoice.setNr(nrFacturii.replaceAll("\\s+", ""));
                invoice.setCod(codAbonat);
                invoice.setDescription("factura de Apa");
                break;
            }
        }
    }

}
