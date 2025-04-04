package ro.mymoney;

import com.google.common.base.Strings;
import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.form.ComboBox;
import com.sdl.selenium.extjs6.form.DateField;
import com.sdl.selenium.extjs6.form.FieldContainer;
import com.sdl.selenium.extjs6.form.TextField;
import com.sdl.selenium.extjs6.grid.Cell;
import com.sdl.selenium.extjs6.grid.Grid;
import com.sdl.selenium.extjs6.grid.Row;
import com.sdl.selenium.extjs6.window.Window;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

@Getter
@Slf4j
public class View {
    private static final Logger LOGGER = LoggerFactory.getLogger(View.class);

    public View() {
    }

    private final Grid grid = new Grid().setExcludeClasses("x-grid-locked", "x-tree-panel", "x-grid-header-hidden");
    private final Button add = new Button(grid, "Add Transaction");
    private final Window addNewOrEdit = new Window("|Add New|Edit", SearchType.CONTAINS_ANY);
    private final TextField name = new TextField(addNewOrEdit, "Name:");
    private final ComboBox category = new ComboBox(addNewOrEdit, "Category:").setLabelPosition("//following-sibling::*//");
    private final ComboBox subCategory = new ComboBox(addNewOrEdit, "Subcategory:").setLabelPosition("//following-sibling::*//");
    private final DateField dateField = new DateField(addNewOrEdit, "Data:");
    private final TextField sumaField = new TextField(addNewOrEdit, "Price:");
    private final Button saveButton = new Button(addNewOrEdit, "Save");
    private final Button removeButton = new Button(addNewOrEdit, "Remove");
    private final Button leftButton = new Button().setIconCls("fa-chevron-left");

    public boolean addInsert(String denum, String cat, String sub, String sum) {
        add.ready(Duration.ofSeconds(10));
        add.click();
        name.setValue(denum);
        category.select(cat, Duration.ofSeconds(1));
        Utils.sleep(1000);
        subCategory.doSelect(sub, Duration.ofSeconds(1));
        Utils.sleep(500);
        subCategory.select(sub, Duration.ofSeconds(1));
        sumaField.setValue(sum);
        return saveButton.click();
    }

    public boolean addInsert(String denum, String cat, String sub, String data, String sum) {
        return addInsert(denum, cat, sub, data, "dd-MM-yyyy", sum);
    }

    public boolean addInsert(String denum, String cat, String sub, String data, String format, String sum) {
        add.ready(Duration.ofSeconds(10));
        RetryUtils.retry(2, add::click);
        name.setValue(denum);
        RetryUtils.retry(6, () -> category.doSelect(cat, Duration.ofSeconds(2)));
        Utils.sleep(800);
        RetryUtils.retry(6, () -> {
            subCategory.doSelect(sub, Duration.ofSeconds(2));
            return subCategory.getValue().equals(sub);
        });
        boolean select = RetryUtils.retry(2, () -> dateField.select(data, format));
        sumaField.setValue(sum);
        if (select) {
            return saveButton.click();
        } else {
            return false;
        }
    }

    public void addItems(List<Item> items) {
        List<Item> notFoundSubCategory = new ArrayList<>();
        List<Item> isAlreadyExist = new ArrayList<>();
        List<Item> addItems = new ArrayList<>();
        String date1 = items.get(0).getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate d = LocalDate.parse(date1.split(" ")[0], formatter);
        String monthAndYear = d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear();
        boolean inCorrectView = isInCorrectView(monthAndYear);
        if (inCorrectView) {
            getGrid().ready(true);
            for (Item item : items) {
                getGrid().scrollTop();
                LocalDate datetime = LocalDate.parse(item.getDate().split(" ")[0], formatter);
                String date = datetime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
                String sum = getCorrectValue(item.getSum());
                Transaction transaction = getSubCategory(item.getName());
                if (transaction == null) {
                    notFoundSubCategory.add(item);
                } else {
                    String name = transaction.getName();
                    String subCategory = transaction.getSubCategory();
                    Row row = getGrid().getRow(new Cell(1, name), new Cell(3, subCategory), new Cell(4, date), new Cell(5, sum, SearchType.EQUALS));
                    if (!row.scrollInGrid() || !row.waitToRender(Duration.ofMillis(100), false)) {
                        if (Strings.isNullOrEmpty(name)) {
                            notFoundSubCategory.add(item);
                        } else {
                            addItems.add(item);
                            log.info(name);
                            addInsert(name, "Cheltuieli", subCategory, item.getDate(), item.getSum());
                        }
                    } else {
                        isAlreadyExist.add(item);
                    }
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

    public void addItemsTO(List<ItemTO> items) {
        List<ItemTO> notFoundSubCategory = new ArrayList<>();
        List<ItemTO> isAlreadyExist = new ArrayList<>();
        List<ItemTO> addItems = new ArrayList<>();
        String date1 = items.get(0).getData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate d = LocalDate.parse(date1.split(" ")[0], formatter);
        String monthAndYear = d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear();
        boolean inCorrectView = isInCorrectView(monthAndYear);
        if (inCorrectView) {
            getGrid().ready(true);
            for (ItemTO item : items) {
                getGrid().scrollTop();
                LocalDate datetime = LocalDate.parse(item.getData().split(" ")[0], formatter);
                String date = datetime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
                String sum = getCorrectValue(item.getValue());
                Transaction transaction = getSubCategory(item.getName());
                if (transaction == null) {
                    notFoundSubCategory.add(item);
                } else {
                    String name = transaction.getName();
                    String subCategory = transaction.getSubCategory();
                    Row row = getGrid().getRow(new Cell(1, name), new Cell(3, subCategory), new Cell(4, date), new Cell(5, sum, SearchType.EQUALS));
                    if (!row.isPresent()) {
                        if (!row.scrollInGrid() || !row.waitToRender(Duration.ofMillis(100), false)) {
                            if (Strings.isNullOrEmpty(name)) {
                                notFoundSubCategory.add(item);
                            } else {
                                addItems.add(item);
                                log.info(name);
                                addInsert(name, item.getCategory(), subCategory, item.getData(), "dd.MM.yyyy", item.getValue());
                            }
                        } else {
                            isAlreadyExist.add(item);
                        }
                    } else {
                        isAlreadyExist.add(item);
                    }
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

    public boolean isInCorrectView(String monthAndYear) {
        FieldContainer container = new FieldContainer();
        Button button = new Button(container, monthAndYear);
        return RetryUtils.retry(6, () -> {
            boolean ready = button.ready();
            if (!ready) {
                getLeftButton().click();
            }
            return ready;
        });
    }

    public String getCorrectValue(String sum) {
        BigDecimal number = new BigDecimal(sum);
        BigDecimal roundedNumber = number.setScale(1, RoundingMode.HALF_UP);
        DecimalFormat df = new DecimalFormat("#,###.0", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(roundedNumber);
    }

    public Transaction getSubCategory(String name) {
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
        } else if ((finder = find(List.of(new Category("Concordia", "CPL Concordia")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Gaz");
        } else if ((finder = find(List.of(new Category("Compania de Apa", "Compania de Apa")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Apa");
        } else if ((finder = find(List.of(new Category("Hidroelectrica", "Hidroelectrica")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Energie Electrica");
        } else if ((finder = find(concediu, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Concedii");
        } else if ((finder = find(List.of(new Category("TEENCHALLENGECLUJ.ORG", "TEENCHALLENGECLUJ.ORG")
                , new Category("SomethingNew", "ASOCIATIA ORGANIZATIA CR")
        ), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Darnicie");
        } else if ((finder = find(transport, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Transport");
        } else if ((finder = find(taxe, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Taxe");
        } else if ((finder = find(List.of(new Category("Digi(RCS RDS)", "Digi(RCS RDS)")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Internet");
        } else if ((finder = find(List.of(new Category("Platforma E-BLOC.RO", "Platforma E-BLOC.RO")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Investitii");
        } else if ((finder = find(List.of(new Category("NAPOCA  7", "NAPOCA  7")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Incaltaminte");
        } else if ((finder = find(tratament, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Tratament");
        } else if ((finder = find(restaurant, name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Restaurant");
        } else if ((finder = find(List.of(new Category("Bonuri de masa", "SDL LANGUAGE WEAVER SRL")), name)).getPresent()) {
            transaction = new Transaction(finder.getName(), "Bonuri de masa");
        }
        return transaction;
    }

    private final List<Category> taxe = List.of(new Category("WWW.GHISEUL.RO", "WWW.GHISEUL.RO/MFINANT")
            , new Category("Impozit pe casa", "COMUNA APAHIDA")
            , new Category("ANAF", "DIRECTIA GEN A FINANTE")
    );

    private final List<Category> casa = List.of(new Category("Hornbach", "HORNBACH"), new Category("Leroy Merlin", "LEROY MERLIN")
            , new Category("Dedeman", "DEDEMAN"), new Category("Altex", "ALTEX ROMANIA")
            , new Category("MAXIMUM ELECTRONIC", "MAXIMUM ELECTRONIC"), new Category("Pragmatic", "SC PRAGMATIC TCV SRL")
            , new Category("wifistore", "EPwifistore.ro"), new Category("INTERNATIONAL PAPER BUSI", "INTERNATIONAL PAPER BUSI")
            , new Category("KSA", "KSA"), new Category("Moemax", "MOEMAX Cluj")
            , new Category("Jumbo", List.of("JUMBO ORADEA C8", "JUMBO CONSTANTA")), new Category("semintegazon", "mpy*semintegazon")
            , new Category("Smart Home", "SMART HOME 360"), new Category("GradinaMax", "GradinaMax")
            , new Category("bioculturi", "mpy*bioculturi"), new Category("vexio.ro", "PayU*vexio.ro")
            , new Category("AGRO TOTAL", "AGRO TOTAL EXPRES SRL"), new Category("Shelly", List.of("ALLTERCOROB", "Allterco Robotics"))
            , new Category("Asigurare Casa", "INTER BROKER DE ASIG"), new Category("CLEANEXPERT", "CLEANEXPERT SHOP SRL")
            , new Category("SALICE", "SALICE COMPROD"), new Category("Remarcabil", "EUROTRANS SRL")
            , new Category("gardencentrum", "*EPgardencentrum.net"), new Category("Magazinul Gospodarului", "Magazinul Gospodarului")
            , new Category("Aliexpress", "aliexpress"), new Category("TIRANA", "TIRANA SEDIU CENTRAL")
            , new Category("NIGE", "SC NIGE IMPEX SRL"), new Category("MAFCOM", "MAFCOM PROD IMPEX SRL")
            , new Category("TAE ELECTRIC", "TAE ELECTRIC DISTRIB"), new Category("JIEDUOBEKU4", "*PAYPAL JIEDUOBEKU4")
            , new Category("Ventilatie", "DYNAMIC PARCEL DISTRIB"), new Category("Tindie", "TINDIE.COM")
            , new Category("Temu", "Temu.com"), new Category("CarteFunciara", "ANCPI NETOPIA")
            , new Category("AtuTech", "ATU TECH SRL")
            , new Category("Saimon Electronics", "SAIMON ELECTRONICS")
    );
    private final List<Category> produseAlimentare = List.of(new Category("Lidl", List.of("Lidl", "LIDL")), new Category("Dedeman", "DEDEMAN")
            , new Category("Auchan", List.of("AUCHAN", "Auchan Cluj", "AUC 0004 CLUJ", "AUC 0037 CJ IR")), new Category("Penny", "PENNY"), new Category("Kaufland", "KAUFLAND")
            , new Category("Kaufland", "Kaufland"), new Category("Mega Image", List.of("MEGAIMAGE", "MEGA IMAGE")), new Category("Bonas", "BONAS")
            , new Category("La Vestar", "LA VESTAR"), new Category("BUCURCRISS", "BUCURCRISS")
            , new Category("Profi", "PROFI"), new Category("CICMAR", "CICMAR"), new Category("VARGA", "VARGA")
            , new Category("Flavianda", "FLAVIANDA CRISAN"), new Category("Agropan", "AGROPAN PRODCOM"), new Category("TIENDA FRUTAS", "TIENDA FRUTAS")
            , new Category("PREMIO DISTRIBUTION", "PREMIO DISTRIBUTION"), new Category("Premier Restaurants", "PREMIER RESTAURANTS")
            , new Category("Panemar", "PANEMAR"), new Category("MCFLYING SRL", "MCFLYING SRL")
            , new Category("Carrefour express", "ARTIMA SA"), new Category("MAGAZIN LA 2 PASI", "MAGAZIN LA 2 PASI")
            , new Category("INM KFL CLUJ FAB C1", "INM KFL CLUJ FAB C1"), new Category("SC OPREA AVI COM SRL", "SC OPREA AVI COM SRL")
            , new Category("ANAMIR BIOMARKET", List.of("ANAMIR BIOMARKET SRL", "ANAMIR BIOMARKET")), new Category("MAVIOS IMPEX SRL", "MAVIOS IMPEX SRL")
            , new Category("Carrefour", "CARREFOUR"), new Category("Linela", "Linela"), new Category("Selgros", "SELGROS")
            , new Category("CARMIC IMPEX", "CARMIC IMPEX"), new Category("BODRUM DONER MARASTI", "BODRUM DONER MARASTI")
            , new Category("RODIMEX INVEST", "RODIMEX INVEST"), new Category("MELFRUCTUS", "MELFRUCTUS SRL")
            , new Category("ADARIA SERV SRL", "ADARIA SERV SRL"), new Category("Ergon", "ERGON")
            , new Category("Rebeca Fruct", "REBECA FRUCT SRL"), new Category("Ferma Steluta", "FERMA STELUTA SRL")
            , new Category("EURO MARKET", "EURO MARKET"), new Category("INMEDIO", "INMEDIO")
            , new Category("MagazinGradina", "MAGAZIN CLUJ AUREL VLA"), new Category("PULSAR", "PULSAR TEO SRL")
            , new Category("CBA", "CBA NORD VEST SRL"), new Category("Pastravaria", "PASTRAVARIA INCDS GILA")
            , new Category("SHOP&GO", "SHOP&GO"), new Category("Mavios", "MAVIOS IMPEX")
            , new Category("Oncos", "SC ONCOS TRANSILVANIA"), new Category("Piccolino", "PICCOLINO CAFFE SRL")
            , new Category("SERVUS", "SERVUS"), new Category("MEMO 10", "MEMO 10")
    );
    private final List<Category> haine = List.of(new Category("ZARA", "ZARA"), new Category("H&M", "H&M"), new Category("Pepco", "PEPCO")
            , new Category("ORGANIZATIA CRESTINA", "ORGANIZATIA CRESTINA"), new Category("KiK", "KiK Textilien")
            , new Category("LANELKA", "LANELKA"), new Category("MELI MELO", "MELI MELO"), new Category("Sinsay", List.of("SINSAY", "Sinsay"))
            , new Category("REGALALIMENTNONSTO", "REGALALIMENTNONSTO"), new Category("JYSK", "JYSK"), new Category("THE BODY SHOP", "THE BODY SHOP")
            , new Category("BricoStore", "BRICOSTORE"), new Category("C&A", "C & A")
            , new Category("Decathlon", List.of("ROUMASPORT SRL", "Decathlon", "DECATHLON CLUJ")), new Category("Tabita", "TABITA IMPEX SRL")
            , new Category("KIK", "KIK 9119 CLUJ"), new Category("SECONDTEXTILIASAM", "SECONDTEXTILIASAM")
            , new Category("Reserved", "RESERVED"), new Category("METASAN", "METASAN RUBY ROSE")
            , new Category("BRUUJ", "BRUUJ SRL"), new Category("Deichmann", "Deichmann Cluj 037")
            , new Category("JURBAKA", "JURBAKA FASHION SRL")
            , new Category("NEW YORKER", "NEW YORKER CLUJ")
    );
    private final List<Category> masina = List.of(new Category("Motorina", List.of("OMV", "LUKOIL", "ROMPETROL"))
            , new Category("Rovinieta", "Roviniete"), new Category("Taxa De Pod", "Taxa De Pod")
            , new Category("EPiesa", List.of("EURO PARTS DISTRIB")), new Category("SAFETY BROKER", "SAFETY BROKER")
            , new Category("SOS ITP SERVICE", "SOS ITP SERVICE"), new Category("MALL DOROBANTILOR", List.of("MALL DOROBANTILOR SERVICE", "ITP DOROBANTILOR SRL"))
            , new Category("MC BUSINESS", "MC BUSINESS"), new Category("ATTRIUS DEVELOPMENTS", "ATTRIUS DEVELOPMENTS")
            , new Category("Vigneta", List.of("Pago*Vignette", "Pago*Timesafe")), new Category("Parcare Iulius", "MOBILPAYYEPARKING")
            , new Category("Parcare", "*MPYYEPARKING SOLUTIO")
            , new Category("ITP", "WIGSTEIN SRL")
    );
    private final List<Category> alte = List.of(new Category("EXCELLENTE SOURCE", "EXCELLENTE SOURCE")
            , new Category("PAYU", "PAYU"), new Category("Pasapoarte", "IMPRIMERIA NATIONALA")
            , new Category("MOTILOR", "MOTILOR"), new Category("WANG FU BUSINESS", "WANG FU BUSINESS")
            , new Category("FUNDATIA PRISON", "FUNDATIA PRISON"), new Category("VELLA MED DISTRICT", "VELLA MED DISTRICT")
            , new Category("HUSE COLORATE", "HUSE COLORATE"), new Category("KIDDYPARK", "KIDDYPARK SRL")
            , new Category("SC PIATA MARASTI SRL", "SC PIATA MARASTI SRL"), new Category("DRM CLUJ", "DRM CLUJ")
            , new Category("MOBILPAYKASEWEB DISTR", "MOBILPAYKASEWEB DISTR"), new Category("VO CHEF", "VO CHEF SRL")
            , new Category("OTEN V B", "OTEN V B SRL ARIESULUI"), new Category("CINEMA CITY", "CINEMA CITY ROMANIA")
            , new Category("FLOWERS", "MACRIDELI FLOWERS SRL"), new Category("LIBRARIA KERIGMA", "LIBRARIA KERIGMA CLU")
            , new Category("NEW IDEA PRINT", "NEW IDEA PRINT SRL"), new Category("FLORI", "FLORI BESTIALE SRL")
            , new Category("EROGLU", "EROGLU ROMANIA SRL"), new Category("RATI INNOVATIONS", "RATI INNOVATIONS SRL")
            , new Category("PayU*eMAG.ro", "PayU*eMAG.ro"), new Category("ALGO ENTERTAINMENT", "ALGO ENTERTAINMENT")
            , new Category("JULC 60MIN", "JULC 60MIN RLX S R L"), new Category("Meron", "MERON PLATINIA")
            , new Category("KINDERCITY", "KINDERCITY S R L"), new Category("FACULTAS PLUS", "FACULTAS PLUS SRL")
            , new Category("ALDISANA INVEST", "ALDISANA INVEST SRL"), new Category("XUYIDA MARKET", "XUYIDA MARKET STORE SR")
            , new Category("MOMO", "MOMO INTERNATIONAL S R L"), new Category("RENELA", "RENELA")
            , new Category("MUZEUL ETNO", "MUZEUL ETNO AL TRANSILVANIEI"), new Category("ADYSYM IMPEX", "ADYSYM IMPEX SRL")
            , new Category("GOLDEN AGE PRODUCTION", "GOLDEN AGE PRODUCTION SR"), new Category("MOPS", "MOPS ROMANIA")
            , new Category("LITERA.RO", "*PlatiOnLITERA.RO"), new Category("thefastshop.eu", "*EPthefastshop.eu")
            , new Category("MAGAZIN WEST", "MAGAZIN WEST"), new Category("S F COMPANY", "S F COMPANY SRL")
            , new Category("DIRECT CLIENT SERVICES", "DIRECT CLIENT SERVICES"), new Category("MOLDOVAN EVENTS", "MOLDOVAN EVENTS")
            , new Category("CASUTACUCUTII", "CASUTACUCUTII S R L"), new Category("Cinema City", "*cinemacity.ro")
            , new Category("GIFTBOXART", "GIFTBOXART SRL"), new Category("CROMA MOB", "CROMA MOB SRL")
            , new Category("PRINK", "PRINK ROMANIA SRL"), new Category("RABANC", "RABANC SRL")
            , new Category("TEAM MOBILE", "TEAM MOBILE ONLINE SRL"), new Category("KRAFTCHAIN", "KRAFTCHAIN ENTERPRISES")
            , new Category("DAEF ONLINE", "DAEF ONLINE SRL"), new Category("CRAZY BANANA", "CRAZY BANANA SRL")
            , new Category("PILWAX", "PILWAX COMIMPEX SRLL"), new Category("DAMADRIS", "DAMADRIS SEKART")
            , new Category("ADYSYM", "ADYSYM IMPEX"), new Category("COUNTRYSIDE", "COUNTRYSIDE HOME SRL")
            , new Category("Carturesti", "CARTURESTI IULIUS MA")
    );
    private final List<Category> restaurant = List.of(new Category("Lemnul Verde", "LEMNUL VERDE"), new Category("ASI BAKLAVA", "ASI BAKLAVA")
            , new Category("Moldovan", List.of("MOLDOVAN CARMANGERIE", "MOLDOVAN FAMILY BUSINESS")), new Category("HOMS FOOD", "HOMS FOOD")
            , new Category("Tartine", "TARTINE FACTORY SRL"), new Category("Stradale", "OCEANUL PACIFIC")
            , new Category("CARESA CATERING", "CARESA CATERING"), new Category("VARZARIE", "VARZARIE ALIMENTATIE PUBLICA SR")
            , new Category("Bianco Milano", "BIANCO MILANO"), new Category("ADIADO", "ADIADO"), new Category("MADO", List.of("MADO CORPORATION", "MADO FAST FOOD"))
            , new Category("PARFOIS", "PARFOIS"), new Category("Onesti - Marasesti", "Onesti - Marasesti"), new Category("KFC", "KFC")
            , new Category("Hanul cu Peste", "HANUL CU PESTE"), new Category("Marty", "MARTY"), new Category("PEP & PEPPER", "PEP & PEPPER")
            , new Category("Starbucks", "STARBUCKS"), new Category("Dashi", "DASHI")
            , new Category("LC WAIKIKI", "LC WAIKIKI"), new Category("ART OF CAKES", "ART OF CAKES SRL")
            , new Category("CARIANA ALIMENTAR", "CARIANA ALIMENTAR SRL"), new Category("KOPP KAFFE", "KOPP KAFFE")
            , new Category("MEAT UP", "MEAT UP"), new Category("MILENIUM LANDSCAPE DEV", "MILENIUM LANDSCAPE DEV")
            , new Category("SAVANNAH DRINKS", "SAVANNAH DRINKS"), new Category("SONMARE SRL", "SONMARE SRL")
            , new Category("Twelve", "MARKET TWELVE SRL"), new Category("Cantina Bosch", List.of("Eurest Rom SRL Bosch", "Eurest Cantina Bosch"))
            , new Category("JAMON FOOD", "JAMON FOOD SRL"), new Category("Rosa", List.of("ROSA FOOD ART SRL", "Rosa"))
            , new Category("MADISONBAGEL", "MADISONBAGEL"), new Category("Pizza Big Belly", List.of("bigbelly-cluj", "RES QUALITY FOOD"))
            , new Category("DONUTERIE", "DONUTERIE OPERATIONAL SR"), new Category("Lunch Box", "LUNCH BOX SRL")
            , new Category("MST BUBBLE", "MST BUBBLE CONCEPT SRL"), new Category("Meron", List.of("MERON POLUS", "MERON 2"))
            , new Category("LA CASA RISTORANTE", "LA CASA RISTORANTE"), new Category("Agape", "HOTEL AGAPE - AUTOSERV")
            , new Category("ELIXIPLANTA", "ELIXIPLANTA SRL"), new Category("Piata9", "HONEST FOOD SRL")
            , new Category("JUHANIO", "JUHANIO S.R.L."), new Category("SPARTAN", "SPARTAN ALEXANDRU VAID")
            , new Category("NOODLE PACK", "NOODLE PACK"), new Category("RECEPTIE PENSIUNE", "RECEPTIE PENSIUNE")
            , new Category("LAPROLEMN SRL", "LAPROLEMN SRL"), new Category("GIGI", "GIGI CLUJ")
            , new Category("Moara de Vant", List.of("BUCATARIA LUMII SRL", "GERROM THERMOHAUS SRL"))
            , new Category("Inghetata", "CREMERIA EMILIA SRL"), new Category("A la Tarte", "DELITART SRL-D")
            , new Category("Gustino", "GUSTINO SERV SRL"), new Category("AMZA", "AMZA PROD SRL")
            , new Category("Shaorma", "ACAPULCO FOOD LOUNGE SRL"), new Category("IRIS DELICE", "IRIS DELICE")
            , new Category("COFFEE CUP ROASTERS", "COFFEE CUP ROASTERS SRL"), new Category("SalatBox", "BIO BOX SRL")
            , new Category("TERASA JANKA", "TERASA JANKA SRL"), new Category("CARTOFISSERIE", "CARTOFISSERIE IULIUS M")
            , new Category("CHOPSTIX", "CHOPSTIX IULIUS CL"), new Category("MORITZ", "MORITZ EIS SRL CLUJ NA")
            , new Category("CARTOFISSERIE", "CARTOFISSERIE VIVO CLU"), new Category("MCDonalds", List.of("MCDONALD S", "MCD 52 CLUJ POLUS"))
            , new Category("Cafea", "JOAYOKANU COFFEE SRL"), new Category("Restaurant Continental", "RESTAURANT CONTINENTAL HOTELS")
            , new Category("Poco Loco", "POCO LOCO CITY"), new Category("Taco Bueno", "TACO BUENO")
            , new Category("KOVACS", "KOVACS"), new Category("VKUSNO CONCEPT", "VKUSNO CONCEPT")
            , new Category("COOKOUT", "COOKOUT S R L")
    );

    List<Category> medicamente = List.of(
            new Category("Remedium", "REMEDIUM"), new Category("Aldedra", "ALDEDRA")
            , new Category("ELMAFARM", "ELMAFARM SRL"), new Category("Farmactiv", "Farmactiv SRL")
            , new Category("Ducfarm", "DUCFARM SRL"), new Category("FARMACIA TOMA", "SC FARMACIA TOMA")
            , new Category("VITAFARM", "VITAFARM PLUS SRL"), new Category("AMBROSIA", "AMBROSIA FARM 1")
            , new Category("Catena", List.of("CATENA CLUJ", "CATENA FARMACIE - CLUJ"))
    );

    List<Category> igiena = List.of(new Category("ABURIDO", "ABURIDO SRL"), new Category("Promomix", "WWW.PROMOMIX.RO")
            , new Category("NALA COSMETICS SRL", "NALA COSMETICS SRL"), new Category("German Market", "GERMAN MARKET SRL")
            , new Category("MOBILPAYLADYBUG INVES", "MOBILPAYLADYBUG INVES")
            , new Category("Escapade World", "*EPshop.escapade.world")
            , new Category("Sephora", "Sephora")
    );
    List<Category> cadouri = List.of(new Category("ANDY EVENTS", "ANDY EVENTS"), new Category("ORANGE SMART STORE", "ORANGE SMART STORE CAH")
            , new Category("EC GARDEN MANAGEMENT", "EC GARDEN MANAGEMENT"), new Category("Flori", List.of("FREYA FLOWERS DESIGN SRL", "FLORARIA NOLINA"))
            , new Category("Noriel", "NORIEL TOYS IULIUS MAL"), new Category("BOB CRISTINA", "BOB CRISTINA MARIA INTRE")
            , new Category("Florarie", "ILCA LAURA II FLORARIE"), new Category("DECATHLON", "DECATHLON BRASOV LN")
            , new Category("Castelul Piticilor", "CASTELUL PITICILOR SRL")
    );

    List<Category> concediu = List.of(new Category("Hotel", "Hotel at Booking.com"), new Category("SUFRO COMPANY", "SUFRO COMPANY SRL")
            , new Category("Avion", "WIZZ"), new Category("Cafea", "AMBROCAFE SRL")
            , new Category("Pizza", "PIZZERIA CAPUTO"), new Category("Duomo", "DUOMO CORDUSIO")
            , new Category("Coop", "Coop"), new Category("Tren", "FFS Stazione Lugano")
            , new Category("Manor", "Manor AG"), new Category("CON.FID", "CON.FID. STATION SRL")
            , new Category("Lugano", "DIF SPA AGENZIA DIFFUS"), new Category("Hotel Maxim", "HOTEL MAXIM 2")
            , new Category("Austria", "Eni 4044"), new Category("MINI TRANSYLVANIA", "MINI TRANSYLVANIA")
            , new Category("ASOC CULTURALA", "ASOC CULTURALA VISUS"), new Category("CRINABEL", "CRINABEL")
            , new Category("VinietaMD", "www.mpay.gov.md"), new Category("Bucuria", "MAIB BUCURIA")
            , new Category("Marisel", "HOSUS TURISM SRL"), new Category("BOOKING", "BOOKING.COM")
            , new Category("RESTAURANT IDOL", "RESTAURANT IDOL 2"), new Category("GHESZEL", "GHESZEL SRL")
            , new Category("GRADINA ZOOLOGICA", "GRADINA ZOOLOGICA BRAS"), new Category("MAMAIA", "MAMAIA LAC")
            , new Category("OXFIX A DOI MAMAIA", "OXFIX A DOI MAMAIA"), new Category("ROXMAR", "ROXMAR SRL")
            , new Category("DELTAROM", "DELTAROM PECO 2")
    );

    List<Category> transport = List.of(new Category("CTP", "CTP"), new Category("tpark.ro", "tpark.ro")
            , new Category("PARKING EXPERTS", "PARKING EXPERTS"), new Category("Parcare", "ATTRIUS DEVELOPMENTS PALAS IASI")
    );

    List<Category> tratament = List.of(new Category("Radiologie", "CENTRU DE RADIOLOGIE DIG")
            , new Category("Stomatologie", "STOMPRAX MEDICA SRL"), new Category("Stomatologie", "REJOICE DENT S R L")
            , new Category("Stomatologie", "DENTAL CHIQUE CLINIQUE"), new Category("Stomatologie", "CLINICA DENTARA CLUJ")
            , new Category("OptiBlu", "OPTICAL INVESTMENT GROUP")
    );

    private Finder find(List<Category> categories, String name) {
        Optional<Category> category = categories.stream().filter(i -> doFind(name, i.getValues())).findFirst();
        return category.map(value -> new Finder(value.getName(), true)).orElseGet(() -> new Finder("", false));
    }

    private boolean doFind(String name, List<String> values) {
        for (String value : values) {
//            log.info("name:{}=>value:{}", name, value);
            boolean contains = name.contains(value);
            if (contains) {
                return true;
            }
        }
        return false;
    }
}
