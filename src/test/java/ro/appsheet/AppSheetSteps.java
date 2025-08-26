package ro.appsheet;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;

import java.util.List;

@Slf4j
public class AppSheetSteps extends TestBase {

    @And("in AppSheet I add following values:")
    public void inAppSheetIAddFollowingValues(List<ItemRecord> items) {
        Dashboard appSheetUtils = new Dashboard();
        for (ItemRecord item : items) {
            appSheetUtils.addItem(item);
        }
    }
}
