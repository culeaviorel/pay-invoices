package ro.sheet;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Strings;
import lombok.SneakyThrows;

import java.io.IOException;
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

    public static Sheets getSheetsService() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get("src/test/resources/credentials.json"))).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
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
        Color color;
        if ("Unavailable".equals(value)) {
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
            userEnteredValue = new ExtendedValue().setFormulaValue("=HYPERLINK(\"" + link + "\", \"" + value + "\")");
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
}
