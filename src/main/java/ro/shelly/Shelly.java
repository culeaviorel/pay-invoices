package ro.shelly;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;

import java.util.ArrayList;
import java.util.List;

public class Shelly {
    private final WebLocator main = new WebLocator().setClasses("main");

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
        Button item = new Button(main, myHome, SearchType.DEEP_CHILD_NODE_OR_SELF);
        item.click();
    }

    public List<Item> collectAllCardsName() {
        List<Item> names = new ArrayList<>();
        WebLocator card = new WebLocator(main).setClasses("advanced-card");
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
            WebLocator contentEl = new WebLocator(panel).setClasses("collapse-content").setChildNodes(deviceIdElChild);
            WebLocator deviceIdEl = new WebLocator(contentEl).setTag("p").setClasses("text-break-all");
            String deviceId = RetryUtils.retry(3, deviceIdEl::getText);
            names.add(new Item(deviceId, name));
        }
        return names;
    }
}
