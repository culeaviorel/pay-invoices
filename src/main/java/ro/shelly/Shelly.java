package ro.shelly;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Shelly {
    private final WebLocator main = new WebLocator().setClasses("main");
    private final WebLocator card = new WebLocator(main).setClasses("advanced-card");

    public void login(String email, String password) {
        WebLocator loginParent = new WebLocator().setClasses("login-layout-wrapper");
        TextField emailEl = new TextField(loginParent).setPlaceholder("Enter your e-mail");
        TextField passwordEl = new TextField(loginParent).setPlaceholder("Enter your password");
        Button signIn = new Button(loginParent, "Sign in", SearchType.DEEP_CHILD_NODE_OR_SELF);
        emailEl.setValue(email);
        passwordEl.setValue(password);
        signIn.click();
    }

    public void openTab(String myHome) {
        main.ready(Duration.ofSeconds(40));
        Button item = new Button(main, myHome, SearchType.DEEP_CHILD_NODE_OR_SELF);
        RetryUtils.retry(2, item::click);
    }

    public List<Item> collectAllCardsName() {
        List<Item> names = new ArrayList<>();
        int size = card.size();
        for (int i = 1; i <= size; i++) {
            card.setResultIdx(i);
            card.click();
            WebLocator panel = new WebLocator().setClasses("drawer", "open");
            WebLocator nameEl = new WebLocator(panel).setTag("p").setAttribute("slot", "heading");
            String name = RetryUtils.retry(3, nameEl::getText);
            Button item = new Button(panel).setIconCls("fa-cog");
            item.click();
            WebLocator deviceInformation = new WebLocator(panel).setClasses("mb-s", "collapse").setText("Device information", SearchType.DEEP_CHILD_NODE_OR_SELF);
            deviceInformation.click();
            WebLocator deviceIdElChild = new WebLocator().setTag("p").setText("device id");
            WebLocator contentDeviceIdEl = new WebLocator(panel).setClasses("d-flex", "flex-wrap").setChildNodes(deviceIdElChild);
            WebLocator deviceIdEl = new WebLocator(contentDeviceIdEl).setTag("p").setClasses("text-break-all");
            String deviceId = RetryUtils.retry(3, deviceIdEl::getText);

            WebLocator deviceIPElChild = new WebLocator().setTag("p").setText("device IP");
            WebLocator contentDeviceIPEl = new WebLocator(panel).setClasses("d-flex", "flex-wrap").setChildNodes(deviceIPElChild);
            WebLocator deviceIPEl = new WebLocator(contentDeviceIPEl).setTag("p").setClasses("text-break-all");
            String ip = RetryUtils.retry(3, deviceIPEl::getText);
            names.add(new Item(deviceId, name, ip));
        }
        return names;
    }

    public void saveIP(Item device, String wifi, String password) {
        TextField searchEl = new TextField().setPlaceholder("search");
        searchEl.setValue(device.name());
        WebLocator titleEl = new WebLocator().setClasses("title-container").setText(device.name(), SearchType.EQUALS, SearchType.DEEP_CHILD_NODE_OR_SELF);
        card.setChildNodes(titleEl);
        RetryUtils.retry(3, card::click);

        WebLocator panel = new WebLocator().setClasses("drawer", "open");
        WebLocator el = new WebLocator(panel).setClasses("controls-container");
        String offline = el.getText();
        if (!offline.contains("Device is offline")) {
            Button globe = new Button(panel).setIconCls("fa-globe");
            globe.click();
            WebLocator tabContent = new WebLocator(panel).setClasses("tab-content", "have-tabs");
            WebLocator wifiEl = new WebLocator(tabContent).setClasses("mb-s").setText("Wi-Fi 1", SearchType.DEEP_CHILD_NODE_OR_SELF);
            wifiEl.click();
            WebLocator content = new WebLocator(panel).setClasses("collapse-content");
            TextField ipEl = new TextField(content).setPlaceholder("IP address");
            if (!ipEl.isPresent()) {
                Utils.sleep(1);
            }
            String actualIp = RetryUtils.retry(2, ipEl::getValue);
            if (!actualIp.equals(device.ip())) {
                ipEl.setValue(device.ip());
                TextField passEl = new TextField(content).setPlaceholder("Password");
                passEl.setValue(password);
                Button save = new Button(content, "Save", SearchType.DEEP_CHILD_NODE_OR_SELF);
                save.click();
                Utils.sleep(1000);
                WebLocator modal = new WebLocator().setClasses("modal-container");
                Button confirm = new Button(modal, "Confirm", SearchType.DEEP_CHILD_NODE_OR_SELF);
                RetryUtils.retry(2, confirm::click);
                Utils.sleep(1000);
            }
        }
    }
}
