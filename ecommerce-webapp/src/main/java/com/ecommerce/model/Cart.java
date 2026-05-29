package com.ecommerce.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private String userId;
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(String userId) {
        this.userId = userId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public void addItem(CartItem item) {
        for (CartItem existing : items) {
            if (existing.getProductId().equals(item.getProductId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public boolean removeItem(Long productId) {
        return items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public double getTotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }
}
