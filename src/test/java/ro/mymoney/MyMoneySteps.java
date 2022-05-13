package ro.mymoney;

import com.google.common.base.Strings;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Item> items = readCSV("C:\\Users\\vculea\\Desktop\\BT\\Aprilie.csv");
        for (Item item : items) {
            String subCategory = getSubCategory(item.getName());
            if (Strings.isNullOrEmpty(subCategory)) {
                log.info(item.toString());
            } else {
                view.addInsert(item.getName(), "Cheltuieli", subCategory, item.getDate(), item.getSum().replace(",", "."));
            }
        }
        Utils.sleep(1);
    }

    private String getSubCategory(String name) {
        String subCategory = null;
        if (name.contains("Lidl") || name.contains("AUCHAN") || name.contains("PENNY") || name.contains("KAUFLAND") ||
                name.contains("MEGAIMAGE") || name.contains("BONAS") || name.contains("LA VESTAR")
        ) {
            subCategory = "Produse alimentare";
        } else if (name.contains("HORNBACH") || name.contains("LEROY MERLIN")) {
            subCategory = "Casa";
        } else if (name.contains("ZARA") || name.contains("H&M") || name.contains("PEPCO")) {
            subCategory = "Haine";
        } else if (name.contains("OMV")) {
            subCategory = "Masina";
        } else if (name.contains("REMEDIUM")) {
            subCategory = "Medicamente";
        } else if (name.contains("CTP") || name.contains("tpark.ro")) {
            subCategory = "Transport";
        } else if (name.contains("LEMNUL VERDE") || name.contains("ASI BAKLAVA")) {
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
                    list.add(new Item(values.get(0), values.get(2), values.get(3), values.get(5)));
                }
            }
        }
        return list;
    }
}
