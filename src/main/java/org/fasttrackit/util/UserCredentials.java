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

    public String getNeoID() {
        return getProperty("neo.id");
    }
    public String getNeoPassword() {
        return getProperty("neo.password");
    }

    public String getBTGoID() {
        return getProperty("btgo.id");
    }
    public String getBTGoPassword() {
        return getProperty("btgo.password");
    }

    public String getCaSomesEmail() {
        return getProperty("ca.somes.email");
    }
    public String getCaSomesPassword() {
        return getProperty("ca.somes.password");
    }

    public String getUpEmail() {
        return getProperty("up.email");
    }
    public String getUpPassword() {
        return getProperty("up.password");
    }

    public String getMyVirtualEmail() {
        return getProperty("my.virtual.email");
    }
    public String getMyVirtualPassword() {
        return getProperty("my.virtual.password");
    }

    public String getContDeEconomii() {
        return getProperty("cont.de.economii");
    }

    public String getContCurent() {
        return getProperty("cont.curent");
    }

    public String getOblioEmail() {
        return getProperty("oblio.email");
    }
    public String getOblioPassword() {
        return getProperty("oblio.password");
    }
}
