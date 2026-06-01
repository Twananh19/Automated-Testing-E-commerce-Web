package com.ecommerce.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class BaseSeleniumTest {

    @LocalServerPort
    protected int port;

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().clearResolutionCache().browserVersion("144").setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (driver != null) {
            takeScreenshot(testInfo.getDisplayName());
            driver.quit();
        }
    }

    private boolean isTestFailed(TestInfo testInfo) {
        try {
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    protected void takeScreenshot(String testName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path targetDir = Paths.get("target", "screenshots");
            Files.createDirectories(targetDir);
            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
            Files.copy(screenshot.toPath(), targetDir.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot successfully saved to target/screenshots/" + fileName);
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void navigateTo(String path) {
        System.out.println("[NAVIGATE] Navigating to: " + path);
        driver.get(baseUrl + path);
        String currentUrl = driver.getCurrentUrl();
        System.out.println("[NAVIGATE] Current URL after driver.get: " + currentUrl);
        if (currentUrl.contains("/login")) {
            System.out.println("[NAVIGATE] Redirection to /login detected! Logging in...");
            loginAs("standard_user", "secret_sauce");
            String postLoginUrl = driver.getCurrentUrl();
            System.out.println("[NAVIGATE] Current URL after login: " + postLoginUrl);
            if (!postLoginUrl.contains(path) && !path.equals("/")) {
                System.out.println("[NAVIGATE] Navigating back to final destination: " + path);
                driver.get(baseUrl + path);
            }
        }
    }

    protected void loginAs(String username, String password) {
        System.out.println("[LOGIN] Initiating login for user: " + username);
        driver.get(baseUrl + "/login");
        org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10));
        
        System.out.println("[LOGIN] Waiting for username field...");
        org.openqa.selenium.WebElement usernameField = wait.until(
            org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.id("username"))
        );
        usernameField.clear();
        usernameField.sendKeys(username);
        
        org.openqa.selenium.WebElement passwordField = driver.findElement(org.openqa.selenium.By.id("password"));
        passwordField.clear();
        passwordField.sendKeys(password);
        
        System.out.println("[LOGIN] Clicking login button...");
        org.openqa.selenium.WebElement loginBtn = driver.findElement(org.openqa.selenium.By.cssSelector("button[data-testid='login-button']"));
        loginBtn.click();
        
        System.out.println("[LOGIN] Waiting for redirect away from /login...");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.not(
            org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/login")
        ));
        System.out.println("[LOGIN] Login complete. Redirected to: " + driver.getCurrentUrl());
    }
}

