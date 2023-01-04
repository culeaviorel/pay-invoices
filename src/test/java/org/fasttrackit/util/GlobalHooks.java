package org.fasttrackit.util;

import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.utils.Utils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GlobalHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalHooks.class);
    
    @After("@screen")
    public void screen(Scenario scenario) {
        String scenarioName = scenario.getName(); // TODO class name

        boolean isFailed = scenario.isFailed();
        if (isFailed) {
            LOGGER.warn(scenarioName + " Scenario has failed! Embed the screenshot in the report!--- ");
            byte[] screenshot = ((TakesScreenshot) WebDriverConfig.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenarioName + " - screenshot");
        }
    }

    @Before("@start")
    public void start(Scenario scenario) throws IOException {
        Driver.initialize();
    }

//    @AfterAll
//    public void stop() {
//        Driver.stop(true);
//    }

    public void takeScreenShot(String screenShotName) {
        // trim screenshot name because deleting files from jenkins cannot delete files that have a long name
        if (screenShotName.length() > 80) {
            screenShotName = screenShotName.substring(0, 80);
        }
        screenShotName = screenShotName.replaceAll(" ", "_");
        String resultPath, screenShotsResultsPath;
        resultPath = new File("test-output").getAbsolutePath();
        LOGGER.debug("resultPath: " + resultPath);
        screenShotsResultsPath = new File(resultPath + File.separator + this.getClass().getSimpleName() + File.separator + screenShotName).getAbsolutePath();
        screenShotName = Utils.getScreenShot(screenShotName, resultPath + "\\" + this.getClass().getSimpleName() + "\\");
        LOGGER.info(screenShotsResultsPath);
    }

}
