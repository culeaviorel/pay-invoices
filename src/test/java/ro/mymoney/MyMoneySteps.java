package ro.mymoney;

import com.google.common.base.Strings;
import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.grid.Cell;
import com.sdl.selenium.extjs6.grid.Grid;
import com.sdl.selenium.extjs6.grid.Row;
import com.sdl.selenium.extjs6.window.MessageBox;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.utils.RetryUtils;
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
import java.text.DecimalFormatSymbols;
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
        List<Item> addItems = new ArrayList<>();
        List<Item> items = readCSV("C:\\Users\\vculea\\OneDrive - RWS\\Desktop\\BT\\Ianuarie.csv");
        String date1 = items.get(0).getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate d = LocalDate.parse(date1.split(" ")[0], formatter);
        String monthAndYear = d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear();
        boolean inCorrectView = isInCorrectView(monthAndYear);
        if (inCorrectView) {
            view.getGrid().ready(true);
            for (Item item : items) {
                view.getGrid().scrollTop();
                LocalDate datetime = LocalDate.parse(item.getDate().split(" ")[0], formatter);
                String date = datetime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
                String sum = getCorrectValue(item.getSum());
                Row row = view.getGrid().getRow(new Cell(4, date), new Cell(5, sum, SearchType.EQUALS));
                if (!row.scrollInGrid() || !row.waitToRender(Duration.ofMillis(100), false)) {
                    String subCategory = getSubCategory(item.getName());
                    if (Strings.isNullOrEmpty(subCategory)) {
                        notFoundSubCategory.add(item);
                    } else {
                        addItems.add(item);
                        log.info(subCategory);
                        view.addInsert(subCategory, "Cheltuieli", subCategory, item.getDate(), item.getSum());
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

    private boolean isInCorrectView(String monthAndYear) {
        Button button = new Button(null, monthAndYear);
        return RetryUtils.retry(6, () -> {
            boolean ready = button.ready();
            if (!ready) {
                view.getLeftButton().click();
            }
            return ready;
        });
    }

    private String getCorrectValue(String sum) {
        double s = Double.parseDouble(sum);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.#", dfs);
        String format = df.format(s);
        String value = format.contains(".") ? format : format + ".0";
        return value;
    }

    public List<String> casa = List.of("HORNBACH", "LEROY MERLIN", "DEDEMAN", "ALTEX ROMANIA", "MAXIMUM ELECTRONIC"
            , "SC PRAGMATIC TCV SRL", "EPwifistore.ro", "INTERNATIONAL PAPER BUSI"
            , "KSA"
    );
    public List<String> produseAlimentare = List.of("Lidl", "LIDL", "DEDEMAN", "AUCHAN", "PENNY", "KAUFLAND"
            , "Kaufland", "MEGAIMAGE", "BONAS", "LA VESTAR", "PROFI", "CICMAR", "VARGA", "BUCURCRISS", "FLAVIANDA CRISAN"
            , "AGROPAN PRODCOM", "TIENDA FRUTAS", "PREMIO DISTRIBUTION", "PREMIER RESTAURANTS", "PANEMAR", "ARTIMA SA"
            , "MAGAZIN LA 2 PASI", "INM KFL CLUJ FAB C1", "ANAMIR BIOMARKET SRL", "MAVIOS IMPEX SRL", "MCFLYING SRL"
            , "CARREFOUR", "RES QUALITY FOOD", "Linela", "SELGROS", "CARMIC IMPEX", "BODRUM DONER MARASTI", "SC OPREA AVI COM SRL"
            , "RODIMEX INVEST", "MELFRUCTUS SRL", "ADARIA SERV SRL", "MEGA IMAGE"
            , "ERGON"
    );
    public List<String> haine = List.of("ZARA", "H&M", "PEPCO", "ORGANIZATIA CRESTINA", "KiK Textilien"
            , "LANELKA", "MELI MELO", "SINSAY", "REGALALIMENTNONSTO", "JYSK", "THE BODY SHOP", "BRICOSTORE", "C & A"
            , "ROUMASPORT SRL", "Decathlon", "TABITA IMPEX SRL", "KIK 9119 CLUJ"
            , "SECONDTEXTILIASAM"
    );
    public List<String> masina = List.of("OMV", "Roviniete", "Taxa De Pod", "SAFETY BROKER", "SOS ITP SERVICE",
            "MALL DOROBANTILOR SERVICE", "MC BUSINESS", "ATTRIUS DEVELOPMENTS", "LUKOIL", "EURO PARTS DISTRIB");
    private List<String> alte = List.of("EXCELLENTE SOURCE", "EUROTRANS SRL", "PAYU", "IMPRIMERIA NATIONALA"
            , "MOTILOR", "WANG FU BUSINESS", "ALGO ENTERTAINMENT", "FUNDATIA PRISON", "VELLA MED DISTRICT", "DRM CLUJ"
            , "HUSE COLORATE", "KIDDYPARK SRL", "SC PIATA MARASTI SRL", "MOBILPAYKASEWEB DISTR", "VO CHEF SRL"
            , "OTEN V B SRL ARIESULUI", "CINEMA CITY ROMANIA", "MACRIDELI FLOWERS SRL", "LIBRARIA KERIGMA CLU"
            , "NEW IDEA PRINT SRL", "FLORI BESTIALE SRL", "EROGLU ROMANIA SRL"
            , "RATI INNOVATIONS SRL"

    );
    private List<String> restaurant = List.of("LEMNUL VERDE", "ASI BAKLAVA", "MOLDOVAN CARMANGERIE", "HOMS FOOD"
            , "TARTINE FACTORY SRL", "OCEANUL PACIFIC", "CARESA CATERING", "BIANCO MILANO", "ADIADO", "MADO CORPORATION"
            , "PARFOIS", "Onesti - Marasesti", "KFC", "HANUL CU PESTE", "MARTY", "PEP & PEPPER", "STARBUCKS", "DASHI"
            , "LC WAIKIKI", "ART OF CAKES SRL", "CARIANA ALIMENTAR SRL", "KOPP KAFFE", "MEAT UP"
            , "MILENIUM LANDSCAPE DEV", "SAVANNAH DRINKS", "SONMARE SRL", "MARKET TWELVE SRL", "Eurest Rom SRL Bosch"
            , "JAMON FOOD SRL"
            , "ROSA FOOD ART SRL"
    );

    public static void main(String[] args) {
        MyMoneySteps d = new MyMoneySteps();
//        String correctValue = d.getCorrectValue("2.34");
//        String correctValue1 = d.getCorrectValue("2.56");
        String name = "HORNBACH SA";
        boolean contains = d.casa.stream().anyMatch(name::contains);
        Utils.sleep(1);
    }

    private String getSubCategory(String name) {
        String subCategory = null;
        if (produseAlimentare.stream().anyMatch(name::contains)) {
            subCategory = "Produse alimentare";
        } else if (casa.stream().anyMatch(name::contains)) {
            subCategory = "Casa";
        } else if (haine.stream().anyMatch(name::contains)) {
            subCategory = "Haine";
        } else if (masina.stream().anyMatch(name::contains)) {
            subCategory = "Masina";
        } else if (alte.stream().anyMatch(name::contains)) {
            subCategory = "Alte Cheltuieli";
        } else if (List.of("REMEDIUM", "ALDEDRA", "Farmactiv SRL").stream().anyMatch(name::contains)) {
            subCategory = "Medicamente";
        } else if (List.of("ABURIDO SRL", "WWW.PROMOMIX.RO", "NALA COSMETICS SRL", "GERMAN MARKET SRL").stream().anyMatch(name::contains)) {
            subCategory = "Igiena";
        } else if (List.of("ANDY EVENTS", "ORANGE SMART STORE CAH", "EC GARDEN MANAGEMENT").stream().anyMatch(name::contains)) {
            subCategory = "Cadouri";
        } else if (List.of("CPL Concordia").stream().anyMatch(name::contains)) {
            subCategory = "Gaz";
        } else if (List.of("Compania de Apa").stream().anyMatch(name::contains)) {
            subCategory = "Apa";
        } else if (List.of("Hidroelectrica").stream().anyMatch(name::contains)) {
            subCategory = "Energie Electrica";
        } else if (List.of("Hotel at Booking.com", "SUFRO COMPANY SRL").stream().anyMatch(name::contains)) {
            subCategory = "Concedii";
        } else if (List.of("TEENCHALLENGECLUJ.ORG").stream().anyMatch(name::contains)) {
            subCategory = "Darnicie";
        } else if (List.of("CTP", "tpark.ro", "PARKING EXPERTS", "ATTRIUS DEVELOPMENTS PALAS IASI").stream().anyMatch(name::contains)) {
            subCategory = "Transport";
        } else if (List.of("WWW.GHISEUL.RO/MFINANT").stream().anyMatch(name::contains)) {
            subCategory = "Taxe";
        } else if (List.of("Digi(RCS RDS)").stream().anyMatch(name::contains)) {
            subCategory = "Internet";
        } else if (List.of("Platforma E-BLOC.RO").stream().anyMatch(name::contains)) {
            subCategory = "Investitii";
        } else if (restaurant.stream().anyMatch(name::contains)) {
            subCategory = "Restaurant";
        }
        return subCategory;
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
            if (val.contains("2023")) {
                List<String> values = record.toList();
                if ("Decontat".equals(values.get(2))) {
                    list.add(new Item(values.get(0).split(" ")[0], values.get(3), values.get(5).replace(",", ".")));
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
            if (val.contains("2023") && val.contains("/01/")) {
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
        boolean inCorrectView = isInCorrectView(monthAndYear);
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
        boolean inCorrectView = isInCorrectView(monthAndYear);
        if (inCorrectView) {
            view.getGrid().ready(true);
            for (Item item : items) {
                view.getGrid().scrollTop();
                LocalDate datetime = LocalDate.parse(item.getDate(), formatter);
                String date = datetime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
                String sum = getCorrectValue(item.getSum());
                Row row = view.getGrid().getRow(new Cell(4, date), new Cell(5, sum, SearchType.EQUALS));
                if (!row.scrollInGrid() || !row.waitToRender(Duration.ofMillis(100), false)) {
                    String subCategory = getSubCategory(item.getName());
                    if (Strings.isNullOrEmpty(subCategory)) {
                        notFoundSubCategory.add(item);
                    } else {
                        addItems.add(item);
                        log.info(subCategory);
                        view.addInsert(subCategory, "Cheltuieli", subCategory, item.getDate(), "dd/MM/yyyy", item.getSum());
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
            view.addInsert(value.getName(), value.getCategory(), value.getSubCategory(), value.getData(),"dd.MM.yyyy", value.getValue());
        }
    }
}
