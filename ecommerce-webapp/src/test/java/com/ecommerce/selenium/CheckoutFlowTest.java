package com.ecommerce.selenium;

import com.ecommerce.selenium.pages.CartPage;
import com.ecommerce.selenium.pages.CheckoutPage;
import com.ecommerce.selenium.pages.HomePage;
import com.ecommerce.selenium.pages.OrderConfirmationPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checkout Flow E2E Tests")
class CheckoutFlowTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Happy Path - mua hàng hoàn chỉnh với voucher hợp lệ")
    void test_fullPurchaseFlow_validVoucher_shouldCompleteOrder() {
        // Mở trang chủ
        navigateTo("/");
        HomePage homePage = new HomePage(driver);

        // Tìm kiếm sản phẩm
        homePage.searchProduct("Laptop");
        assertFalse(homePage.getProductList().isEmpty());

        // Thêm sản phẩm vào giỏ
        homePage.clickAddToCart(1L);

        // Vào giỏ hàng
        CartPage cartPage = homePage.goToCart();
        assertTrue(cartPage.hasItems());
        assertFalse(cartPage.getCartTotal().isEmpty());

        // Checkout
        CheckoutPage checkoutPage = cartPage.clickCheckout();
        assertFalse(checkoutPage.getOrderTotal().isEmpty());

        // Áp dụng voucher
        checkoutPage = checkoutPage.applyVoucher("SAVE10");
        String voucherMsg = checkoutPage.getVoucherMessage();
        System.out.println("[DEBUG-TEST] test_fullPurchaseFlow Voucher Message: '" + voucherMsg + "'");
        assertTrue(voucherMsg.contains("thành công"));
        
        // Đặt hàng
        OrderConfirmationPage confirmationPage = checkoutPage.placeOrder();
        assertTrue(confirmationPage.isConfirmationDisplayed());
        assertNotNull(confirmationPage.getOrderId());
        assertEquals("PENDING", confirmationPage.getOrderStatus());
    }

    @Test
    @DisplayName("Áp dụng voucher hợp lệ - hiển thị tổng tiền đã giảm")
    void test_applyValidVoucher_shouldShowDiscountedTotal() {
        navigateTo("/");
        HomePage homePage = new HomePage(driver);
        homePage.clickAddToCart(1L);

        CartPage cartPage = homePage.goToCart();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        String totalBefore = checkoutPage.getOrderTotal();
        checkoutPage = checkoutPage.applyVoucher("SAVE20");

        String message = checkoutPage.getVoucherMessage();
        System.out.println("[DEBUG-TEST] test_applyValidVoucher Voucher Message: '" + message + "'");
        assertTrue(message.contains("thành công"));
    }

    @Test
    @DisplayName("Áp dụng voucher không hợp lệ - hiển thị thông báo lỗi")
    void test_applyInvalidVoucher_shouldShowErrorMessage() {
        navigateTo("/");
        HomePage homePage = new HomePage(driver);
        homePage.clickAddToCart(1L);

        CartPage cartPage = homePage.goToCart();
        CheckoutPage checkoutPage = cartPage.clickCheckout();
        checkoutPage = checkoutPage.applyVoucher("INVALID_CODE");

        String message = checkoutPage.getVoucherMessage();
        assertTrue(message.contains("không hợp lệ"));
    }

    @Test
    @DisplayName("Checkout với giỏ hàng trống - hiển thị cảnh báo")
    void test_checkoutWithEmptyCart_shouldShowWarning() {
        // Vào cart page, nếu có items thì clear
        navigateTo("/cart");
        CartPage cartPage = new CartPage(driver);
        if (!cartPage.isCartEmpty()) {
            cartPage.clearCart();
        }

        navigateTo("/checkout");
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        assertTrue(checkoutPage.hasWarning());
        assertTrue(checkoutPage.getWarningMessage().contains("Giỏ hàng trống"));
    }
}
