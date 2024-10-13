package ro.invoices;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/InvoicesTest.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.btrl",
                "ro.payu.secure",
                "ro.secure11gw",
                "ro.mymoney",
                "ro.neo",
                "ro.btgo",
                "ro.rcsrds.digicare",
                "ro.jira",
                "ro.eon",
                "ro.up",
                "ro.nova",
                "ro.shelly",
                "ro.olx",
                "ro.casomes",
                "ro.sheet",
                "ro.home.assistant",
                "ro.leiibvb",
                "ro.autovit"
        },
        features = {
                "src/test/resources/feature/invoice/all-invoices.feature"
        }
)
@RunWith(Cucumber.class)
public class InvoicesRunner {
}
