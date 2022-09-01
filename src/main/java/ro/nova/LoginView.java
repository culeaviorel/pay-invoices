package ro.nova;

import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.form.TextField;

public class LoginView extends WebLocator {

    public LoginView() {
        setClasses("login");
    }

    private final WebLocator emailLabel = new WebLocator(this ).setText("Email *");
    private final TextField userNameField = new TextField(emailLabel).setRoot("//following-sibling::");
    private final TextField passwordField = new TextField(this).setType("password");
    private final Button loginButton = new Button(this, "Autentifica-te", SearchType.DEEP_CHILD_NODE_OR_SELF);

    public void login(String user, String pass) {
        userNameField.setValue(user);
        passwordField.setValue(pass);
        loginButton.click();
    }
}
