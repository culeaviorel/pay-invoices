package org.fasttrackit.util;

import com.sdl.selenium.utils.config.WebDriverConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Driver {
    public static WebDriver driver;

    public static void initialize() throws IOException {
        if (driver == null) {
            driver = WebDriverConfig.getWebDriver("chrome.properties");
            try {
                String downloadPath = WebDriverConfig.getDownloadPath();
                File download = new File(downloadPath.substring(0, downloadPath.lastIndexOf("\\") + 1));
                FileUtils.cleanDirectory(download);
                File directory = new File(downloadPath);
                FileUtils.forceMkdir(directory);
            } catch (IOException e) {
                log.error("{}", e.toString());
            }
        }
    }

    public static void initialize(boolean newDriver) throws IOException {
        stop(newDriver);
        initialize();
    }

    public static void stop(boolean newDriver) {
        if (newDriver && driver != null) {
            driver.quit();
            driver = null;
        }
    }
}