package ro.appsheet;

import com.sdl.selenium.Go;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
public class Dashboard {
    private final Button prevBtn = new Button().setAttribute("aria-label", "Prev");
    private final Button nextBtn = new Button().setAttribute("title", "Next");
    private final Button editBtn = new Button().setAttribute("aria-label", "Edit");
    private final Button saveBtn = new Button(null, "Save", SearchType.DEEP_CHILD_NODE_OR_SELF);
    private final Locale locale = new Locale("ro", "RO");
    private final TextField startDateEl = new TextField().setAttribute("aria-label", "Start Date");
    private final WebLocator table = new WebLocator().setId("Dashboard___FilteredDataView");
    private final WebLocator row = new WebLocator(table).setClasses("DeckRow");
    private final TextField priceField = new TextField().setAttribute("aria-label", "Suma");
    private final WebLocator dashboard = new WebLocator().setTag("li").setText("Dashboard", SearchType.DEEP_CHILD_NODE_OR_SELF);

    public void addItem(ItemRecord item) {
        Button addButton = new Button(null, "Add", SearchType.DEEP_CHILD_NODE_OR_SELF);
        addButton.click();
        WebLocator header = new WebLocator().setAttribute("role", "heading").setText("FilteredData");
        WebLocator window = new WebLocator().setAttribute("role", "presentation").setVisibility(true).setChildNodes(header);
        WebLocator nameField = new WebLocator(window).setAttribute("aria-label", "Name");
        nameField.sendKeys(item.name());
        WebLocator categoryField = new WebLocator(window).setClasses("ButtonSelectButton").setText(item.category(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        categoryField.click();
        WebLocator subCategorieComboBox = new WebLocator(window).setAttribute("aria-label", "SubCategorie");
        RetryUtils.retry(2, () -> {
            subCategorieComboBox.doClick();
            WebLocator options = new WebLocator().setAttribute("role", "presentation").setAttribute("x-placement", "top");
            WebLocator optionWithText = new WebLocator(options).setTag("li").setText(item.subcategory(), SearchType.DEEP_CHILD_NODE_OR_SELF);
            if (optionWithText.isPresent()) {
                optionWithText.click();
            } else {
                WebLocator option = new WebLocator(options).setTag("li");
                int size = option.size();
                option.setResultIdx(size);
                option.scrollIntoView(Go.START);
                if (optionWithText.isPresent()) {
                    optionWithText.click();
                }
            }
            String value = WebLocator.getExecutor().getValue(subCategorieComboBox);
            return value.equals(item.subcategory());
        });
        WebLocator priceField = new WebLocator(window).setAttribute("aria-label", "Suma");
        priceField.sendKeys(item.price());
        WebLocator paymentField = new WebLocator(window).setClasses("ButtonSelectButton").setText(item.payment(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        paymentField.click();
        WebLocator dateField = new WebLocator(window).setAttribute("aria-label", "Date");
        dateField.sendKeys(item.date());
        Button saveButton = new Button(window, "Save", SearchType.DEEP_CHILD_NODE_OR_SELF);
        saveButton.click();
        Utils.sleep(1);
    }

    public void editItem(ItemRecord item) {
        LocalDate itemDate = LocalDate.parse(item.date(), DateTimeFormatter.ofPattern("MM/dd/yyyy", locale));
        int month = itemDate.getMonthValue();
        int year = itemDate.getYear();
        RetryUtils.retry(12, () -> {
            String value = startDateEl.getValue();
            LocalDate startDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd", locale));
            int actualMonth = startDate.getMonthValue();
            int actualYear = startDate.getYear();
            if (month > actualMonth) {
                prevBtn.click();
            } else if (year > actualYear) {
                prevBtn.click();
            } else if (month < actualMonth) {
                nextBtn.click();
            } else if (year < actualYear) {
                nextBtn.click();
            }
            return month == actualMonth;
        });
        WebLocator group = new WebLocator(table).setClasses("GroupedHeaderRow").setText(item.category(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        int size = row.size();
        WebLocator next = group.next().setTag("following-sibling::*").setText(item.name(), SearchType.DEEP_CHILD_NODE_OR_SELF);
        RetryUtils.retry(2, () -> {
            boolean present = next.isPresent();
            if (!present) {
                row.setResultIdx(size);
                row.scrollIntoView(Go.START);
            } else {
                next.scrollIntoView(Go.CENTER);
            }
            return present;
        });
        next.click();
        editBtn.click();
        priceField.setValue(item.price());
        saveBtn.click();
        dashboard.click();
        Utils.sleep(1);
    }
}
