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
        return facturi() + "Dovada\\";
    }

    public static String dovada2025() {
        return facturi2025() + "Dovada\\";
    }

    public static String facturi() {
        return location() + "Facturi\\";
    }

    public static String facturi2025() {
        return location2025() + "Facturi\\";
    }

    public static String csv() {
        return location() + "CSV\\";
    }

    public static String csv2025() {
        return location2025() + "CSV\\";
    }

    public static String location() {
        return "C:\\Users\\vculea\\Desktop\\Biserica\\2024\\";
    }

    public static String location2025() {
        return "C:\\Users\\vculea\\Desktop\\Biserica\\2025\\";
    }

    public static String bt() {
        return "C:\\Users\\vculea\\Desktop\\BT\\2025\\";
    }
}
