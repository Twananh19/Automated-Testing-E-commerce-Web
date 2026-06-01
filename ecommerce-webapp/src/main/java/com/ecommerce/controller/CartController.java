package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        String username = principal.getName();
        Cart cart = cartService.getCart(username);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(username));
        model.addAttribute("cartEmpty", cart.getItems().isEmpty());
        model.addAttribute("username", username);
        return "cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, Principal principal) {
        cartService.removeFromCart(principal.getName(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return "redirect:/cart";
    }
}
