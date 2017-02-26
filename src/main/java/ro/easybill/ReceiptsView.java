package ro.easybill;

import com.sdl.selenium.bootstrap.button.Button;
import com.sdl.selenium.bootstrap.form.Form;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.ComboBox;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;

public class ReceiptsView extends Form {

    public ReceiptsView() {
        setId("genereazaChitanta");
    }

    private WebLink chitanteSiPlatiLink = new WebLink().setTitle("Chitante si plati");
    private Button emiteButton = new Button().setText("Emite chitanta");
    private TextField periodEl = new TextField().setId("rangepicker");
    private TextField nameEl = new TextField(this).setName("firma");
    private TextField dataEl = new TextField(this).setName("data_chit");
    private TextField addressEl = new TextField(this).setName("adresa");
    private TextField amountEl = new TextField(this).setName("suma");
    private ComboBox currencyEl = new ComboBox(this).setName("defaultCurrency");
    private TextField typeEl = new TextField(this).setName("reprezentand");
    private InputButton submitButton = new InputButton(this).setName("Submit2");

    private void setCustomer(Customers customer) {
        ready();
        nameEl.setValue(customer.getName());
        dataEl.setValue(customer.getDate());
        nameEl.click();
        addressEl.setValue(customer.getAddress());
        amountEl.setValue(customer.getAmount());
        currencyEl.select(customer.getType());
        typeEl.setValue(customer.getReprezentand());
        submitButton.click();
        WebDriverConfig.switchToLastTab();
        WebDriverConfig.getDriver().close();
        WebDriverConfig.switchToFirstTab();
    }

    public void generateReceiptsForNew(Customers customer) {
        chitanteSiPlatiLink.ready(10);
        chitanteSiPlatiLink.click();
        periodEl.ready();
        emiteButton.click();
        setCustomer(customer);
    }
}
