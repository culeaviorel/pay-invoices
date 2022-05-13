package ro.jira;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.fasttrackit.util.TestBase;
import ro.imobiliare.LoginView;

import java.util.List;

/**
 * Scenario: Create my tasks
 * Given I open url "https://jira.sdl.com/secure/RapidBoard.jspa?projectKey=LTLC&rapidView=252"
 * When I login on JIRA using ""/""
 * And I create sub tasks:
 * | user   | storyId | team | pi    | activity                    | nameTask    | hours |
 * | vculea | 22181   | Tyke | PI 11 | Automation/Performance Test | Automation  | 8     |
 * | vculea | 22181   | Tyke | PI 11 | Manual Test                 | Manual Test | 8     |
 * | vculea | 22181   | Tyke | PI 11 | Test Case                   | Test Case   | 8     |
 */
public class JiraSteps extends TestBase {
    private LoginView loginView = new LoginView();
    private BoardView boardView = new BoardView();

    @Then("^I login on JIRA using \"([^\"]*)\"/\"([^\"]*)\"$")
    public void login(String user, String pass) {
        loginView.login(user, pass);
    }

    @And("^I create sub tasks:$")
    public void iCreateSubTasks(List<Tasks> tasks) {
        boardView.createTasks(tasks);
    }
}
