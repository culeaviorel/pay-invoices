package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/BTGoRunner.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.btgo",
                "ro.sheet"
        },
        features = {
                "src/test/resources/feature/invoice/btGo.feature"
        }
)
@RunWith(Cucumber.class)
public class BTGoRunner {
}
