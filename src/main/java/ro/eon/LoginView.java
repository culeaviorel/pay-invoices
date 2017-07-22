package ro.eon;

import com.sdl.selenium.bootstrap.form.Form;
import com.sdl.selenium.web.SearchType;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.button.Button;
import com.sdl.selenium.web.button.InputButton;
import com.sdl.selenium.web.form.TextField;

public class LoginView extends Form {

    public LoginView() {
        setId("form-login");
    }

    private TextField userNameField = new TextField(this).setName("_username");
    private TextField passwordField = new TextField(this).setName("_password");
    private Button loginButton = new Button(this, "Login", SearchType.DEEP_CHILD_NODE_OR_SELF);

    public void login(String user, String pass) {
        userNameField.setValue(user);
        passwordField.setValue(pass);
        loginButton.click();
    }
}
