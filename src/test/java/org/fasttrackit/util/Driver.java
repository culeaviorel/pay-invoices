package org.fasttrackit.util;

import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.Browser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@Slf4j
public class Driver {
    public static WebDriver driver;

    public static void initialize() throws IOException {
        if (driver == null) {
            driver = WebDriverConfig.getWebDriver(Browser.CHROME);
            try {
                FileUtils.forceMkdir(new File(WebDriverConfig.getDownloadPath()));
            } catch (IOException e) {
                log.error("{}", e);
            }
        }
    }

    public static void initialize(boolean newDriver) throws IOException {
        if (newDriver && driver != null) {
            driver.quit();
            driver = null;
        }
        initialize();
    }
}