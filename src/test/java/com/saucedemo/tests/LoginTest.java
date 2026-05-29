package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.InventoryPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * LoginTest - Bộ test kiểm thử chức năng Đăng nhập.
 *
 * Kỹ thuật kiểm thử áp dụng:
 * - Equivalence Partitioning (Phân vùng tương đương): chia input thành các nhóm hợp lệ/không hợp lệ
 * - Boundary Value Analysis (Phân tích giá trị biên): kiểm tra các giá trị biên (rỗng, khoảng trắng)
 *
 * Kế thừa BaseTest để tự động khởi tạo và đóng trình duyệt.
 */
public class LoginTest extends BaseTest {

    // ==================== POSITIVE TEST CASES (Kiểm thử tích cực) ====================

    @Test
    @DisplayName("TC_LOGIN_01 - Đăng nhập thành công với tài khoản hợp lệ")
    public void testLoginSuccessfully() {
        // Arrange (Chuẩn bị)
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        // Act (Thực hiện)
        loginPage.loginAs("standard_user", "secret_sauce");

        // Assert (Kiểm chứng) - verify chuyển hướng đến trang Inventory
        inventoryPage.waitForLoad();
        String expectedUrl = "http://localhost:5173/inventory";
        Assertions.assertEquals(expectedUrl, driver.getCurrentUrl(),
                "Đăng nhập không thành công, URL không khớp!");
        Assertions.assertTrue(inventoryPage.isPageDisplayed(),
                "Trang Inventory không hiển thị sau khi đăng nhập!");
    }

    @Test
    @DisplayName("TC_LOGIN_08 - Đăng nhập với tài khoản performance_glitch_user (trang tải chậm)")
    public void testLoginWithPerformanceGlitchUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("performance_glitch_user", "secret_sauce");

        // Trang vẫn phải chuyển hướng thành công dù tải chậm (Explicit Wait xử lý)
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitForLoad();
        String expectedUrl = "http://localhost:5173/inventory";
        Assertions.assertEquals(expectedUrl, driver.getCurrentUrl(),
                "performance_glitch_user không thể đăng nhập!");
    }

    // ==================== NEGATIVE TEST CASES (Kiểm thử tiêu cực) ====================

    @Test
    @DisplayName("TC_LOGIN_02 - Đăng nhập với tài khoản bị khóa (locked_out_user)")
    public void testLoginWithLockedOutUser() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("locked_out_user", "secret_sauce");

        String expectedError = "Sorry, this user has been locked out.";
        Assertions.assertEquals(expectedError, loginPage.getErrorMessage(),
                "Thông báo lỗi tài khoản bị khóa không đúng!");
    }

    @Test
    @DisplayName("TC_LOGIN_03 - Đăng nhập với mật khẩu sai")
    public void testLoginWithWrongPassword() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("standard_user", "wrong_password");

        String expectedError = "Username and password do not match any user in this service";
        Assertions.assertEquals(expectedError, loginPage.getErrorMessage(),
                "Thông báo lỗi sai mật khẩu không đúng!");
    }

    @Test
    @DisplayName("TC_LOGIN_04 - Đăng nhập với username rỗng")
    public void testLoginWithEmptyUsername() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("", "secret_sauce");

        String expectedError = "Username is required";
        Assertions.assertEquals(expectedError, loginPage.getErrorMessage(),
                "Thông báo lỗi username rỗng không đúng!");
    }

    @Test
    @DisplayName("TC_LOGIN_05 - Đăng nhập với password rỗng")
    public void testLoginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("standard_user", "");

        String expectedError = "Password is required";
        Assertions.assertEquals(expectedError, loginPage.getErrorMessage(),
                "Thông báo lỗi password rỗng không đúng!");
    }

    @Test
    @DisplayName("TC_LOGIN_06 - Đăng nhập với cả username và password rỗng")
    public void testLoginWithBothFieldsEmpty() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("", "");

        String expectedError = "Username is required";
        Assertions.assertEquals(expectedError, loginPage.getErrorMessage(),
                "Thông báo lỗi khi cả 2 trường rỗng không đúng!");
    }

    @Test
    @DisplayName("TC_LOGIN_07 - Đăng nhập với username không tồn tại")
    public void testLoginWithInvalidUsername() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("invalid_user_abc", "secret_sauce");

        String expectedError = "Username and password do not match any user in this service";
        Assertions.assertEquals(expectedError, loginPage.getErrorMessage(),
                "Thông báo lỗi username không tồn tại không đúng!");
    }
}
