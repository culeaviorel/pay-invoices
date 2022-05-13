package org.fasttrackit.util;

import com.sdl.selenium.utils.config.WebDriverConfig;
//import com.sdl.selenium.web.Browser;
//import io.cucumber.spring.CucumberContextConfiguration;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.test.context.ContextConfiguration;

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
}
