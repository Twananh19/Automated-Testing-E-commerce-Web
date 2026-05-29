package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * CheckoutTest - Bộ test kiểm thử luồng Mua hàng (Checkout Flow).
 *
 * Kỹ thuật kiểm thử áp dụng:
 * - Decision Table Testing: kiểm tra các tổ hợp input khi điền thông tin giao hàng
 * - End-to-End Testing: kiểm tra toàn bộ luồng từ đăng nhập đến đặt hàng thành công
 *
 * Luồng mua hàng: Login → Inventory → Cart → Checkout Info → Overview → Complete
 */
public class CheckoutTest extends BaseTest {

    // Thông tin đăng nhập mặc định
    private static final String VALID_USER = "standard_user";
    private static final String VALID_PASS = "secret_sauce";

    /**
     * Helper: Đăng nhập và thêm sản phẩm vào giỏ hàng.
     * Dùng chung cho nhiều test case để tránh trùng lặp code (DRY principle).
     */
    private void loginAndAddToCart() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.loginAs(VALID_USER, VALID_PASS);
        inventoryPage.waitForLoad(); // Chờ trang load
        inventoryPage.clickAddToCart();
        try { Thread.sleep(500); } catch(Exception e) {} // Đợi API response
        inventoryPage.clickCartIcon();
    }

    // ==================== POSITIVE TEST CASES ====================

    @Test
    @DisplayName("TC_CHECKOUT_01 - Luồng mua hàng thành công end-to-end")
    public void testEndToEndCheckoutFlow() {
        // Bước 1: Đăng nhập
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(VALID_USER, VALID_PASS);

        // Bước 2: Thêm sản phẩm vào giỏ hàng
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitForLoad(); // Chờ trang load
        inventoryPage.clickAddToCart();

        try { Thread.sleep(500); } catch(Exception e) {} // Đợi API response

        // Verify: badge giỏ hàng hiển thị số 1
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCount(),
                "Số lượng sản phẩm trong giỏ hàng không đúng!");

        // Bước 3: Vào giỏ hàng
        inventoryPage.clickCartIcon();

        // Bước 4: Checkout
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        // Bước 5: Nhập thông tin giao hàng
        CheckoutInfoPage checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutInfoPage.enterShippingInfo("Twan", "Nguyen", "700000");

        // Bước 6: Xác nhận đơn hàng
        CheckoutOverviewPage checkoutOverviewPage = new CheckoutOverviewPage(driver);
        checkoutOverviewPage.clickFinish();

        // Bước 7: Kiểm tra thông báo thành công
        CheckoutCompletePage checkoutCompletePage = new CheckoutCompletePage(driver);
        String expectedMessage = "Thank you for your order!";
        Assertions.assertEquals(expectedMessage, checkoutCompletePage.getSuccessMessage(),
                "Luồng mua hàng thất bại, thông báo thành công không khớp!");
    }

    @Test
    @DisplayName("TC_CHECKOUT_06 - Verify tên sản phẩm hiển thị đúng ở trang Overview")
    public void testProductNameInOverview() {
        loginAndAddToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutInfoPage checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutInfoPage.enterShippingInfo("Test", "User", "12345");

        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(driver);
        Assertions.assertEquals("Sauce Labs Backpack", overviewPage.getItemName(),
                "Tên sản phẩm trên trang Overview không đúng!");
    }

    // ==================== NEGATIVE TEST CASES (Decision Table) ====================

    @Test
    @DisplayName("TC_CHECKOUT_02 - Checkout không nhập First Name → hiển thị lỗi")
    public void testCheckoutWithoutFirstName() {
        loginAndAddToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        // Chỉ nhập Last Name và Zip, bỏ trống First Name
        CheckoutInfoPage checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutInfoPage.enterShippingInfo("", "Nguyen", "700000");

        String expectedError = "Error: First Name is required";
        Assertions.assertEquals(expectedError, checkoutInfoPage.getErrorMessage(),
                "Thông báo lỗi thiếu First Name không đúng!");
    }

    @Test
    @DisplayName("TC_CHECKOUT_03 - Checkout không nhập Last Name → hiển thị lỗi")
    public void testCheckoutWithoutLastName() {
        loginAndAddToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutInfoPage checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutInfoPage.enterShippingInfo("Twan", "", "700000");

        String expectedError = "Error: Last Name is required";
        Assertions.assertEquals(expectedError, checkoutInfoPage.getErrorMessage(),
                "Thông báo lỗi thiếu Last Name không đúng!");
    }

    @Test
    @DisplayName("TC_CHECKOUT_04 - Checkout không nhập Zip Code → hiển thị lỗi")
    public void testCheckoutWithoutZipCode() {
        loginAndAddToCart();

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutInfoPage checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutInfoPage.enterShippingInfo("Twan", "Nguyen", "");

        String expectedError = "Error: Postal Code is required";
        Assertions.assertEquals(expectedError, checkoutInfoPage.getErrorMessage(),
                "Thông báo lỗi thiếu Zip Code không đúng!");
    }

    @Test
    @DisplayName("TC_CHECKOUT_05 - Xóa sản phẩm khỏi giỏ hàng → giỏ hàng trống")
    public void testRemoveItemFromCart() {
        loginAndAddToCart();

        CartPage cartPage = new CartPage(driver);

        // Verify giỏ hàng có sản phẩm
        Assertions.assertTrue(cartPage.hasItems(), "Giỏ hàng phải có sản phẩm!");

        // Xóa sản phẩm
        cartPage.removeItem();

        // Verify giỏ hàng trống
        Assertions.assertFalse(cartPage.hasItems(), "Giỏ hàng phải trống sau khi xóa!");
    }
}
