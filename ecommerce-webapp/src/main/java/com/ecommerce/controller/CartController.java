package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    private static final String DEFAULT_USER = "user1";

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(Model model) {
        Cart cart = cartService.getCart(DEFAULT_USER);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(DEFAULT_USER));
        model.addAttribute("cartEmpty", cart.getItems().isEmpty());
        return "cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(DEFAULT_USER, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart() {
        cartService.clearCart(DEFAULT_USER);
        return "redirect:/cart";
    }
}
