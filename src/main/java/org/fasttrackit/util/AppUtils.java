package org.fasttrackit.util;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.base.Strings;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AppUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppUtils.class);

    private static Sheets sheetsService;
    private static final String facturiSheetId = "1SL4EGDDC3qf1X80s32OOEMxmVbvlL7WRbh5Kr88hPy0";
    private static final String contSheetId = "1UMkkX0VPDrRzu8RlLq2wILIx1VEJ5Sv2nQQDEBjEC8o";
    private final String facturiFolderId = "1g6ySt6dEBEE7YgBpvC9E5VoPGejoq3Fp";// 2025/facturi
    private final String dovadaFolderId = "1K6eKD5GJwUGz9dlOecAOi1OWLkQwKODT";// 2025/dovada

    public static void openUrl(String url) {
        LOGGER.info("open {}", url);
        WebDriverConfig.getDriver().get(url);
    }

    @SneakyThrows
    public void uploadFileAndAddRowInFacturiAndContForItem(String facturaFilePath, String dovadaFilePath, String category, String description, double value) {
        boolean hasFactura = !Strings.isNullOrEmpty(facturaFilePath);
        String facturaLink = "";
        if (hasFactura) {
            facturaLink = uploadFileInDrive(facturaFilePath, facturiFolderId);
        }
        String dovadaLink = uploadFileInDrive(dovadaFilePath, dovadaFolderId);
        List<List<Object>> values = getValues(facturiSheetId, "2025!A1:G");
        int id = values.size();
        List<Request> requests = new ArrayList<>();
        Integer sheetId = getSheetId(facturiSheetId, "2025");
        GoogleSheet.addItemForUpdate(category, id, 0, sheetId, requests);
        GoogleSheet.addItemForUpdate("Cont", id, 1, sheetId, requests);
        LocalDate now = LocalDate.now();
        now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        GoogleSheet.addItemForUpdateDate(now, id, 2, sheetId, requests);
        GoogleSheet.addItemForUpdate(value, id, 3, sheetId, requests);
        GoogleSheet.addItemForUpdate(description, id, 4, sheetId, requests);
        int columnIndex = 5;
        if (hasFactura) {
            GoogleSheet.addItemForUpdate("Factura", facturaLink, ";", id, 5, sheetId, requests);
            columnIndex++;
        }
        GoogleSheet.addItemForUpdate("Dovada", dovadaLink, ";", id, columnIndex, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(facturiSheetId, batchUpdateRequest).execute();
        Utils.sleep(1);
    }

    @SneakyThrows
    public List<List<Object>> getValues(String sheetId, String range) {
        sheetsService = GoogleSheet.getSheetsService();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(sheetId, range).execute();
        return valueRange.getValues();
    }

    @SneakyThrows
    public void uploadFileAndAddRowInFacturiAndContForItem(ItemTO item, String location) {
        String dataValue = item.getData();
        LocalDate localDate = LocalDate.parse(dataValue, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        int year = localDate.getYear();
        String month = StringUtils.capitalize(localDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("ro", "RO")));
        String link = uploadFileInDrive(location + item.getFileName(), facturiFolderId);
        List<List<Object>> values = getValues(facturiSheetId, year + "!A1:G");
        List<RowTO> list = values.stream().map(i -> new RowTO((String) i.get(0), (String) i.get(1), (String) i.get(2), (String) i.get(3), (String) i.get(4), (String) i.get(5))).toList();
        Optional<RowTO> firstRow = list.stream().filter(i -> isBefore(i, localDate)).reduce((first, second) -> first);
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
        GoogleSheet.addItemForUpdate(item.getPlata(), id, 1, sheetId, requests);
        GoogleSheet.addItemForUpdateDate(localDate, id, 2, sheetId, requests);
        String tmp = item.getValue();
        double value = Double.parseDouble(tmp);
        GoogleSheet.addItemForUpdate(value, id, 3, sheetId, requests);
        GoogleSheet.addItemForUpdate(item.getDescription(), id, 4, sheetId, requests);
        GoogleSheet.addItemForUpdate(item.getType(), link, ";", id, 5, sheetId, requests);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(facturiSheetId, batchUpdateRequest).execute();
        Utils.sleep(1);
        if (item.getPlata().equals("Cash")) {
            ValueRange valueRange1 = sheetsService.spreadsheets().values().get(contSheetId, month + "!A1:I").execute();
            List<List<Object>> values1 = valueRange1.getValues();
            values1 = values1.stream().filter(i -> i.size() > 7).toList();
            List<RowTO> list1 = values1.stream().map(i -> new RowTO((String) i.get(0), (String) i.get(1), (String) i.get(2), (String) i.get(4), (String) i.get(6), (String) i.get(7))).toList();
            Optional<RowTO> firstRow1 = list1.stream().filter(i -> isBefore(i, localDate)).reduce((first, second) -> first);
            int id1 = list1.size();
            if (firstRow1.isPresent()) {
                id1 = list1.indexOf(firstRow1.get());
            }
            List<Request> insertRequests1 = new ArrayList<>();
            Integer sheetId1 = getSheetId(contSheetId, month);
            GoogleSheet.insertItem(id1, sheetId1, insertRequests1);
            GoogleSheet.addItemForUpdateFormula("F" + (id1 + 1) + "+D" + (id1 + 2) + "-E" + (id1 + 2), id1 + 1, 5, sheetId1, insertRequests1);
            BatchUpdateSpreadsheetRequest batchInsertRequest1 = new BatchUpdateSpreadsheetRequest().setRequests(insertRequests1);
            BatchUpdateSpreadsheetResponse insertResponse1 = sheetsService.spreadsheets().batchUpdate(contSheetId, batchInsertRequest1).execute();

            List<Request> requests1 = new ArrayList<>();
            GoogleSheet.addItemForUpdate("Cheltuieli", id1, 0, sheetId1, requests1);
            GoogleSheet.addItemForUpdate(item.getCategory(), id1, 1, sheetId1, requests1);
            GoogleSheet.addItemForUpdateDate(localDate, id1, 2, sheetId1, requests1);
            GoogleSheet.addItemForUpdate(value, id1, 4, sheetId1, requests1);
            GoogleSheet.addItemForUpdateFormula("F" + id1 + "+D" + (id1 + 1) + "-E" + (id1 + 1), id1, 5, sheetId1, requests1);
            GoogleSheet.addItemForUpdate(item.getDescription(), id1, 6, sheetId1, requests1);
            GoogleSheet.addItemForUpdate(item.getType(), link, ";", id1, 7, sheetId1, requests1);
            BatchUpdateSpreadsheetRequest batchUpdateRequest1 = new BatchUpdateSpreadsheetRequest().setRequests(requests1);
            BatchUpdateSpreadsheetResponse response1 = sheetsService.spreadsheets().batchUpdate(contSheetId, batchUpdateRequest1).execute();
            Utils.sleep(1);
        }
    }

    private static boolean isBefore(RowTO i, LocalDate localDate) {
        try {
            String data = i.getData();
            String format = detectDateFormat(data);
            LocalDate date1 = LocalDate.parse(data, DateTimeFormatter.ofPattern(format));
            boolean before = localDate.isBefore(date1);
            return before;
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String detectDateFormat(String dateStr) {
        List<String> possibleFormats = List.of(
                "dd.MM.yyyy",   // Zi, lună, an
                "MM.dd.yyyy",   // Lună, zi, an (ex. SUA)
                "dd/MM/yyyy",
                "yyyy.MM.dd"    // An, lună, zi
        );

        return possibleFormats.stream()
                .filter(format -> {
                    try {
                        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
                        return true;
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse("Unknown format for: " + dateStr);
    }

    @SneakyThrows
    public String uploadFileInDrive(String filePath, String driveFolderId) {
        java.io.File file = new java.io.File(filePath);
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);
        String type = switch (extension) {
            case "pdf" -> "application/pdf";
            case "csv" -> "text/csv";
            case "jpeg" -> "image/jpeg";
            default -> null;
        };
        Drive driveService = GoogleSheet.getDriveService();
        File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setParents(List.of(driveFolderId));
        FileContent mediaContent = new FileContent(type, file);
        File uploadFile = driveService.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
        return driveService.files().get(uploadFile.getId()).setFields("webViewLink").execute().getWebViewLink();
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

    public void collectForGas(Invoice invoice, List<String> list) {
        String total = "";
        String nrFacturii = "";
        String codAbonat = "";
        for (String row : list) {
            if (row.contains("Total de plată la data de")) {
                total = splitAfterLastSpace(row)[1];
            } else if (row.contains("MS EON")) {
                nrFacturii = row.split("MS EON")[1].trim().split(" ")[0].trim();
            } else if (row.contains("Cod client")) {
                codAbonat = "1003628159";
            }
            if (!total.isEmpty() && !nrFacturii.isEmpty() && !codAbonat.isEmpty()) {
                String value = fixString(total);
                invoice.setValue(value);
                invoice.setNr(nrFacturii);
                invoice.setCod(codAbonat);
                invoice.setDescription("factura de Gaz");
                invoice.setFurnizor("EON ENERGIE ROMANIA (GAZ SI ELECTRICITATE)");
                break;
            }
        }
    }

    private static String fixString(String total) {
        total = total.replaceAll("\\.", "");
        return total.replaceAll(",", ".");
    }

    public static String[] splitAfterLastSpace(String input) {
        int lastSpaceIndex = input.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            return new String[]{input}; // No space found, return the original string
        }
        String part1 = input.substring(0, lastSpaceIndex);
        String part2 = input.substring(lastSpaceIndex + 1);
        return new String[]{part1, part2};
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
                invoice.setFurnizor("SUPERCOM SA");
                invoice.setIban("RO85CECEB00030RON2670130");
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
                invoice.setFurnizor("COMPANIA DE APA SOMES - CLUJ");
                break;
            }
        }
    }
}
