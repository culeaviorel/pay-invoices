package ro.mymoney;

import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.extjs6.grid.Cell;
import com.sdl.selenium.extjs6.grid.Grid;
import com.sdl.selenium.extjs6.grid.Row;
import com.sdl.selenium.extjs6.window.MessageBox;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.neo.Storage;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class MyMoneySteps extends TestBase {
    private final Login login = new Login();
    private final View view = new View();
    private final UserCredentials credentials = new UserCredentials();

    @Then("I login on MyVirtual")
    public void login() {
        login.login(credentials.getMyVirtualEmail(), credentials.getMyVirtualPassword());
    }

    @And("I read {string} csv file and insert date")
    public void iReadCsvFileAndInsertDate(String filePath) {
        List<Item> items = readCSV(filePath);
        view.addItems(items);
    }

    @SneakyThrows
    public List<Item> readCSV(String filePath) {
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.ISO_8859_1);
        CSVFormat csvFormat = CSVFormat.Builder.create().setDelimiter(',').build();
        CSVParser csvParser = new CSVParser(reader, csvFormat);
        List<CSVRecord> records = csvParser.getRecords();
        List<Item> list = new ArrayList<>();
        for (CSVRecord record : records) {
            String val = record.toList().get(0);
            if (val.contains("-2025")) {
                List<String> values = record.toList();
                if ("Decontat".equals(values.get(2))) {
                    list.add(new Item(values.get(0).split(" ")[0], values.get(3), values.get(4).replace(",", ".")));
                }
            }
        }
        return list;
    }

    @SneakyThrows
    public List<Item> readUPCSV(String filePath) {
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.ISO_8859_1);
        CSVFormat csvFormat = CSVFormat.Builder.create().setDelimiter(',').build();
        CSVParser csvParser = new CSVParser(reader, csvFormat);
        List<CSVRecord> records = csvParser.getRecords();
        List<Item> list = new ArrayList<>();
        for (CSVRecord record : records) {
            String val = record.toList().get(2);
            if (val.contains("2023") && val.contains("/05/")) {
                List<String> values = record.toList();
                if (values.get(4).contains("DEBIT")) {
                    list.add(new Item(clean(values.get(2)), clean(values.get(1)), clean(values.get(6).replace(",", ".").replace("-", ""))));
                }
            }
        }
        return list;
    }

    public String clean(String value) {
        value = value.replace("=\"", "").replace("\"", "");
        return value;
    }

    @And("I remove duplicate for {string} month")
    public void iRemoveDuplicateFromMonth(String month) {
        LocalDate d = LocalDate.now();
        String monthAndYear = month + " " + d.getYear();
        boolean inCorrectView = view.isInCorrectView(monthAndYear);
        if (inCorrectView) {
            Grid grid = view.getGrid();
            grid.ready(true);
            Row row = null;
            while (row == null || getNextRow(row).isPresent()) {
                List<String> values = row == null ? grid.getRow(1).getCellsText() : getNextRow(row).getCellsText();
                log.info(values.toString());
                row = grid.getRow(new Cell(1, values.get(0)),
                        new Cell(2, values.get(1)),
                        new Cell(3, values.get(2)),
                        new Cell(4, values.get(3)),
                        new Cell(5, values.get(4))
                );
                WebLocatorUtils.scrollToWebLocator(row);
                int size = row.size();
                log.info("size:{}", size);
                for (int j = 2; j <= size; j++) {
                    row.setResultIdx(2);
                    Row finalRow = row;
                    RetryUtils.retry(3, () -> {
                        finalRow.doClick();
                        return finalRow.doDoubleClickAt();
                    });
                    view.getRemoveButton().click();
                    new MessageBox("Remove").getYesButton().click();
                }
                row.setResultIdx(-1);
            }
        }
    }

    public Row getNextRow(Row row) {
        return new Row(row).setRoot("/").setTag("following-sibling::table[1]");
    }

    @And("I read csv file from UP and insert date")
    public void iReadCsvFileFromUPAndInsertDate() {
        List<Item> notFoundSubCategory = new ArrayList<>();
        List<Item> isAlreadyExist = new ArrayList<>();
        List<Item> addItems = new ArrayList<>();
        List<Item> items = readUPCSV("C:\\Users\\vculea\\OneDrive - RWS\\Desktop\\BT\\data.csv");
        String date1 = items.get(0).getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate d = LocalDate.parse(date1, formatter);
        String monthAndYear = d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear();
        boolean inCorrectView = view.isInCorrectView(monthAndYear);
        if (inCorrectView) {
            view.getGrid().ready(true);
            for (Item item : items) {
                view.getGrid().scrollTop();
                LocalDate datetime = LocalDate.parse(item.getDate(), formatter);
                String date = datetime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
                String sum = view.getCorrectValue(item.getSum());
                Transaction transaction = view.getSubCategory(item.getName());
                String name = null;
                try {
                    name = transaction.getName();
                } catch (NullPointerException e) {
                    Utils.sleep(1);
                }
                String subCategory = transaction.getSubCategory();
                Row row = view.getGrid().getRow(new Cell(1, name), new Cell(3, subCategory), new Cell(4, date), new Cell(5, sum, SearchType.EQUALS));
                if (!row.scrollInGrid() || !row.waitToRender(Duration.ofMillis(100), false)) {
                    if (Strings.isNullOrEmpty(name)) {
                        notFoundSubCategory.add(item);
                    } else {
                        addItems.add(item);
                        log.info(name);
                        view.addInsert(name, "Cheltuieli", subCategory, item.getDate(), "dd/MM/yyyy", item.getSum());
                    }
                } else {
                    isAlreadyExist.add(item);
                }
            }
        }
        log.info("Deja erau adaugate:");
        isAlreadyExist.forEach(i -> log.info(i.toString()));
        log.info("S-au adaugat:");
        addItems.forEach(i -> log.info(i.toString()));
        log.info("Nu s-a putut gasi subcategori pentru:");
        notFoundSubCategory.forEach(i -> log.info(i.toString()));
        log.info("Diferenta: {}", items.size() - isAlreadyExist.size() - notFoundSubCategory.size() - addItems.size());
        Utils.sleep(1);
    }

    @And("I add in MyVirtual transactions:")
    public void iAddInMyVirtualTransactions(List<ItemTO> values) {
        for (ItemTO value : values) {
            view.addInsert(value.getName(), value.getCategory(), value.getSubCategory(), value.getData(), "dd.MM.yyyy", value.getValue());
        }
    }

    @And("in MyVirtual I add transactions from storage")
    public void inMyVirtualIAddTransactionsFromStorage() {
        List<ItemTO> items = Storage.get("items");
        view.addItemsTO(items);
    }
}
