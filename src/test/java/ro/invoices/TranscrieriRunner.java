package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/TranscrieriRunner.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.transcrieri",
                "ro.sheet"
        },
        features = {
                "src/test/resources/feature/invoice/transcrieri.feature"
        }
)
@RunWith(Cucumber.class)
public class TranscrieriRunner {
}
