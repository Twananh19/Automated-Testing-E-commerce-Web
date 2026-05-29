package com.ecommerce.service;

import com.ecommerce.exception.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private static final Map<String, Double> VOUCHERS = new HashMap<>();
    private static final Map<String, Boolean> EXPIRED_VOUCHERS = new HashMap<>();

    static {
        VOUCHERS.put("SAVE10", 10.0);
        VOUCHERS.put("SAVE20", 20.0);
        VOUCHERS.put("SAVE50", 50.0);
        EXPIRED_VOUCHERS.put("EXPIRED2024", true);
    }

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Tạo đơn hàng từ danh sách cart items. Kiểm tra tồn kho và giảm stock.
     */
    @Transactional
    public Order createOrder(String userId, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new InvalidInputException("Giỏ hàng trống, không thể tạo đơn hàng");
        }

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new InvalidInputException(
                            "Sản phẩm không tồn tại: " + cartItem.getProductId()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Sản phẩm '" + product.getName() + "' không đủ tồn kho. " +
                                "Còn lại: " + product.getStock() + ", yêu cầu: " + cartItem.getQuantity());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        List<OrderItem> orderItems = cartItems.stream()
                .map(ci -> new OrderItem(ci.getProductId(), ci.getProductName(), ci.getPrice(), ci.getQuantity()))
                .collect(Collectors.toList());

        double total = calculateOrderTotal(orderItems);
        Order order = new Order(userId, orderItems, total);
        return orderRepository.save(order);
    }

    /**
     * Hủy đơn hàng. Chỉ cho phép khi status = PENDING.
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Không thể hủy đơn hàng với trạng thái: " + order.getStatus());
        }

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Tính tổng tiền đơn hàng. Áp dụng discount theo số lượng:
     * >= 10 items: giảm 5%, >= 20 items: giảm 10%.
     */
    public double calculateOrderTotal(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }

        double subtotal = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        int totalQuantity = items.stream().mapToInt(OrderItem::getQuantity).sum();

        double discountRate = 0.0;
        if (totalQuantity >= 20) {
            discountRate = 0.10;
        } else if (totalQuantity >= 10) {
            discountRate = 0.05;
        }

        return subtotal * (1 - discountRate);
    }

    /**
     * Áp dụng mã voucher cho đơn hàng.
     */
    @Transactional
    public Order applyVoucher(Long orderId, String voucherCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (EXPIRED_VOUCHERS.containsKey(voucherCode)) {
            throw new InvalidInputException("Mã voucher đã hết hạn: " + voucherCode);
        }

        Double discountPercent = VOUCHERS.get(voucherCode);
        if (discountPercent == null) {
            throw new InvalidInputException("Mã voucher không hợp lệ: " + voucherCode);
        }

        double discountedTotal = order.getTotalAmount() * (1 - discountPercent / 100.0);
        order.setTotalAmount(discountedTotal);
        return orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng với ID: " + id));
    }
}
