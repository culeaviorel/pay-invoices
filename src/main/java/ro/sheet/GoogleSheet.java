package ro.sheet;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Strings;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GoogleSheet {
    private static Sheets sheetsService;
    private static final String sheetId = "16zaLSn9PqIZ3zeR51ZJS97p9HmG8agnc2EwIAG3GaKY";

    @SneakyThrows
    public static Sheets getSheetsService() {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get("src/test/resources/credentials.json"))).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("Google Sheets").build();
    }

    @SneakyThrows
    public static Drive getDriveService() {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get("src/test/resources/credentials.json"))).createScoped(SheetsScopes.all());
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("Google Sheets").build();
    }

    @SneakyThrows
    public static List<Sheet> getSheets() {
        return getSheets(sheetId);
    }

    @SneakyThrows
    public static List<Sheet> getSheets(String spreadsheetId) {
        sheetsService = getSheetsService();
        Spreadsheet execute = sheetsService.spreadsheets().get(spreadsheetId).execute();
        return execute.getSheets();
    }

    @SneakyThrows
    public static SheetProperties getSheet(String spreadsheetId, String name) {
        List<Sheet> sheets = getSheets(spreadsheetId);
        List<Sheet> activeSheets = sheets.stream().filter(i -> i.getProperties().getHidden() == null).toList();
        SheetProperties properties = null;
        for (Sheet sheet : activeSheets) {
            properties = sheet.getProperties();
            String title = properties.getTitle();
            if (name.equals(title)) {
                break;
            }
        }
        return properties;
    }

    @SneakyThrows
    public static String getNameFromActualSheet() {
        List<Sheet> sheets = getSheets();
        List<Sheet> activeSheets = sheets.stream().filter(i -> i.getProperties().getHidden() == null).toList();
        for (Sheet sheet : activeSheets) {
            String title = sheet.getProperties().getTitle();
            ValueRange response = sheetsService.spreadsheets().values().get(sheetId, title + "!B2:B6").execute();
            List<List<Object>> values = response.getValues();
            if (values.size() > 3) {
                String start = String.valueOf(values.get(1).get(0));
                String end = String.valueOf(values.get(3).get(0));
                LocalDate today = LocalDate.now();
                LocalDate startDate;
                LocalDate endDate;
                try {
                    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMMM yyyy").toFormatter(Locale.forLanguageTag("ro-RO"));
                    startDate = LocalDate.parse(start, formatter);
                    endDate = LocalDate.parse(end, formatter);
                } catch (DateTimeParseException e) {
                    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd MMM yyyy").toFormatter();
                    startDate = LocalDate.parse(start, formatter);
                    endDate = LocalDate.parse(end, formatter);
                }
                if (today.isAfter(startDate) && today.isBefore(endDate)) {
                    return title;
                }
            }
        }
        return null;
    }

    public static void addItemForUpdate(String value, int rowIndex, int columnIndex, int sheetId, final List<Request> requests) {
        addItemForUpdate(value, null, rowIndex, columnIndex, sheetId, requests);
    }

    public static void addItemForUpdate(String value, String link, int rowIndex, int columnIndex, int sheetId, final List<Request> requests) {
        addItemForUpdate(value, link, ",", rowIndex, columnIndex, sheetId, requests);
    }

    public static void addItemForUpdate(String value, String link, String separator, int rowIndex, int columnIndex, int sheetId, final List<Request> requests) {

        Color color;
        if (!Strings.isNullOrEmpty(link)) {
            color = new Color().setRed(0.0f).setGreen(0.0f).setBlue(1.0f).setAlpha(0.8f);
        } else if ("Unavailable".equals(value)) {
            color = new Color().setRed(0.8f).setGreen(0.0f).setBlue(0.0f).setAlpha(1.0f);
        } else if ("Available".equals(value)) {
            color = new Color().setRed(0.0f).setGreen(0.6f).setBlue(0.0f).setAlpha(1.0f);
        } else {
            color = new Color().setRed(0.0f).setGreen(0.0f).setBlue(0.0f).setAlpha(1.0f);
        }
        TextFormat textFormat = new TextFormat()
                .setFontSize(10)
                .setForegroundColor(color);
        CellFormat cellFormat = new CellFormat().setTextFormat(textFormat);
        ExtendedValue userEnteredValue;
        if (!Strings.isNullOrEmpty(link)) {
            userEnteredValue = new ExtendedValue().setFormulaValue("=HYPERLINK(\"" + link + "\"" + separator + " \"" + value + "\")");
        } else {
            userEnteredValue = new ExtendedValue().setStringValue(value);
        }
        CellData cellData = new CellData()
                .setUserEnteredValue(userEnteredValue)
                .setUserEnteredFormat(cellFormat);
        List<CellData> cellValues = List.of(cellData);
        RowData rowData = new RowData().setValues(cellValues);
        Request request = new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetId)
                                .setRowIndex(rowIndex)
                                .setColumnIndex(columnIndex)
                        )
                        .setRows(List.of(rowData))
//                                        .setFields("userEnteredValue,userEnteredFormat.textFormat.fontSize"));
                        .setFields("userEnteredValue,userEnteredFormat.textFormat"));
        requests.add(request);
    }

    public static void addItemForUpdate(double value, int rowIndex, int columnIndex, int sheetId, final List<Request> requests) {
        NumberFormat numberFormat = new NumberFormat();
        numberFormat.setType("CURRENCY");
        CellFormat cellFormat = new CellFormat().setNumberFormat(numberFormat);
        ExtendedValue userEnteredValue = new ExtendedValue().setNumberValue(value);
        CellData cellData = new CellData()
                .setUserEnteredValue(userEnteredValue)
                .setUserEnteredFormat(cellFormat);
        List<CellData> cellValues = List.of(cellData);
        RowData rowData = new RowData().setValues(cellValues);
        Request request = new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetId)
                                .setRowIndex(rowIndex)
                                .setColumnIndex(columnIndex)
                        )
                        .setRows(List.of(rowData))
                        .setFields("userEnteredValue,userEnteredFormat.numberFormat"));
        requests.add(request);
    }

    public static void addItemForUpdateDate(String value, int rowIndex, int columnIndex, int sheetId, final List<Request> requests) {
        NumberFormat numberFormat = new NumberFormat();
        numberFormat.setPattern("dd/MM/yyyy");
        numberFormat.setType("DATE");
        CellFormat cellFormat = new CellFormat().setNumberFormat(numberFormat);
        ExtendedValue userEnteredValue = new ExtendedValue().setStringValue(value);
        CellData cellData = new CellData()
                .setUserEnteredValue(userEnteredValue)
                .setUserEnteredFormat(cellFormat);
        List<CellData> cellValues = List.of(cellData);
        RowData rowData = new RowData().setValues(cellValues);
        Request request = new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetId)
                                .setRowIndex(rowIndex)
                                .setColumnIndex(columnIndex)
                        )
                        .setRows(List.of(rowData))
                        .setFields("userEnteredValue,userEnteredFormat.numberFormat"));
        requests.add(request);
    }

    public static void insertItem(int rowIndex, int sheetId, final List<Request> requests) {
        Request request = new Request()
                .setInsertDimension(new InsertDimensionRequest()
                        .setRange(new DimensionRange()
                                .setSheetId(sheetId)
                                .setDimension("ROWS")
                                .setStartIndex(rowIndex)
                                .setEndIndex(rowIndex + 1)
                        )
                );
        requests.add(request);
    }

    @SneakyThrows
    public static String createFile(String month) {
        Drive driveService = getDriveService();
        String name = "Raport " + month;
        String folderId = "1Uc2IebVqTxFSYJSDcnBXdjHCw9ioHDmR"; // folder id 2024/CSV
        String query = String.format("name = '%s' and '%s' in parents and trashed = false", name, folderId);
        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();
        if (!result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        } else {
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
            fileMetadata.setParents(List.of(folderId)); // folder id 2024/CSV
            File file = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            return file.getId();
        }
    }
}
