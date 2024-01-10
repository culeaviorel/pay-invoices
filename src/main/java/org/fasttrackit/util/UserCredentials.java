package org.fasttrackit.util;

import com.sdl.selenium.web.utils.PropertiesReader;

public class UserCredentials extends PropertiesReader {

    public UserCredentials() {
        super("src\\test\\resources\\user-credential.properties");
    }

    public String getShellyEmail() {
        return getProperty("shelly.email");
    }
    public String getShellyPassword() {
        return getProperty("shelly.password");
    }

    public String getHomeAssistantName() {
        return getProperty("home.assistant.name");
    }
    public String getHomeAssistantPassword() {
        return getProperty("home.assistant.password");
    }
}
