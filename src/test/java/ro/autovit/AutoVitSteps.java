package ro.autovit;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.Result;
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

@Slf4j
public class AutoVitSteps extends TestBase {
    private static final String spreadsheetId = "1XQNi6a67uDzutiSNalDAVpSn2miUvE6Jtt8k8xk-Rvs";

    @SneakyThrows
    @And("I find on AutoVit and add in google sheets")
    public void iFindOnOLXAndAddInGoogleSheets() {
        acceptAll();
        WebLocator container = new WebLocator().setAttribute("data-testid", "main-container");
        WebLocator grid = new WebLocator(container).setAttribute("data-testid", "search-results");
        WebLocator nextPage = new WebLocator().setTag("li").setAttribute("title", "Go to next Page").setAttribute("aria-disabled", "false");
        List<String> links = new ArrayList<>();
        collectAllLinks(grid, links);
        while (nextPage.ready(Duration.ofSeconds(1))) {
            nextPage.mouseOver();
            RetryUtils.retry(8, () -> {
                boolean doClick = nextPage.doClick();
                if (!doClick) {
                    WebLocatorUtils.scrollToWebLocator(nextPage, -200);
                }
                return doClick;
            });
            Utils.sleep(2000);
            collectAllLinks(grid, links);
        }
        links = links.stream().filter(link -> !(Strings.isNullOrEmpty(link)
                || link.contains("smart")
                || link.contains("mini")
                || link.contains("e-up")
                || link.contains("yoyo")
                || link.contains("fiat-500")
                || link.contains("aixam")
                || link.contains("ford-transit")
                || link.contains("hyundai-i20")
                || link.contains("hyundai-i30")
                || link.contains("blue")
                || link.contains("kangoo")
                || link.contains("renault-trafic")
                || link.contains("renault-express")
                || link.contains("ford-courier")
                || link.contains("ford-fiesta")
        )).toList();
        Sheets sheetsService = GoogleSheet.getSheetsService();
        SheetProperties properties = GoogleSheet.getSheet(spreadsheetId, "MasinaAutoVit");
        Integer sheetId = properties.getSheetId();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(spreadsheetId, "MasinaAutoVit" + "!A2:H").execute();
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

    private static void collectAllLinks(WebLocator grid, final List<String> links) {
        WebLocator article = new WebLocator(grid).setTag("article").setAttribute("data-media-size", "small");
        int size = article.size();
        for (int i = 1; i <= size; i++) {
            article.setResultIdx(i);
            WebLink webLink = new WebLink(article);
            String link = webLink.getAttribute("href");
            links.add(link);
        }
    }

    @SneakyThrows
    @And("in AutoVit I open links and add details in google sheet")
    public void inAutoVitIOpenLinksAndAddDetailsInGoogleSheet() {
        Sheets sheetsService = GoogleSheet.getSheetsService();
        SheetProperties properties = GoogleSheet.getSheet(spreadsheetId, "MasinaAutoVit");
        Integer sheetId = properties.getSheetId();
        ValueRange valueRange = sheetsService.spreadsheets().values().get(spreadsheetId, "MasinaAutoVit" + "!A2:H").execute();
        List<List<Object>> values = valueRange.getValues();
        List<Request> requests = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < values.size(); i++) {
            List<Object> list = values.get(i);
            if (list.size() != 7) {
                String link = (String) list.get(0);
                WebDriverConfig.getDriver().get(link);
                if (j == 0) {
                    acceptAll();
                }
                WebLocator titleEl = new WebLocator().setClasses("offer-title");
                WebLocator nuExista = new WebLocator().setTag("h1").setText("Scuze, nu găsim pagina căutată…");
                Result<Boolean> result = RetryUtils.retryUntilOneIs(Duration.ofSeconds(4), titleEl::isPresent, nuExista::isPresent);
                if (result.position() == 2) {
                    log.info("link-ul nu exista? {}", link);
                    continue;
                }
                String title = RetryUtils.retry(3, titleEl::getText);
                if (Strings.isNullOrEmpty(title)) {
                    log.info("link-ul nu exista? {}", link);
                    continue;
                }
                int rowIndex = i + 1;
                GoogleSheet.addItemForUpdate(title, link, rowIndex, 1, sheetId, requests);
                WebLocator priceEl = new WebLocator().setClasses("offer-price__number");
                String price = "";
                try {
                    price = RetryUtils.retry(2, priceEl::getText).split("\n")[0];
                } catch (NullPointerException e) {
                    log.info("price not found? {}", link);
                }
                GoogleSheet.addItemForUpdate(price, rowIndex, 2, sheetId, requests);
                String an = getValueFormItem("Anul producției");
                GoogleSheet.addItemForUpdate(an, rowIndex, 3, sheetId, requests);
                try {
                    String km = getValueFormDetails("Km");
                    GoogleSheet.addItemForUpdate(km, rowIndex, 4, sheetId, requests);
                } catch (NullPointerException e) {
                    log.info("km not found? {}", link);
                }
                try {
                    WebLocator localizareEl = new WebLocator().setAttribute("href", "#map");
                    String localizare = RetryUtils.retry(8, localizareEl::getText).replaceAll("\n", "");
                    GoogleSheet.addItemForUpdate(localizare, rowIndex, 5, sheetId, requests);
                    String status = list.size() < 4 ? null : (String) list.get(4);
                    if (Strings.isNullOrEmpty(status)) {
                        GoogleSheet.addItemForUpdate("Nu am verificat", rowIndex, 6, sheetId, requests);
                    }
                } catch (NullPointerException e) {
                    log.info("localization nu exista? {}", link);
                }
                if (j == 30) {
                    BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                    BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
                    requests = new ArrayList<>();
                    j = 0;
                }
                j++;
            }
        }
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }

    private static String getValueFormItem(String label) {
        WebLocator pEl = new WebLocator().setTag("p").setText(label);
        WebLocator detailsItemEl = new WebLocator().setAttribute("data-testid", "year").setChildNodes(pEl);
        return detailsItemEl.getText().split("\n")[1];
    }

    private static String getValueFormDetails(String label) {
        WebLocator pEl = new WebLocator().setTag("p").setText(label);
        WebLocator detailsItemEl = new WebLocator().setAttribute("data-testid", "detail").setChildNodes(pEl);
        return detailsItemEl.getText().split("\n")[1];
    }
}
