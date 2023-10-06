package ro.olx;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;
import ro.sheet.GoogleSheet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OLXSteps extends TestBase {
    private static final String spreadsheetId = "1XQNi6a67uDzutiSNalDAVpSn2miUvE6Jtt8k8xk-Rvs";

    @SneakyThrows
    @And("I find on OLX and add in google sheets")
    public void iFindOnOLXAndAddInGoogleSheets() {
        acceptAll();
        WebLocator container = new WebLocator().setAttribute("data-testid", "listing-filters-form");
        WebLocator grid = new WebLocator(container).setAttribute("data-testid", "listing-grid");
        WebLocator paginationContainer = new WebLocator(container).setAttribute("data-cy", "pagination");
        WebLink nextPage = new WebLink(paginationContainer).setAttribute("data-testid", "pagination-forward");
        List<String> links = new ArrayList<>();
        collectAllLinks(grid, links);
        WebLocatorUtils.scrollToWebLocator(paginationContainer);
        while (nextPage.ready(Duration.ofSeconds(1))) {
            nextPage.mouseOver();
            RetryUtils.retry(2, nextPage::click);
            Utils.sleep(1000);
            collectAllLinks(grid, links);
            WebLocatorUtils.scrollToWebLocator(paginationContainer);
            scrollBack();

        }
        Sheets sheetsService = GoogleSheet.getSheetsService();
        SheetProperties properties = GoogleSheet.getSheet(spreadsheetId, "BoilerOLX");
        Integer sheetId = properties.getSheetId();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(spreadsheetId, "BoilerOLX" + "!A2:H").execute();
        List<List<Object>> values = valueRange.getValues() == null ? new ArrayList<>() : valueRange.getValues();
        List<Request> requests = new ArrayList<>();
        int startAt = values.size();
        if (links.size() > startAt) {
            for (int i = 0; i < links.size(); i++) {
                String link = links.get(i);
                if (values.isEmpty()) {
                    GoogleSheet.addItemForUpdate(link, i + startAt + 1, 0, sheetId, requests);
                } else {
                    for (List<Object> list : values) {
                        if (!list.contains(link)) {
                            GoogleSheet.addItemForUpdate(link, i + startAt + 1, 0, sheetId, requests);
                        }
                    }
                }
            }
            Utils.sleep(1);
            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
            Utils.sleep(1);
        } else if (links.size() == startAt) {
            // nothing to do
            Utils.sleep(1);
        } else {
            List<String> oldLinks = new ArrayList<>();
            for (int i = 0; i < startAt; i++) {
                List<Object> value = values.get(i);
                String link = (String) value.get(0);
                if (!links.contains(link)) {
                    oldLinks.add(link);
                }
            }
//            TODO remove item
            Utils.sleep(1);
        }
    }

    private static void acceptAll() {
        WebLocator accept = new WebLocator().setId("onetrust-accept-btn-handler");
        RetryUtils.retry(2, accept::doClick);
    }

    private void scrollBack() {
        String script = "window.scrollBy(0,-200)";
        WebLocatorUtils.doExecuteScript(script);
    }

    private static void collectAllLinks(WebLocator grid, final List<String> links) {
        WebLocator card = new WebLocator(grid).setAttribute("data-cy", "l-card");
        int size = card.size();
        for (int i = 1; i <= size; i++) {
            card.setResultIdx(i);
            WebLink webLink = new WebLink(card);
            String link = webLink.getAttribute("href");
            links.add(link);
        }
    }

    @SneakyThrows
    @And("I open links and add details in google sheet")
    public void iOpenLinksAndAddDetailsInGoogleSheet() {
        Sheets sheetsService = GoogleSheet.getSheetsService();
        SheetProperties properties = GoogleSheet.getSheet(spreadsheetId, "BoilerOLX");
        Integer sheetId = properties.getSheetId();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(spreadsheetId, "BoilerOLX" + "!A2:H").execute();
        List<List<Object>> values = valueRange.getValues();
        List<Request> requests = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < values.size(); i++) {
            List<Object> list = values.get(i);
            if (Strings.isNullOrEmpty((String) list.get(1))) {
                String link = (String) list.get(0);
                WebDriverConfig.getDriver().get(link);
                if (j == 0) {
                    acceptAll();
                }
                WebLocator priceEl = new WebLocator().setAttribute("data-testid", "ad-price-container");
                String price = priceEl.getText().split("\n")[0];
                GoogleSheet.addItemForUpdate(price, i + 1, 2, sheetId, requests);
                WebLocator titleEl = new WebLocator().setAttribute("data-cy", "ad_title");
                String title = RetryUtils.retry(3, titleEl::getText);
                GoogleSheet.addItemForUpdate(title, link, i + 1, 1, sheetId, requests);
                WebLocator localizareContainer = new WebLocator().setTag("p").setText("Localizare");
                WebLocator localizareEl = new WebLocator(localizareContainer).setRoot("/following-sibling::");
                String localizare = RetryUtils.retry(8, localizareEl::getText).replaceAll("\n", "");
                GoogleSheet.addItemForUpdate(localizare, i + 1, 3, sheetId, requests);
                String status = (String) list.get(4);
                if (Strings.isNullOrEmpty(status)) {
                    GoogleSheet.addItemForUpdate("Nu am verificat", i + 1, 4, sheetId, requests);
                }
                Utils.sleep(1);
                if (j == 30) {
                    BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                    BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
                    requests = new ArrayList<>();
                    j = 0;
                }
                j++;
            }
        }
        Utils.sleep(1);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }

    @SneakyThrows
    @And("I remove rows from google sheet")
    public void iRemoveRowsFromGoogleSheet() {
        Sheets sheetsService = GoogleSheet.getSheetsService();
        SheetProperties properties = GoogleSheet.getSheet(spreadsheetId, "BoilerOLX");
        Integer sheetId = properties.getSheetId();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(spreadsheetId, "BoilerOLX" + "!A2:H").execute();
        List<List<Object>> values = valueRange.getValues();
        List<List<String>> valuesList = values.stream().map(l -> l.stream().map(Object::toString).collect(Collectors.toList())).collect(Collectors.toList());
        List<Request> requests = new ArrayList<>();
        List<String> deletedLinks = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < valuesList.size(); i++) {
            List<String> list = valuesList.get(i);
            String title = list.get(1);
            if (delete(title)) {
                log.info("Delete: {}", title);
                DeleteDimensionRequest deleteRequest = new DeleteDimensionRequest()
                        .setRange(
                                new DimensionRange()
                                        .setSheetId(sheetId)
                                        .setDimension("ROWS")
                                        .setStartIndex(i + 1 - j)
                                        .setEndIndex(i + 2 - j)
                        );
                requests.add(new Request().setDeleteDimension(deleteRequest));
                j++;
            }
        }
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        Utils.sleep(1);
    }

    private static boolean deleteDuplicate(List<String> list, List<List<String>> links, final List<String> deletedLinks) {
        String link = list.get(0);
        boolean match = links.stream().anyMatch(i -> i.stream().anyMatch(j -> j.equals(link)));
        if (match && !deletedLinks.contains(link)) {
            Utils.sleep(1);
            deletedLinks.add(link);
            return true;
        } else {
            return false;
        }
    }

    private static boolean delete(String title) {
        return title.contains("rezistent")
                || title.contains("REZISTENT")
                || title.contains("Rezistent")
                || title.contains("Panou")
                || title.contains("panou")
                || title.contains("PANOUri Solare")
                || title.contains("panouri")
                || title.contains("Termostat")
                || title.contains("pompa de caldura")
                || title.contains("instant")
                || title.contains("Instant")
                || title.contains("Central")
                || title.contains("central")
                || title.contains("calcat cu")
                || title.contains("lemne")
                || title.contains("Invertor")
                || title.contains("Masurător")
                || title.contains("cafea")
                || title.contains("Soba")
                || title.contains("Intrerupator")
                || title.contains("Priza")
                || title.contains("Robinet")
                || title.contains("Tablou")
                || title.contains("Contactor")
                || title.contains("Controler")
                || title.contains("cazan")
                || title.contains("regulator")
                || title.contains("TECEfloor")
                || title.contains("Pachet")
                ;
    }
}
