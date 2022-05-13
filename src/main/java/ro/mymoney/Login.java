package ro.mymoney;

import com.sdl.selenium.extjs6.button.Button;
import com.sdl.selenium.extjs6.form.TextField;
import com.sdl.selenium.extjs6.window.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Window {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    public Login() {
        super("Login");
    }

    private final TextField emailField = new TextField(this, "Email:");
    private final TextField passwordField = new TextField(this, "Password:");
    private final Button loginButton = new Button(this, "Submit");

    public void login(String user, String pass) {
        ready();
        emailField.setValue(user);
        passwordField.setValue(pass);
        loginButton.click();
    }
}
