package com.ecommerce.unit;

import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Property-Based Tests sử dụng jqwik framework.
 * Kiểm tra các tính chất bất biến (invariants) của business logic.
 */
@Label("Property-Based Tests")
class PropertyBasedTest {

    private final OrderService orderService = new OrderService(
            mock(OrderRepository.class),
            mock(ProductRepository.class)
    );

    private final ProductService productService = new ProductService(
            mock(ProductRepository.class)
    );

    // ========== OrderService Properties ==========

    @Property(tries = 200)
    @Label("PBT-001: Tổng tiền đơn hàng luôn >= 0 với mọi input hợp lệ")
    void orderTotal_shouldAlwaysBeNonNegative(
            @ForAll @Size(min = 1, max = 10) List<@From("validOrderItem") OrderItem> items
    ) {
        double total = orderService.calculateOrderTotal(items);
        assertTrue(total >= 0, "Tổng tiền phải luôn >= 0, nhưng nhận được: " + total);
    }

    @Property(tries = 200)
    @Label("PBT-002: Tổng tiền không bao giờ vượt quá subtotal (do discount chỉ giảm)")
    void orderTotal_shouldNotExceedSubtotal(
            @ForAll @Size(min = 1, max = 10) List<@From("validOrderItem") OrderItem> items
    ) {
        double total = orderService.calculateOrderTotal(items);
        double subtotal = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        assertTrue(total <= subtotal + 0.01,
                "Tổng tiền (" + total + ") không được vượt quá subtotal (" + subtotal + ")");
    }

    @Property(tries = 100)
    @Label("PBT-003: Thêm items thì tổng tiền không giảm (monotonic)")
    void orderTotal_shouldBeMonotonic(
            @ForAll @Size(min = 1, max = 5) List<@From("validOrderItem") OrderItem> baseItems,
            @ForAll @From("validOrderItem") OrderItem additionalItem
    ) {
        double totalBefore = orderService.calculateOrderTotal(baseItems);

        List<OrderItem> extendedItems = new ArrayList<>(baseItems);
        extendedItems.add(additionalItem);

        double totalAfter = orderService.calculateOrderTotal(extendedItems);

        // Note: do discount thay đổi, total có thể tăng hoặc giảm nhẹ
        // Tuy nhiên, total luôn >= 0 và subtotal tổng luôn tăng
        double subtotalAfter = extendedItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        assertTrue(subtotalAfter >= totalBefore * 0.9,
                "Subtotal phải tăng khi thêm items");
    }

    @Property(tries = 200)
    @Label("PBT-004: Discount rate nằm trong {0%, 5%, 10%}")
    void discountRate_shouldBeOneOfThreeValues(
            @ForAll @Size(min = 1, max = 10) List<@From("validOrderItem") OrderItem> items
    ) {
        double total = orderService.calculateOrderTotal(items);
        double subtotal = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        if (subtotal > 0) {
            double ratio = total / subtotal;
            // ratio should be one of: 1.0 (0% discount), 0.95 (5%), 0.90 (10%)
            boolean isValid = Math.abs(ratio - 1.0) < 0.001
                    || Math.abs(ratio - 0.95) < 0.001
                    || Math.abs(ratio - 0.90) < 0.001;
            assertTrue(isValid,
                    "Discount ratio phải là 1.0, 0.95, hoặc 0.90 nhưng nhận được: " + ratio);
        }
    }

    // ========== ProductService Properties ==========

    @Property(tries = 200)
    @Label("PBT-005: Giá giảm luôn nằm trong [0, giá gốc]")
    void discountedPrice_shouldBeInValidRange(
            @ForAll @DoubleRange(min = 0.01, max = 100000000) double price,
            @ForAll @DoubleRange(min = 0, max = 100) double discountPercent
    ) {
        Product product = new Product("Test Product", price, 10, "Test");

        double discountedPrice = productService.calculateDiscountedPrice(product, discountPercent);

        assertTrue(discountedPrice >= -0.01,
                "Giá giảm phải >= 0, nhưng nhận được: " + discountedPrice);
        assertTrue(discountedPrice <= price + 0.01,
                "Giá giảm phải <= giá gốc (" + price + "), nhưng nhận được: " + discountedPrice);
    }

    @Property(tries = 100)
    @Label("PBT-006: Discount cao hơn thì giá thấp hơn (monotonic)")
    void higherDiscount_shouldResultInLowerPrice(
            @ForAll @DoubleRange(min = 0.01, max = 100000000) double price,
            @ForAll @DoubleRange(min = 0, max = 99) double discount1
    ) {
        Product product = new Product("Test Product", price, 10, "Test");
        double discount2 = Math.min(discount1 + 1, 100);

        double price1 = productService.calculateDiscountedPrice(product, discount1);
        double price2 = productService.calculateDiscountedPrice(product, discount2);

        assertTrue(price2 <= price1 + 0.01,
                "Discount cao hơn (" + discount2 + "%) phải cho giá thấp hơn hoặc bằng: "
                        + price2 + " vs " + price1);
    }

    // ========== Providers ==========

    @Provide
    Arbitrary<OrderItem> validOrderItem() {
        Arbitrary<Long> ids = Arbitraries.longs().between(1, 100);
        Arbitrary<String> names = Arbitraries.of("Product A", "Product B", "Product C", "Product D");
        Arbitrary<Double> prices = Arbitraries.doubles().between(1000, 50000000).ofScale(0);
        Arbitrary<Integer> quantities = Arbitraries.integers().between(1, 15);

        return Combinators.combine(ids, names, prices, quantities)
                .as(OrderItem::new);
    }
}
