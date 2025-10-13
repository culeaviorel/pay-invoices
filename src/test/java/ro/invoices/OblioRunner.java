package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/OblioRunner.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.sheet",
                "ro.oblio"
        },
        features = {
                "src/test/resources/feature/invoice/oblio.feature"
        }
)
@RunWith(Cucumber.class)
public class OblioRunner {
}
