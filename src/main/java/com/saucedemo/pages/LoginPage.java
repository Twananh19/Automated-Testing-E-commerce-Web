package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage - Page Object đại diện cho trang Đăng nhập (https://www.saucedemo.com/).
 *
 * Áp dụng mẫu thiết kế Page Object Model (POM):
 * - Các locator (bộ định vị) được khai báo private (tính đóng gói)
 * - Các phương thức public đại diện cho hành động người dùng
 *
 * Kỹ thuật kiểm thử áp dụng: Equivalence Partitioning, Boundary Value Analysis
 */
public class LoginPage extends BasePage {

    // === LOCATORS (Bộ định vị phần tử) - private để đảm bảo tính đóng gói ===
    private final By usernameInput = By.cssSelector("[data-testid='username']");       // Ô nhập tên đăng nhập
    private final By passwordInput = By.cssSelector("[data-testid='password']");         // Ô nhập mật khẩu
    private final By loginButton = By.cssSelector("[data-testid='login-button']");       // Nút đăng nhập
    private final By errorMessage = By.cssSelector("[data-testid='error-message']"); // Thông báo lỗi

    /**
     * Constructor - nhận WebDriver từ BaseTest.
     * @param driver Đối tượng WebDriver
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // === ACTION METHODS (Các phương thức thao tác) ===

    /**
     * Nhập tên đăng nhập vào ô Username.
     * @param username Tên đăng nhập
     */
    public void enterUsername(String username) {
        enterText(usernameInput, username);
    }

    /**
     * Nhập mật khẩu vào ô Password.
     * @param password Mật khẩu
     */
    public void enterPassword(String password) {
        enterText(passwordInput, password);
    }

    /**
     * Click nút Login để đăng nhập.
     */
    public void clickLogin() {
        click(loginButton);
    }

    /**
     * Thực hiện đăng nhập đầy đủ - phương thức tiện ích gộp 3 bước.
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     */
    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /**
     * Lấy nội dung thông báo lỗi hiển thị trên trang.
     * @return Chuỗi text thông báo lỗi
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Kiểm tra thông báo lỗi có hiển thị hay không.
     * @return true nếu hiển thị, false nếu không
     */
    public boolean isErrorDisplayed() {
        return isElementDisplayed(errorMessage);
    }
}
