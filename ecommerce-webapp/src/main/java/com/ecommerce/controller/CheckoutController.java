package com.ecommerce.controller;

import com.ecommerce.exception.InvalidInputException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.Order;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;

    private static final String DEFAULT_USER = "user1";

    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        Cart cart = cartService.getCart(DEFAULT_USER);
        if (cart.getItems().isEmpty()) {
            model.addAttribute("warning", "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi thanh toán.");
            model.addAttribute("cart", cart);
            model.addAttribute("orderTotal", 0.0);
            return "checkout";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("orderTotal", cartService.getCartTotal(DEFAULT_USER));
        return "checkout";
    }

    @PostMapping("/checkout/apply-voucher")
    public String applyVoucher(@RequestParam String voucherCode, RedirectAttributes redirectAttributes) {
        try {
            Cart cart = cartService.getCart(DEFAULT_USER);
            if (cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("voucherError", "Giỏ hàng trống");
                return "redirect:/checkout";
            }

            Order tempOrder = orderService.createOrder(DEFAULT_USER, cart.getItems());
            orderService.applyVoucher(tempOrder.getId(), voucherCode);
            Order updatedOrder = orderService.findById(tempOrder.getId());

            redirectAttributes.addFlashAttribute("voucherSuccess",
                    "Áp dụng voucher thành công! Giảm giá đã được áp dụng.");
            redirectAttributes.addFlashAttribute("discountedTotal", updatedOrder.getTotalAmount());
            redirectAttributes.addFlashAttribute("orderId", updatedOrder.getId());
            redirectAttributes.addFlashAttribute("voucherCode", voucherCode);
        } catch (InvalidInputException e) {
            redirectAttributes.addFlashAttribute("voucherError", e.getMessage());
        }
        return "redirect:/checkout";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(@RequestParam(required = false) Long orderId,
                             RedirectAttributes redirectAttributes) {
        try {
            Order order;
            if (orderId != null) {
                order = orderService.findById(orderId);
            } else {
                Cart cart = cartService.getCart(DEFAULT_USER);
                if (cart.getItems().isEmpty()) {
                    redirectAttributes.addFlashAttribute("warning", "Giỏ hàng trống!");
                    return "redirect:/checkout";
                }
                order = orderService.createOrder(DEFAULT_USER, cart.getItems());
            }

            cartService.clearCart(DEFAULT_USER);
            return "redirect:/order-confirmation/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-confirmation/{orderId}")
    public String orderConfirmation(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
