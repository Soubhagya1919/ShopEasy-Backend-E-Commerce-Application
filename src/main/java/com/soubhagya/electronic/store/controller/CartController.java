package com.soubhagya.electronic.store.controller;

import com.soubhagya.electronic.store.constants.AppConstants;
import com.soubhagya.electronic.store.dtos.AddItemToCartRequest;
import com.soubhagya.electronic.store.dtos.ApiResponseMessage;
import com.soubhagya.electronic.store.dtos.CartDto;
import com.soubhagya.electronic.store.services.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * The CartController class handles HTTP requests related to cart operations
 * such as adding items, removing items, clearing the cart, and retrieving the cart details.
 *
 * This controller provides endpoints that are secured and require authorization.
 * Specifically, only users with roles defined in the application constants (e.g., ROLE_NORMAL or ROLE_ADMIN)
 * are authorized to perform cart operations.
 *
 * The controller is annotated with @RestController, making it a web controller in a Spring MVC context.
 * All cart-related endpoints are prefixed with "/carts" as defined by the @RequestMapping annotation.
 *
 * Each method in this controller corresponds to a specific cart operation and returns a ResponseEntity.
 */
@RestController
@RequestMapping("/carts")
@Tag(name = "Cart Controller", description = "This is cart api for cart operations")
@SecurityRequirement(name = "scheme1")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds an item to a user's cart based on the provided request data.
     * This operation requires the user to have either a normal or admin role.
     *
     * @param userId the identifier of the user whose cart is to be modified
     * @param request the details of the item to be added to the cart
     * @return a ResponseEntity containing the updated CartDto
     */
    //add items to cart
    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_NORMAL + "', '" + AppConstants.ROLE_ADMIN + "')")
    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable String userId, @RequestBody AddItemToCartRequest request){
        CartDto cartDto = cartService.addItemToCart(userId, request);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    /**
     * Removes an item from the cart associated with the specified user.
     *
     * @param userId the ID of the user whose cart item is to be removed
     * @param itemId the ID of the item to be removed from the cart
     * @return a ResponseEntity containing an ApiResponseMessage indicating the result of the operation
     */
    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_NORMAL + "', '" + AppConstants.ROLE_ADMIN + "')")
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId, @PathVariable int itemId){
        cartService.removeItemFromCart(userId, itemId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Item is removed !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Clears all items from the cart associated with the specified user.
     *
     * @param userId The unique identifier of the user whose cart is to be cleared.
     * @return A ResponseEntity containing ApiResponseMessage detailing the result of the operation,
     * indicating success and providing an appropriate HTTP status.
     */
    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_NORMAL + "', '" + AppConstants.ROLE_ADMIN + "')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId){
        cartService.clearCart(userId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Cart is cleared !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves the shopping cart details for a specific user given their user ID.
     * The user must have a role of either ROLE_NORMAL or ROLE_ADMIN to access this functionality.
     *
     * @param userId the ID of the user whose cart is to be retrieved
     * @return a ResponseEntity containing the CartDto, which holds the details of the user's cart,
     *         along with an HTTP status code of OK
     */
    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_NORMAL + "', '" + AppConstants.ROLE_ADMIN + "')")
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String userId){
        CartDto cartDto = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

}
