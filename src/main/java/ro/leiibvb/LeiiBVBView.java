package ro.leiibvb;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.CheckBox;
import com.sdl.selenium.web.form.ComboBox;
import com.sdl.selenium.web.form.MultipleSelect;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.jira.Tasks;

import java.util.List;

public class LeiiBVBView extends WebLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeiiBVBView.class);

    public LeiiBVBView() {
        setId("index");
    }

    private WebLocator text = new WebLocator().setText("Foloseste codul", SearchType.DEEP_CHILD_NODE_OR_SELF);
    private WebLocator container = new WebLocator().setClasses("cbel", "cb-text", "h5-text").setChildNodes(text).setVisibility(true);
//    private WebLink close = new WebLink().setAttribute("onclick", "setPageState(\"hidepopup\");");

    private TextField prenume = new TextField(this).setName("Prenume*");
    private TextField name = new TextField(this).setName("Nume*");
    private TextField email = new TextField(this).setName("Email*");
    private TextField phone = new TextField(this).setName("Telefon*");
    private TextField data = new TextField(this).setName("Data tranzactiei*");
    private TextField hour = new TextField(this).setName("Ora tranzactiei*");
    private TextField symbol = new TextField(this).setName("Simbolul instrumentului financiar tranzactionat*");
    private TextField amount = new TextField(this).setName("Valoarea tranzactiei*");
    private ComboBox broker = new ComboBox(this).setName("Brokerul dumneavoastra*");
    private CheckBox acord1 = new CheckBox(this).setName("Sunt de acord cu prelucrarea si stocarea datelor mele cu caracter personal in vederea desemnarii castigatorilor si acordarii premiilor de catre BVB");
    private CheckBox acord2 = new CheckBox(this).setName("Sunt de acord cu introducerea datelor mele in baza de investitori individuali a BVB si sa primesc notificari despre tranzactiile de pe piata de capital");

    private InputButton submit = new InputButton(this, "Submit");

    public void addTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            WebDriverConfig.getDriver().get("http://www.leiibvb.ro");
            container.ready();
            ready();
            WebLocatorUtils.scrollToWebLocator(prenume);
            prenume.setValue(transaction.getPrenume());
            name.setValue(transaction.getNume());
            email.setValue(transaction.getEmail());
            phone.setValue(transaction.getPhone());
            data.setValue(transaction.getData());
            hour.setValue(transaction.getHour());
            symbol.setValue(transaction.getSymbol());
            amount.setValue(transaction.getAmount());
            broker.select(transaction.getBroker());
            acord1.click();
            acord2.click();
            submit.click();
        }
    }
}
