package ro.anaf;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import ro.neo.Storage;
import ro.sheet.GoogleSheet;
import ro.sheet.RowRecord;

import java.util.List;

@Slf4j
public class ANAFSteps extends TestBase {

    private final ANAF anaf = new ANAF();
    private final AppUtils appUtils = new AppUtils();

    @And("I login in ANAF")
    public void iLoginInUp() {
        anaf.login();
    }

    @And("in ANAF I get all invoices for {int} days")
    public void inANAFIGetAllInvoicesForDays(int days) {
        List<RowRecord> list = Storage.get("items");
        List<String> files = GoogleSheet.getFiles(appUtils.getEFacturaFolderId());
        anaf.getAllInvoices(days, list, appUtils.getEFacturaFolderId(), files);
    }
}
