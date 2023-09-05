package ro.sheet;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Strings;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GoogleSheet {
    private static Sheets sheetsService;
    private static final String sheetId = "16zaLSn9PqIZ3zeR51ZJS97p9HmG8agnc2EwIAG3GaKY";

    public static Sheets getSheetsService() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get("src/test/resources/credentials.json"))).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName("Google Sheets").build();
    }

    @SneakyThrows
    public static List<Sheet> getSheets() {
        sheetsService = getSheetsService();
        Spreadsheet execute = sheetsService.spreadsheets().get(sheetId).execute();
        return execute.getSheets();
    }

    @SneakyThrows
    public static String getNameFromActualSheet() {
        List<Sheet> sheets = getSheets();
        List<Sheet> activeSheets = sheets.stream().filter(i -> i.getProperties().getHidden() == null).collect(Collectors.toList());
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

    public static void main(String[] args) throws IOException {
        String nameFromActualSheet = getNameFromActualSheet();
        if (!Strings.isNullOrEmpty(nameFromActualSheet)) {
            String range = nameFromActualSheet + "!B1:S";
            Sheets.Spreadsheets.Values sheets = sheetsService.spreadsheets().values();
            ValueRange response = sheets.get(sheetId, range).execute();
            List<List<Object>> values = response.getValues();
            String name = "Vio";
            int index = values.get(0).indexOf(name);
            for (int i = 24; i < values.size(); i++) {
                List<Object> list = values.get(i);
                if(list.isEmpty()){
                    Utils.sleep(1);
                } else {
                    Utils.sleep(1);
                }

            }
            Utils.sleep(1);
        }
    }
}
