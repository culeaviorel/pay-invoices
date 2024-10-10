package org.fasttrackit.util;

import com.sdl.selenium.utils.config.WebDriverConfig;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//@CucumberContextConfiguration
//@ContextConfiguration("classpath:cucumber.xml")
public abstract class TestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestBase.class);

    public static WebDriver driver;

    static {
        if (WebDriverConfig.getDriver() == null) {
            try {
                Driver.initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String dovada() {
        return location() + "Facturi\\Dovada\\";
    }

    public static String location() {
        return "C:\\Users\\vculea\\Desktop\\Biserica\\2024\\";
    }

    public static String bt() {
        return "C:\\Users\\vculea\\Desktop\\BT\\2024\\";
    }
}
