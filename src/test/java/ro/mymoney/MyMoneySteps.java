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
import java.util.Optional;

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
        List<Item> items = readCSV("C:\\Users\\vculea\\OneDrive - RWS\\Desktop\\BT\\Februarie.csv");
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
                Transaction transaction = getSubCategory(item.getName());
                String name = transaction.getName();
                String subCategory = transaction.getSubCategory();
                Row row = view.getGrid().getRow(new Cell(1, name), new Cell(3, subCategory), new Cell(4, date), new Cell(5, sum, SearchType.EQUALS));
                if (!row.scrollInGrid() || !row.waitToRender(Duration.ofMillis(100), false)) {
                    if (Strings.isNullOrEmpty(name)) {
                        notFoundSubCategory.add(item);
                    } else {
                        addItems.add(item);
                        log.info(name);
                        view.addInsert(name, "Cheltuieli", subCategory, item.getDate(), item.getSum());
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

    private final List<Category> casa = List.of(new Category("HORNBACH", "HORNBACH"), new Category("LEROY MERLIN", "LEROY MERLIN"), new Category("DEDEMAN", "DEDEMAN")
            , new Category("ALTEX ROMANIA", "ALTEX ROMANIA"), new Category("MAXIMUM ELECTRONIC", "MAXIMUM ELECTRONIC")
            , new Category("SC PRAGMATIC TCV SRL", "SC PRAGMATIC TCV SRL"), new Category("EPwifistore.ro", "EPwifistore.ro")
            , new Category("INTERNATIONAL PAPER BUSI", "INTERNATIONAL PAPER BUSI"), new Category("KSA", "KSA")
            , new Category("MOEMAX Cluj", "MOEMAX Cluj")
    );
    private final List<Category> produseAlimentare = List.of(new Category("Lidl", List.of("Lidl", "LIDL")), new Category("Dedeman", "DEDEMAN")
            , new Category("Auchan", "AUCHAN"), new Category("Penny", "PENNY"), new Category("Kaufland", "KAUFLAND")
            , new Category("Kaufland", "Kaufland"), new Category("Mega Image", List.of("MEGAIMAGE", "MEGA IMAGE")), new Category("Bonas", "BONAS"), new Category("La Vestar", "LA VESTAR")
            , new Category("Profi", "PROFI"), new Category("CICMAR", "CICMAR"), new Category("VARGA", "VARGA"), new Category("BUCURCRISS", "BUCURCRISS")
            , new Category("Flavianda", "FLAVIANDA CRISAN"), new Category("Agropan", "AGROPAN PRODCOM"), new Category("TIENDA FRUTAS", "TIENDA FRUTAS")
            , new Category("PREMIO DISTRIBUTION", "PREMIO DISTRIBUTION"), new Category("Premier Restaurants", "PREMIER RESTAURANTS"), new Category("Panemar", "PANEMAR")
            , new Category("ARTIMA SA", "ARTIMA SA"), new Category("MAGAZIN LA 2 PASI", "MAGAZIN LA 2 PASI"), new Category("INM KFL CLUJ FAB C1", "INM KFL CLUJ FAB C1")
            , new Category("ANAMIR BIOMARKET SRL", "ANAMIR BIOMARKET SRL"), new Category("MAVIOS IMPEX SRL", "MAVIOS IMPEX SRL"), new Category("MCFLYING SRL", "MCFLYING SRL")
            , new Category("Carrefour", "CARREFOUR"), new Category("RES QUALITY FOOD", "RES QUALITY FOOD"), new Category("Linela", "Linela"), new Category("Selgros", "SELGROS")
            , new Category("CARMIC IMPEX", "CARMIC IMPEX"), new Category("BODRUM DONER MARASTI", "BODRUM DONER MARASTI"), new Category("SC OPREA AVI COM SRL", "SC OPREA AVI COM SRL")
            , new Category("RODIMEX INVEST", "RODIMEX INVEST"), new Category("MELFRUCTUS SRL", "MELFRUCTUS SRL"), new Category("ADARIA SERV SRL", "ADARIA SERV SRL")
            , new Category("Ergon", "ERGON")
            , new Category("Rebeca Fruct SRL", "REBECA FRUCT SRL")
    );
    private final List<Category> haine = List.of(new Category("ZARA", "ZARA"), new Category("H&M", "H&M"), new Category("PEPCO", "PEPCO")
            , new Category("ORGANIZATIA CRESTINA", "ORGANIZATIA CRESTINA"), new Category("KiK Textilien", "KiK Textilien")
            , new Category("LANELKA", "LANELKA"), new Category("MELI MELO", "MELI MELO"), new Category("SINSAY", "SINSAY")
            , new Category("REGALALIMENTNONSTO", "REGALALIMENTNONSTO"), new Category("JYSK", "JYSK"), new Category("THE BODY SHOP", "THE BODY SHOP")
            , new Category("BRICOSTORE", "BRICOSTORE"), new Category("C & A", "C & A")
            , new Category("Decathlon", List.of("ROUMASPORT SRL", "Decathlon")), new Category("Tabita", "TABITA IMPEX SRL"), new Category("KIK", "KIK 9119 CLUJ")
            , new Category("SECONDTEXTILIASAM", "SECONDTEXTILIASAM")
    );
    private final List<Category> masina = List.of(new Category("Motorina", List.of("OMV", "LUKOIL")), new Category("Rovinieta", "Roviniete"), new Category("Taxa De Pod", "Taxa De Pod")
            , new Category("SAFETY BROKER", "SAFETY BROKER"), new Category("SOS ITP SERVICE", "SOS ITP SERVICE")
            , new Category("MALL DOROBANTILOR SERVICE", "MALL DOROBANTILOR SERVICE"), new Category("MC BUSINESS", "MC BUSINESS")
            , new Category("ATTRIUS DEVELOPMENTS", "ATTRIUS DEVELOPMENTS"), new Category("EURO PARTS DISTRIB", "EURO PARTS DISTRIB"));
    private final List<Category> alte = List.of(new Category("EXCELLENTE SOURCE", "EXCELLENTE SOURCE"), new Category("EUROTRANS SRL", "EUROTRANS SRL")
            , new Category("PAYU", "PAYU"), new Category("IMPRIMERIA NATIONALA", "IMPRIMERIA NATIONALA")
            , new Category("MOTILOR", "MOTILOR"), new Category("WANG FU BUSINESS", "WANG FU BUSINESS"), new Category("ALGO ENTERTAINMENT", "ALGO ENTERTAINMENT")
            , new Category("FUNDATIA PRISON", "FUNDATIA PRISON"), new Category("VELLA MED DISTRICT", "VELLA MED DISTRICT"), new Category("DRM CLUJ", "DRM CLUJ")
            , new Category("HUSE COLORATE", "HUSE COLORATE"), new Category("KIDDYPARK SRL", "KIDDYPARK SRL"), new Category("SC PIATA MARASTI SRL", "SC PIATA MARASTI SRL")
            , new Category("MOBILPAYKASEWEB DISTR", "MOBILPAYKASEWEB DISTR"), new Category("VO CHEF SRL", "VO CHEF SRL")
            , new Category("OTEN V B SRL ARIESULUI", "OTEN V B SRL ARIESULUI"), new Category("CINEMA CITY", "CINEMA CITY ROMANIA")
            , new Category("MACRIDELI FLOWERS SRL", "MACRIDELI FLOWERS SRL"), new Category("LIBRARIA KERIGMA CLU", "LIBRARIA KERIGMA CLU")
            , new Category("NEW IDEA PRINT SRL", "NEW IDEA PRINT SRL"), new Category("FLORI BESTIALE SRL", "FLORI BESTIALE SRL")
            , new Category("EROGLU ROMANIA SRL", "EROGLU ROMANIA SRL"), new Category("RATI INNOVATIONS SRL", "RATI INNOVATIONS SRL"), new Category("PayU*eMAG.ro", "PayU*eMAG.ro")
            , new Category("JULC 60MIN RLX S R L", "JULC 60MIN RLX S R L")
    );
    private final List<Category> restaurant = List.of(new Category("LEMNUL VERDE", "LEMNUL VERDE"), new Category("ASI BAKLAVA", "ASI BAKLAVA")
            , new Category("MOLDOVAN CARMANGERIE", "MOLDOVAN CARMANGERIE"), new Category("HOMS FOOD", "HOMS FOOD")
            , new Category("TARTINE FACTORY SRL", "TARTINE FACTORY SRL"), new Category("OCEANUL PACIFIC", "OCEANUL PACIFIC"), new Category("CARESA CATERING", "CARESA CATERING")
            , new Category("BIANCO MILANO", "BIANCO MILANO"), new Category("ADIADO", "ADIADO"), new Category("MADO CORPORATION", "MADO CORPORATION")
            , new Category("PARFOIS", "PARFOIS"), new Category("Onesti - Marasesti", "Onesti - Marasesti"), new Category("KFC", "KFC")
            , new Category("Hanul cu Peste", "HANUL CU PESTE"), new Category("Marty", "MARTY"), new Category("PEP & PEPPER", "PEP & PEPPER")
            , new Category("Starbucks", "STARBUCKS"), new Category("DASHI", "DASHI")
            , new Category("LC WAIKIKI", "LC WAIKIKI"), new Category("ART OF CAKES SRL", "ART OF CAKES SRL"), new Category("CARIANA ALIMENTAR SRL", "CARIANA ALIMENTAR SRL")
            , new Category("KOPP KAFFE", "KOPP KAFFE"), new Category("MEAT UP", "MEAT UP")
            , new Category("MILENIUM LANDSCAPE DEV", "MILENIUM LANDSCAPE DEV"), new Category("SAVANNAH DRINKS", "SAVANNAH DRINKS"), new Category("SONMARE SRL", "SONMARE SRL")
            , new Category("MARKET TWELVE SRL", "MARKET TWELVE SRL"), new Category("Cantina Bosch", List.of("Eurest Rom SRL Bosch", "Eurest Cantina Bosch"))
            , new Category("JAMON FOOD SRL", "JAMON FOOD SRL"), new Category("ROSA FOOD ART SRL", "ROSA FOOD ART SRL")
            , new Category("MADISONBAGEL", "MADISONBAGEL")
    );

    List<Category> medicamente = List.of(
            new Category("Remedium", "REMEDIUM"),
            new Category("Aldedra", "ALDEDRA"),
            new Category("Farmactiv", "Farmactiv SRL")
    );

    List<Category> igiena = List.of(new Category("ABURIDO SRL", "ABURIDO SRL"), new Category("Promomix", "WWW.PROMOMIX.RO"), new Category("NALA COSMETICS SRL", "NALA COSMETICS SRL")
            , new Category("GERMAN MARKET", "GERMAN MARKET SRL"));
    List<Category> cadouri = List.of(new Category("ANDY EVENTS", "ANDY EVENTS"), new Category("ORANGE SMART STORE CAH", "ORANGE SMART STORE CAH")
            , new Category("EC GARDEN MANAGEMENT", "EC GARDEN MANAGEMENT"));

    private Finder find(List<Category> categories, String name) {
        Optional<Category> category = categories.stream().filter(i -> doFind(name, i.getValues())).findFirst();
        return category.map(value -> new Finder(value.getName(), true)).orElseGet(() -> new Finder("", false));
    }

    private boolean doFind(String name, List<String> values) {
        for (String value : values) {
            return name.contains(value);
        }
        return false;
    }

    private Transaction getSubCategory(String name) {
        Transaction transaction = null;
        Finder finder = find(produseAlimentare, name);
        if (finder.getPresent()) {
            transaction = new Transaction(finder.getName(), "Produse alimentare");
        } else if ((finder = find(casa, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Casa");
        } else if ((finder = find(haine, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Haine");
        } else if ((finder = find(masina, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Masina");
        } else if ((finder = find(alte, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Alte Cheltuieli");
        } else if ((finder = find(medicamente, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Medicamente");
        } else if ((finder = find(igiena, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Igiena");
        } else if ((finder = find(cadouri, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Cadouri");
        } else if ((finder = find(List.of(new Category("CPL Concordia", "CPL Concordia")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Gaz");
        } else if ((finder = find(List.of(new Category("Compania de Apa", "Compania de Apa")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Apa");
        } else if ((finder = find(List.of(new Category("Hidroelectrica", "Hidroelectrica")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Energie Electrica");
        } else if ((finder = find(List.of(new Category("Hotel at Booking.com", "Hotel at Booking.com"), new Category("SUFRO COMPANY SRL", "SUFRO COMPANY SRL")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Concedii");
        } else if ((finder = find(List.of(new Category("TEENCHALLENGECLUJ.ORG", "TEENCHALLENGECLUJ.ORG")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Darnicie");
        } else if ((finder = find(List.of(new Category("CTP", "CTP"), new Category("tpark.ro", "tpark.ro"), new Category("PARKING EXPERTS", "PARKING EXPERTS"), new Category("ATTRIUS DEVELOPMENTS PALAS IASI", "ATTRIUS DEVELOPMENTS PALAS IASI")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Transport");
        } else if ((finder = find(List.of(new Category("WWW.GHISEUL.RO/MFINANT", "WWW.GHISEUL.RO/MFINANT")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Taxe");
        } else if ((finder = find(List.of(new Category("Digi(RCS RDS)", "Digi(RCS RDS)")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Internet");
        } else if ((finder = find(List.of(new Category("Platforma E-BLOC.RO", "Platforma E-BLOC.RO")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Investitii");
        } else if ((finder = find(List.of(new Category("NAPOCA  7", "NAPOCA  7")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Incaltaminte");
        } else if ((finder = find(restaurant, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Restaurant");
        }
        return transaction;
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
            if (val.contains("2023") && val.contains("/02/")) {
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
                Transaction transaction = getSubCategory(item.getName());
                String name = transaction.getName();
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
}
