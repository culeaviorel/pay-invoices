package ro.mymoney;

import com.sdl.selenium.extjs6.form.ComboBox;
import com.sdl.selenium.extjs6.form.DateField;
import com.sdl.selenium.extjs6.form.TextField;
import com.sdl.selenium.extjs6.window.Window;
import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.grid.Grid;
import com.sdl.selenium.web.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class View {
    private static final Logger LOGGER = LoggerFactory.getLogger(View.class);

    public View() {
    }

    private final Grid grid = new Grid();
    private Button add = new Button(grid, "Add Transaction");
    private Window addNew = new Window("Add New");
    private TextField name = new TextField(addNew, "Name:");
    private ComboBox category = new ComboBox(addNew, "Category:");
    private ComboBox subCategory = new ComboBox(addNew, "Subcategory:");
    private DateField dateField = new DateField(addNew, "Data:");
    private TextField sumaField = new TextField(addNew, "Price:");
    private Button saveButton = new Button(addNew, "Save");

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
        category.select(cat, Duration.ofSeconds(1));
        subCategory.select(sub, Duration.ofSeconds(1));
        dateField.select(data, "dd/MM/yyyy");
        sumaField.setValue(sum);
        return saveButton.click();
    }

    public static void main(String[] args) {
        View login = new View();
        LOGGER.debug(login.category.getXPath());
    }
}
