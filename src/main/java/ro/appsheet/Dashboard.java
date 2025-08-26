package ro.appsheet;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Dashboard {
    public void addItem(ItemRecord item) {
        WebLocator addButton = new WebLocator().setTag("button").setText("Add", SearchType.DEEP_CHILD_NODE_OR_SELF);
        addButton.click();
        WebLocator header = new WebLocator().setAttribute("role", "heading").setText("FilteredData");
        WebLocator window = new WebLocator().setAttribute("role", "presentation").setVisibility(true).setChildNodes(header);
        WebLocator nameField = new WebLocator(window).setAttribute("aria-label", "Name");
        nameField.sendKeys(item.name());
        WebLocator categoryField = new WebLocator(window).setClasses("ButtonSelectButton").setText(item.category(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        categoryField.click();
        WebLocator subCategorieComboBox = new WebLocator(window).setAttribute("aria-label", "SubCategorie");
        subCategorieComboBox.click();
        WebLocator options = new WebLocator().setAttribute("role", "presentation").setAttribute("x-placement", "top");
        WebLocator option = new WebLocator(options).setTag("li").setText(item.subcategory(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        option.click();
        WebLocator priceField = new WebLocator(window).setAttribute("aria-label", "Suma");
        priceField.sendKeys(item.price());
        WebLocator paymentField = new WebLocator(window).setClasses("ButtonSelectButton").setText(item.payment(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        paymentField.click();
        WebLocator dateField = new WebLocator(window).setAttribute("aria-label", "Date");
        dateField.sendKeys(item.date());
        WebLocator saveButton = new WebLocator(window).setTag("button").setText("Save", SearchType.DEEP_CHILD_NODE_OR_SELF);
        saveButton.click();
        Utils.sleep(1);
    }
}
