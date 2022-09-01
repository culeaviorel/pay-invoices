package ro.nova;

import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.form.TextField;
import com.sdl.selenium.web.link.WebLink;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.en.And;
import org.fasttrackit.util.TestBase;

public class NovaSteps extends TestBase {
    private final LoginView loginView = new LoginView();

    @And("I login on Nova {string} and {string}")
    public void iLoginOnNova(String user, String password) {
//        WebDriverConfig.getDriver().get("https://crm.novapg.ro/login");
//        FileUtils.cleanDownloadDir();
//        loginView.login(user, password);
//        WebLink facturi = new WebLink(null, "Facturi si plati");
//        facturi.click();
//        WebLocator facturilemele = new WebLocator().setClasses("facturilemele");
//        Table table = new Table(facturilemele);
//        Utils.sleep(4000);
//        boolean populate = table.ready(true);
//        LocalDateTime now = LocalDateTime.now();
//        int monthValue = now.getMonthValue();
//        Button button = new Button().setClasses("v-btn", "pay-btn2");
//        Cell cell = new Cell().setTemplateValue("tagAndPosition", "7").setChildNodes(button);
//        Row row = table.getRow(new Cell(4, "IUGOSLAVIEI, nr. 64"), new Cell(5, monthValue + "-"), cell);
//        boolean ready = row.ready(Duration.ofSeconds(20));
//        List<Factura> facturas = new ArrayList<>();
//        String lastName = "";
//        int size = row.size();
//        for (int i = 1; i <= size; i++) {
//            row.setResultIdx(i);
//            RetryUtils.retry(2, () -> row.getCell(3).click());
//            DetaliiFacturaView detaliiFacturaView = new DetaliiFacturaView();
//            String title = detaliiFacturaView.getTitle();
//            String type = title.contains("gaze") ? "Gaz" : "Current";
//            String factura = detaliiFacturaView.getFactura();
//            String suma = detaliiFacturaView.getSuma();
//            detaliiFacturaView.download.click();
//            String downloadPath = WebDriverConfig.getDownloadPath();
//            String[] list = new File(downloadPath).list();
//            for (String f : list) {
//                if (Strings.isNullOrEmpty(lastName)) {
//                    lastName = f;
//                } else if (!lastName.equals(f)) {
//                    lastName = f;
//                }
//            }
//            String path = downloadPath + File.separator + lastName;
//            detaliiFacturaView.inchide.click();
//            facturas.add(new Factura(factura, type, suma, path));
//        }
        WebDriverConfig.getDriver().get("https://www.google.com/intl/ro_ro/drive/");
        WebLink goDrive = new WebLink(null, "Accesați Drive").setAttribute("data-label", "header").setResultIdx(2);
        goDrive.click();
        TextField email = new TextField().setId("identifierId");
        email.setValue("culeaviorel@gmail.com");
        Button inainte = new Button(null, "Înainte", SearchType.DEEP_CHILD_NODE_OR_SELF);
        inainte.click();
        Utils.sleep(2);
    }
}
