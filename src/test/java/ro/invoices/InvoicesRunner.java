package ro.invoices;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber", "json:target/jsonReports/InvoicesTest.json"},
        glue = {
                "org.fasttrackit.util",
                "ro.btrl",
                "ro.payu.secure",
                "ro.secure11gw",
                "ro.mymoney",
                "ro.rcsrds.digicare",
                "ro.jira",
                "ro.eon",
                "ro.leiibvb"
        },
        features = {
                "src/test/resources/feature/invoice/all-invoices.feature"
        }
)
@RunWith(Cucumber.class)
public class InvoicesRunner {
}
