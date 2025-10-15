package ro.appsheet;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;

import java.util.List;

@Slf4j
public class AppSheetSteps extends TestBase {
    private final Dashboard appSheetUtils = new Dashboard();

    @And("in AppSheet I add following values:")
    public void inAppSheetIAddFollowingValues(List<ItemRecord> items) {
        for (ItemRecord item : items) {
            appSheetUtils.addItem(item);
        }
    }

    @And("in AppSheet I edit following values:")
    public void inAppSheetIEditFollowingValues(List<ItemRecord> items) {
        for (ItemRecord item : items) {
            appSheetUtils.editItem(item);
        }
    }
}
