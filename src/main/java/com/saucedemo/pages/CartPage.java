package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CartPage - Page Object đại diện cho trang Giỏ hàng.
 * URL: https://www.saucedemo.com/cart.html
 *
 * Hiển thị danh sách sản phẩm đã thêm vào giỏ, cho phép tiếp tục checkout.
 */
public class CartPage extends BasePage {

    // === LOCATORS ===
    private final By checkoutBtn = By.cssSelector("[data-testid='checkout-button']");                           // Nút Checkout
    private final By continueShoppingBtn = By.id("continue-shopping");          // Nút Continue Shopping
    private final By cartItem = By.cssSelector(".cart-item");                    // Phần tử sản phẩm trong giỏ
    private final By removeBtn = By.cssSelector("[data-testid^='remove-']");        // Nút Remove sản phẩm

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // === ACTION METHODS ===

    /** Click nút Checkout để chuyển sang bước điền thông tin */
    public void clickCheckout() {
        click(checkoutBtn);
    }

    /** Kiểm tra giỏ hàng có sản phẩm hay không */
    public boolean hasItems() {
        return isElementDisplayed(cartItem);
    }

    /** Xóa sản phẩm khỏi giỏ hàng */
    public void removeItem() {
        click(removeBtn);
    }
}
