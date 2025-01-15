package ro.btgo;

import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.form.TextField;

public class Card extends WebLocator {

    public Card() {
        setClasses("card");
    }

    public Card(WebLocator container) {
        this();
        setContainer(container);
    }

    public Card(WebLocator container, String title) {
        this(container);
        WebLocator titleEl = new WebLocator().setText(title);
        setChildNodes(titleEl);
    }

    public boolean isSource() {
        WebLocator parent = new WebLocator(this).setRoot("/parent::");
        String attribute = parent.getAttribute("accounttype");
        return attribute.equals("SOURCE");
    }

    public boolean setValue(String value) {
        TextField textField = new TextField(this);
        return textField.setValue(value);
    }
}
