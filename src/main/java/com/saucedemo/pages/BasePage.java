package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage - Lớp cơ sở cho tất cả các Page Object trong dự án.
 * 
 * Mục đích: Chứa các hàm tiện ích dùng chung (click, nhập text, lấy text...)
 * sử dụng Explicit Wait thay vì Thread.sleep để đảm bảo test ổn định.
 * 
 * Tất cả các Page class khác (LoginPage, CartPage...) đều kế thừa từ lớp này.
 */
public class BasePage {

    // WebDriver dùng chung - protected để các lớp con có thể truy cập
    protected WebDriver driver;

    // Đối tượng chờ tường minh (Explicit Wait) - thời gian tối đa chờ phần tử xuất hiện
    protected WebDriverWait wait;

    // Thời gian chờ mặc định (giây)
    private static final int DEFAULT_TIMEOUT = 10;

    /**
     * Constructor - Khởi tạo BasePage với WebDriver.
     * @param driver Đối tượng WebDriver được truyền từ BaseTest
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /**
     * Chờ cho phần tử hiển thị trên trang (visible) rồi trả về WebElement.
     * Sử dụng Explicit Wait - KHÔNG dùng Thread.sleep.
     * @param locator Bộ định vị phần tử (By.id, By.cssSelector,...)
     * @return WebElement đã hiển thị
     */
    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Click vào phần tử - chờ phần tử có thể click được (clickable) trước khi thao tác.
     * @param locator Bộ định vị phần tử
     */
    protected void click(By locator) {
        waitForElementVisible(locator);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Nhập text vào ô input - xóa nội dung cũ trước khi nhập mới.
     * @param locator Bộ định vị phần tử input
     * @param text    Nội dung cần nhập
     */
    protected void enterText(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Lấy nội dung text của phần tử.
     * @param locator Bộ định vị phần tử
     * @return Chuỗi text hiển thị trên phần tử
     */
    protected String getText(By locator) {
        return waitForElementVisible(locator).getText();
    }

    /**
     * Chờ URL chứa chuỗi mong đợi — dùng để đảm bảo trang đã chuyển hướng xong
     * trước khi tìm phần tử trên trang mới. Giải quyết lỗi TimeoutException
     * khi trang chưa load xong mà đã tìm phần tử.
     * @param urlFragment Chuỗi con cần có trong URL (ví dụ: "checkout-step-two")
     */
    protected void waitForUrlContains(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Kiểm tra phần tử có đang hiển thị trên trang hay không.
     * @param locator Bộ định vị phần tử
     * @return true nếu phần tử hiển thị, false nếu không
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            return waitForElementVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
