package ro.oblio;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.neo.Storage;
import ro.sheet.RowRecord;

import java.util.List;

@Slf4j
public class OblioSteps extends TestBase {

    private final Oblio oblio = new Oblio();
    private final AppUtils appUtils = new AppUtils();
    private final UserCredentials credentials = new UserCredentials();

    @And("I login in Oblio")
    public void iLoginInUp() {
        oblio.login(credentials.getOblioEmail(), credentials.getOblioPassword());
    }

    @And("in Oblio I get all invoices")
    public void inOblioIGetAllInvoices() {
        List<RowRecord> list = Storage.get("items");
        oblio.getInvoices(list);
    }
}
