package ro.mymoney;

import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.form.ComboBox;
import com.sdl.selenium.extjs6.form.DateField;
import com.sdl.selenium.extjs6.form.TextField;
import com.sdl.selenium.extjs6.grid.Grid;
import com.sdl.selenium.extjs6.window.Window;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Getter
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
        add.ready(Duration.ofSeconds(10));
        add.click();
        name.setValue(denum);
        RetryUtils.retry(2, () -> category.select(cat, Duration.ofSeconds(1)));
        Utils.sleep(800);
        RetryUtils.retry(2, () -> subCategory.select(sub, Duration.ofSeconds(2)));
        boolean select = dateField.select(data.split(" ")[0], "dd-MM-yyyy");
        sumaField.setValue(sum);
        if (select) {
            return saveButton.click();
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        View login = new View();
        LOGGER.debug(login.category.getXPath());
    }
}
