package ro.mymoney;

import com.google.common.base.Strings;
import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.grid.Cell;
import com.sdl.selenium.extjs6.grid.Row;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.fasttrackit.util.TestBase;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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

    @Then("I login on MyVirtual using {string} and {string}")
    public void login(String user, String pass) {
        login.login(user, pass);
    }

    @When("^I add new insert \"([^\"]*)\"/\"([^\"]*)\"/\"([^\"]*)\"$")
    public void iAddNewInsert(String denum, String categ, String sub) {
        view.addInsert(denum, categ, sub, System.getProperty("sum").replaceAll(",", "."));
    }

    @And("I read csv file and insert date")
    public void iReadCsvFileAndInsertDate() {
        List<Item> notFoundSubCategory = new ArrayList<>();
        List<Item> isAlreadyExist = new ArrayList<>();
        List<Item> items = readCSV("C:\\Users\\vculea\\Desktop\\BT\\Aprilie.csv");
        LocalDate d = LocalDate.parse(items.get(0).getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String monthAndYear = d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear();
        Button button = new Button(null, monthAndYear);
        boolean inCorrectView = false;
        if (!button.ready()) {
            view.getLeftButton().click();
            inCorrectView = button.ready();
        }
        if (inCorrectView) {
            view.getGrid().ready(true);
            for (Item item : items) {
                view.getGrid().scrollTop();
                LocalDate datetime = LocalDate.parse(item.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                String date = datetime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
                String sum = getCorrectValue(item.getSum());
                Row row = view.getGrid().getRow(new Cell(4, date), new Cell(5, sum));
                if (!row.waitToRender(Duration.ofSeconds(2), false) && !row.scrollTo()) {
                    String subCategory = getSubCategory(item.getName());
                    if (Strings.isNullOrEmpty(subCategory)) {
                        notFoundSubCategory.add(item);
                    } else {
                        view.addInsert(subCategory, "Cheltuieli", subCategory, item.getDate(), item.getSum());
                    }
                } else {
                    isAlreadyExist.add(item);
                }
            }
        }
        log.info("Nu s-a putut gasi subcategori pentru:");
        notFoundSubCategory.forEach(i -> log.info(i.toString()));
        log.info("Deja erau adaugate:");
        isAlreadyExist.forEach(i -> log.info(i.toString()));
        log.info("Diferenta: {}", items.size() - isAlreadyExist.size() - notFoundSubCategory.size());
        Utils.sleep(1);
    }

    private String getCorrectValue(String sum) {
        double s = Double.parseDouble(sum);
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(s).replace(",", ".");
    }

    public static void main(String[] args) {
        MyMoneySteps d = new MyMoneySteps();
        String correctValue = d.getCorrectValue("2.34");
        String correctValue1 = d.getCorrectValue("2.56");
        Utils.sleep(1);
    }

    private String getSubCategory(String name) {
        String subCategory = null;
        if (name.contains("Lidl") || name.contains("LIDL") || name.contains("AUCHAN") || name.contains("PENNY") || name.contains("KAUFLAND") ||
                name.contains("MEGAIMAGE") || name.contains("BONAS") || name.contains("LA VESTAR") || name.contains("PROFI") ||
                name.contains("CICMAR") || name.contains("VARGA") || name.contains("BUCURCRISS")
        ) {
            subCategory = "Produse alimentare";
        } else if (name.contains("HORNBACH") || name.contains("LEROY MERLIN")) {
            subCategory = "Casa";
        } else if (name.contains("ZARA") || name.contains("H&M") || name.contains("PEPCO") || name.contains("ORGANIZATIA CRESTINA")) {
            subCategory = "Haine";
        } else if (name.contains("OMV")) {
            subCategory = "Masina";
        } else if (name.contains("EXCELLENTE SOURCE") || name.contains("EUROTRANS SRL")) {
            subCategory = "Alte Cheltuieli";
        } else if (name.contains("REMEDIUM")) {
            subCategory = "Medicamente";
        } else if (name.contains("ABURIDO SRL")) {
            subCategory = "Igiena";
        } else if (name.contains("ANDY EVENTS")) {
            subCategory = "Cadouri";
        } else if (name.contains("CTP") || name.contains("tpark.ro")) {
            subCategory = "Transport";
        } else if (name.contains("LEMNUL VERDE") || name.contains("ASI BAKLAVA") || name.contains("MOLDOVAN CARMANGERIE")) {
            subCategory = "Restaurant";
        }
        return subCategory;
    }

    @SneakyThrows
    public List<Item> readCSV(String filePath) {
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.ISO_8859_1);
        CSVFormat csvFormat = CSVFormat.Builder.create().setDelimiter(';').build();
        CSVParser csvParser = new CSVParser(reader, csvFormat);
        List<CSVRecord> records = csvParser.getRecords();
        List<Item> list = new ArrayList<>();
        for (CSVRecord record : records) {
            String val = record.toList().get(0);
            if (val.contains("2022")) {
                List<String> values = record.toList();
                if ("Decontat".equals(values.get(2))) {
                    list.add(new Item(values.get(0), values.get(2), values.get(3), values.get(5).replace(",", ".")));
                }
            }
        }
        return list;
    }
}
