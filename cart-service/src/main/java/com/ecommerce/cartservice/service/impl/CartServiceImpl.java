package com.ecommerce.cartservice.service.impl;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.ProductResponse;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        CartResponse response = new CartResponse();
        response.setUserId(userId);

        final double[] total = {0};

        var detailsList = cart.getItems().stream().map(item -> {
            ProductResponse product = productClient.getProductById(item.getProductId());

            CartResponse.CartItemDetails details = new CartResponse.CartItemDetails();
            details.setProductId(item.getProductId());
            details.setProductName(product.getName());
            details.setPrice(product.getPrice());
            details.setQuantity(item.getQuantity());
            details.setSubtotal(product.getPrice() * item.getQuantity());

            total[0] += details.getSubtotal();
            return details;
        }).toList();

        response.setItems(detailsList);
        response.setTotalAmount(total[0]);
        return response;
    }

    @Override
    public void addItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        cart.addItem(productId);
        cartRepository.save(cart);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.removeItem(productId);
            cartRepository.save(cart);
        });
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }
}
