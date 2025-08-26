package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/AppSheetRunner.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.appsheet"
        },
        features = {
                "src/test/resources/feature/invoice/appSheet.feature"
        }
)
@RunWith(Cucumber.class)
public class AppSheetRunner {
}
