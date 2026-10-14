package com.financetracker;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Selenium WebDriver end-to-end tests.
 *
 * Run with:  mvn verify -P selenium
 *
 * Requires: Google Chrome installed on the machine.
 * WebDriverManager automatically downloads a compatible ChromeDriver.
 *
 * These tests are excluded from the default `mvn test` run (see pom.xml Surefire config)
 * and are executed via the Failsafe plugin during `mvn verify`.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    // ── Setup / Teardown ──────────────────────────────────────────

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");   // headless mode for CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1366,768");

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Tests ─────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Dashboard page loads with title 'Finance Tracker'")
    void pageLoads() {
        driver.get(baseUrl() + "/");
        assertThat(driver.getTitle()).contains("Finance Tracker");
    }

    @Test
    @Order(2)
    @DisplayName("Dashboard shows income, expense, balance cards")
    void dashboardCardsPresent() {
        driver.get(baseUrl() + "/");
        assertThat(driver.findElement(By.id("card-income")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("card-expense")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("card-balance")).isDisplayed()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Add income transaction and verify it appears in the table")
    void addIncomeTransaction() {
        driver.get(baseUrl() + "/");

        driver.findElement(By.id("title")).sendKeys("Test Salary");

        driver.findElement(By.id("amount")).sendKeys("12000");

        new Select(driver.findElement(By.id("type"))).selectByValue("INCOME");
        new Select(driver.findElement(By.id("category"))).selectByValue("Salary");

        driver.findElement(By.id("submit-btn")).click();

        // Wait for redirect and table to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("transaction-table")));

        String body = driver.findElement(By.id("transaction-table")).getText();
        assertThat(body).contains("Test Salary");
        assertThat(body).contains("INCOME");
    }

    @Test
    @Order(4)
    @DisplayName("Dashboard totals update after adding a transaction")
    void dashboardTotalsUpdate() {
        driver.get(baseUrl() + "/");
        String income = driver.findElement(By.id("total-income")).getText();
        // Should contain a rupee amount (not zero, since previous test added ₹12000)
        assertThat(income).contains("₹");
        assertThat(income).doesNotContain("₹ 0.00");
    }

    @Test
    @Order(5)
    @DisplayName("Health endpoint returns {\"status\":\"UP\"}")
    void healthEndpoint() {
        driver.get(baseUrl() + "/health");
        assertThat(driver.getPageSource()).contains("UP");
    }

    @Test
    @Order(6)
    @DisplayName("Client-side validation prevents empty form submission")
    void clientSideValidation() {
        driver.get(baseUrl() + "/");

        // Submit without filling in any field
        driver.findElement(By.id("submit-btn")).click();

        // The page URL should NOT have changed (JS prevented submission)
        assertThat(driver.getCurrentUrl()).endsWith("/");

        // An error message should be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("js-error")));
        assertThat(driver.findElements(By.className("js-error"))).isNotEmpty();
    }

    @Test
    @Order(7)
    @DisplayName("Delete button removes transaction from the table")
    void deleteTransaction() {
        // First, add a transaction to delete
        driver.get(baseUrl() + "/");
        driver.findElement(By.id("title")).sendKeys("To Delete");
        driver.findElement(By.id("amount")).sendKeys("99");
        new Select(driver.findElement(By.id("type"))).selectByValue("EXPENSE");
        new Select(driver.findElement(By.id("category"))).selectByValue("Food");
        driver.findElement(By.id("submit-btn")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("transaction-table")));

        // Find the delete button for our transaction and click it
        // Accept the JS confirm dialog
        WebElement deleteBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//td[contains(text(),'To Delete')]/following-sibling::td//button[@class='btn-delete']")
                )
        );

        // Handle the confirm() dialog
        ((JavascriptExecutor) driver).executeScript(
                "window.confirm = function(){ return true; }");

        deleteBtn.click();

        // Wait for redirect
        wait.until(ExpectedConditions.urlToBe(baseUrl() + "/"));

        // "To Delete" should no longer be in the table (or empty state is shown)
        String pageSource = driver.getPageSource();
        assertThat(pageSource).doesNotContain(">To Delete<");
    }
}
