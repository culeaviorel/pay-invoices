package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/UpRomaniaRunner.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.up",
                "ro.mymoney"
        },
        features = {
                "src/test/resources/feature/invoice/upRomania.feature"
        }
)
@RunWith(Cucumber.class)
public class UpRomaniaRunner {
}
