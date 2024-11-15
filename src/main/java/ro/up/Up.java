package ro.up;

import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import ro.mymoney.ItemTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class Up {
    private final WebLocator app = new WebLocator().setId("app");

    public void login(String id, String password) {
        WebLink utilizatorTab = new WebLink().setClasses("cardusersTab");
        utilizatorTab.click();
        WebLocator loginForm = new WebLocator().setClasses("loginForm");
        TextField emailEl = new TextField(loginForm).setPlaceholder("E-mail");
        TextField passwordEl = new TextField(loginForm).setPlaceholder("Parola");
        InputButton logIn = new InputButton().setClasses("submitCompanies");
        InputButton confirma = new InputButton(null, "CONFIRMA");
        emailEl.ready(Duration.ofSeconds(40));
        RetryUtils.retry(40, () -> emailEl.setValue(id));
        passwordEl.setValue(password);
        logIn.click();
        acceptAll();
        Utils.sleep(1); //wait for cod OTP
        confirma.click();
        WebLocator popup = new WebLocator().setId("modal_popup_new_tc");
        WebLink close = new WebLink(popup).setClasses("close-button");
        close.click();
        WebLocator popup2 = new WebLocator().setId("modal_popup_welcome2");
        WebLink close2 = new WebLink(popup2).setClasses("close-button");
        close2.doClick();
    }

    private void acceptAll() {
        WebLink acceptAllButton = new WebLink(app, "ACCEPTA");
        acceptAllButton.doClick();
    }

    public List<ItemTO> collectData() {
        WebLocator picker = new WebLocator().setClasses("vue-daterange-picker");
        picker.click();
        WebLocator popUp = new WebLocator().setClasses("daterangepicker");
        WebLocator ultimele3Luni = new WebLocator(popUp).setAttribute("data-range-key", "Luna trecuta");
        ultimele3Luni.click();
        WebLocator apply = new WebLocator(popUp).setClasses("applyBtn");
        apply.click();
        Utils.sleep(2000);
        WebLocator transactions = new WebLocator().setClasses("transaction");
        int size = transactions.size();
        List<ItemTO> items = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            transactions.setResultIdx(i);
            WebLocator name = new WebLocator(transactions).setClasses("transaction-name");
            WebLocator date = new WebLocator(transactions).setClasses("transaction-date");
            WebLocator suma = new WebLocator(transactions).setClasses("amount-bold");
            WebLocator rejected = new WebLocator(transactions).setClasses("rejected");
            if (!rejected.isPresent()) {
                String dateText = date.getText().split(" ")[0];
                String nameText = name.getText();
                String sumaText = suma.getText().split(" ")[0].replaceAll("-", "").replaceAll(",", ".");
                if (sumaText.contains("+")) {
                    sumaText = sumaText.replace("+", "");
                    items.add(new ItemTO("Venituri", "Bonuri de masa", nameText, dateText, sumaText));
                } else {
                    items.add(new ItemTO("Cheltuieli", "Produse alimentare", nameText, dateText, sumaText));
                }
            }
        }
        return items;
    }
}
