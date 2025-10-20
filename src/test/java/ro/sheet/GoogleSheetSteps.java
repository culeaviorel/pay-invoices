package ro.sheet;

import com.google.common.base.Strings;
import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import ro.neo.Storage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GoogleSheetSteps extends TestBase {

    private final AppUtils appUtils = new AppUtils();

    @And("I add in Facturi or Bonuri in google sheet:")
    public void iAddInFacturiOrBonuriInGoogleSheet(List<ItemTO> items) {
        for (ItemTO item : items) {
            String facturaPath = item.getType().equals("Dovada") ? dovada2025() : facturi2025();
            String deciziilePath = Strings.isNullOrEmpty(item.getDecizia()) ? "" : deciziile2025();
            String decontPath = Strings.isNullOrEmpty(item.getDecont()) ? "" : decont2025();
            appUtils.uploadFileAndAddRowInFacturiAndContForItem(item, facturaPath, deciziilePath, decontPath);
        }
    }

    @And("in Google Sheets I get all items from Factura")
    public void inANAFIGetAllItemsFromFractura() {
        List<List<Object>> values = appUtils.getValues(appUtils.getFacturiSheetId(), "2025!A1:H");
        List<RowRecord> list = values.stream().map(i -> {
            RowRecord rowRecord = new RowRecord(
                    (String) i.get(0),
                    (String) i.get(1),
                    (String) i.get(2),
                    (String) i.get(3),
                    (String) i.get(4),
                    (String) i.get(5),
                    i.size() != 7 ? "" : (String) i.get(6),
                    i.size() != 8 ? "" : (String) i.get(7)
            );
            return rowRecord;
        }).filter(i -> i.value().contains(",")).toList();
        List<RowRecord> existingItems = new ArrayList<>(list);
        Storage.set("items", existingItems);
    }
}
