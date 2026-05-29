package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CartService cartService;

    private static final String DEFAULT_USER = "user1";

    public HomeController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchByName(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            products = productService.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("cartItemCount", cartService.getCart(DEFAULT_USER).getItems().size());
        return "index";
    }

    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable Long productId) {
        cartService.addToCart(DEFAULT_USER, productId, 1);
        return "redirect:/";
    }
}
