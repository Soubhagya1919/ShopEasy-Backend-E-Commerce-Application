package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.dtos.AddItemToCartRequest;
import com.soubhagya.electronic.store.dtos.CartDto;
import com.soubhagya.electronic.store.entities.Cart;
import com.soubhagya.electronic.store.entities.CartItem;
import com.soubhagya.electronic.store.entities.Product;
import com.soubhagya.electronic.store.entities.User;
import com.soubhagya.electronic.store.exceptions.BadApiRequestException;
import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.repositories.CartItemRepository;
import com.soubhagya.electronic.store.repositories.CartRepository;
import com.soubhagya.electronic.store.repositories.ProductRepository;
import com.soubhagya.electronic.store.repositories.UserRepository;
import com.soubhagya.electronic.store.services.CartService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the CartService interface for managing shopping cart operations.
 * This class handles the addition and removal of items in a user's shopping cart, as well as
 * clearing the cart and retrieving the cart details for a specific user.
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ModelMapper modelMapper;

    public CartServiceImpl(ProductRepository productRepository, UserRepository userRepository, CartRepository cartRepository, ModelMapper modelMapper, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Adds an item to the user's shopping cart. If the item exists in the cart, the quantity is updated.
     * If the item does not exist, it is added to the cart.
     *
     * @param userId the ID of the user whose cart will be updated
     * @param request a request object containing details about the item to be added to the cart,
     *                including the product ID and the desired quantity
     * @return a CartDto object representing the updated state of the user's cart
     * @throws BadApiRequestException if the requested quantity is invalid
     * @throws ResourceNotFoundException if the product or user is not found in the database
     */
    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if(quantity <= 0) {
            throw new BadApiRequestException("Requested quantity is not valid !!");
        }
        //fetch the product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found !!"));
        //fetch the user from db
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found !!"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCartId(UUID.randomUUID().toString());
                    newCart.setCreatedAt(new Date());
                    newCart.setUser(user);
                    return newCart;
                });

        //perform cart operation
        //check: if cartItem already exists in cart only increase the quantity
//        AtomicReference<Boolean> updated = new AtomicReference<>(false);
//
//        List<CartItem> items = cart.getItems();
//        items = items.stream().map(item -> {
//            if (item.getProduct().getProductId().equals(productId)) {
//                //item already present in cart
//                item.setQuantity(quantity);
//                item.setTotalPrice(quantity * product.getDiscountedPrice());
//                updated.set(true);
//            }
//            return item;
//        }).collect(Collectors.toList());

        List<CartItem> items = cart.getItems();
        boolean itemUpdated = false;

        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                // Check if the price has changed
                if (item.getProduct().getDiscountedPrice() != product.getDiscountedPrice()) {
                    item.setTotalPrice(quantity * product.getDiscountedPrice());
                }
                item.setQuantity(quantity);
                itemUpdated = true;
                break;
            }
        }

        //cart.setItems(updatedItems);

        //create items
        if(!itemUpdated){
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();
            cart.getItems().add(cartItem);
        }

        cart.setUser(user);

        Cart updatedCart = cartRepository.save(cart);
        //cartItems will also get updated because CascadeType is ALL

        log.info("Adding product {} to cart for user {}", productId, userId);
        return modelMapper.map(updatedCart, CartDto.class);
    }

    /**
     * Removes an item from the user's shopping cart.
     *
     * @param userId the ID of the user whose cart is being modified
     * @param cartItem the ID of the cart item to be removed
     */
    @Override
    public void removeItemFromCart(String userId, int cartItem) {
        CartItem cartItem1 = cartItemRepository.findById(cartItem).orElseThrow(() -> new ResourceNotFoundException("Cart Item not found in db !!"));
        cartItemRepository.delete(cartItem1);
    }

    /**
     * Clears all items from the cart associated with the specified user. This method retrieves the user's
     * cart and removes all items from it, effectively emptying the cart. If the user or the cart does not
     * exist, a ResourceNotFoundException is thrown.
     *
     * @param userId the unique identifier of the user whose cart is to be cleared
     */
    @Override
    public void clearCart(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found !!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user was not found !!"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Retrieves the shopping cart associated with a specific user.
     *
     * @param userId the identifier of the user whose cart is to be retrieved
     * @return the CartDto object containing the details of the user's shopping cart
     * @throws ResourceNotFoundException if the user or the cart is not found
     */
    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found !!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user was not found !!"));

        return modelMapper.map(cart, CartDto.class);
    }
}
