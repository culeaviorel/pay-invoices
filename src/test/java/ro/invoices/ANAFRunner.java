package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/ANAFRunner.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.anaf"
        },
        features = {
                "src/test/resources/feature/invoice/anaf.feature"
        }
)
@RunWith(Cucumber.class)
public class ANAFRunner {
}
