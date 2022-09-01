package ro.nova;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;

public class DetaliiFacturaView extends WebLocator {

    public DetaliiFacturaView() {
        setClasses("v-dialog", "v-dialog--active");
    }

    public Button inchide = new Button(this, "Inchide", SearchType.DEEP_CHILD_NODE_OR_SELF);
    public Button download = new Button(this, "Descarca factura", SearchType.DEEP_CHILD_NODE_OR_SELF);

    public String getTitle() {
        WebLocator title = new WebLocator(this).setTag("b");
        return title.getText();
    }

    public String getFactura() {
        WebLocator factura = new WebLocator(this).setTag("pre").setText("Factura");
        return factura.getText().split("Factura")[1].trim();
    }

    public String getSuma() {
        WebLocator label = new WebLocator(this).setTag("pre").setText("Valoare");
        WebLocator suma = new WebLocator(label).setTag("pre").setRoot("//preceding-sibling::");
        return suma.getText().split(" ")[0].trim();
    }
}
