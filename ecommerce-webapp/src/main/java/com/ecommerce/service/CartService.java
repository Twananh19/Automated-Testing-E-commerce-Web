package com.ecommerce.service;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.InvalidInputException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {

    private final ConcurrentHashMap<String, Cart> carts = new ConcurrentHashMap<>();
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Thêm sản phẩm vào giỏ hàng. Kiểm tra tồn kho trước khi thêm.
     */
    public Cart addToCart(String userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidInputException("Sản phẩm không tồn tại: " + productId));

        if (quantity <= 0) {
            throw new InvalidInputException("Số lượng phải lớn hơn 0");
        }

        Cart cart = carts.computeIfAbsent(userId, Cart::new);

        int currentInCart = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (currentInCart + quantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Không đủ tồn kho. Còn lại: " + product.getStock() +
                            ", trong giỏ: " + currentInCart + ", yêu cầu thêm: " + quantity);
        }

        CartItem cartItem = new CartItem(productId, product.getName(), product.getPrice(), quantity);
        cart.addItem(cartItem);
        return cart;
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng.
     */
    public Cart removeFromCart(String userId, Long productId) {
        Cart cart = carts.get(userId);
        if (cart == null) {
            throw new InvalidInputException("Giỏ hàng của người dùng không tồn tại: " + userId);
        }

        boolean removed = cart.removeItem(productId);
        if (!removed) {
            throw new InvalidInputException("Sản phẩm không có trong giỏ hàng: " + productId);
        }
        return cart;
    }

    /**
     * Xóa toàn bộ giỏ hàng.
     */
    public void clearCart(String userId) {
        Cart cart = carts.get(userId);
        if (cart != null) {
            cart.clear();
        }
    }

    /**
     * Tính tổng tiền giỏ hàng.
     */
    public double getCartTotal(String userId) {
        Cart cart = carts.get(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            return 0.0;
        }
        return cart.getTotal();
    }

    /**
     * Lấy giỏ hàng theo userId.
     */
    public Cart getCart(String userId) {
        return carts.computeIfAbsent(userId, Cart::new);
    }
}
