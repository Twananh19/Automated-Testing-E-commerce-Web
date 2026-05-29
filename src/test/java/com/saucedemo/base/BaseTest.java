package com.saucedemo.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BaseTest - Lớp cơ sở cho tất cả các Test class trong dự án.
 *
 * Chịu trách nhiệm:
 * - Khởi tạo WebDriver trước mỗi test case (@BeforeEach)
 * - Đóng trình duyệt sau mỗi test case (@AfterEach)
 * - Điều hướng đến trang chủ saucedemo.com
 *
 * Tất cả Test class (LoginTest, CheckoutTest...) đều kế thừa từ lớp này.
 */
public class BaseTest {

    // WebDriver dùng chung - protected để các lớp test con có thể truy cập
    protected WebDriver driver;

    // URL trang web cần kiểm thử
    private static final String BASE_URL = "http://localhost:5173/";

    /**
     * Phương thức setUp - chạy TRƯỚC MỖI test case.
     * Khởi tạo trình duyệt Chrome, mở trang saucedemo.com.
     *
     * Lưu ý: KHÔNG dùng implicitlyWait ở đây vì đã dùng Explicit Wait
     * trong BasePage. Selenium khuyến cáo không trộn lẫn 2 loại wait.
     */
    @BeforeEach
    public void setUp() {
        // Cấu hình Chrome - chạy headless (không giao diện) để tăng tốc
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Khởi tạo ChromeDriver (Selenium 4.x tự quản lý driver, không cần set path)
        driver = new ChromeDriver(options);

        // Phóng to cửa sổ trình duyệt
        driver.manage().window().maximize();

        // Mở trang web cần kiểm thử
        driver.get(BASE_URL);
    }

    /**
     * Phương thức tearDown - chạy SAU MỖI test case.
     * Đóng trình duyệt và giải phóng tài nguyên.
     */
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Chụp ảnh màn hình - hữu ích khi test thất bại để debug.
     * Ảnh được lưu vào thư mục docs/screenshots/
     * @param testName Tên test case để đặt tên file ảnh
     */
    protected void takeScreenshot(String testName) {
        try {
            // Tạo thư mục screenshots nếu chưa tồn tại
            Path screenshotDir = Paths.get("docs", "screenshots");
            Files.createDirectories(screenshotDir);

            // Chụp ảnh màn hình
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Đặt tên file: testName_timestamp.png
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";

            // Lưu file ảnh
            Path destination = screenshotDir.resolve(fileName);
            Files.copy(srcFile.toPath(), destination);
        } catch (IOException e) {
            System.err.println("Không thể chụp ảnh màn hình: " + e.getMessage());
        }
    }
}
