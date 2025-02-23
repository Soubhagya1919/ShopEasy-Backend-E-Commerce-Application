package com.soubhagya.electronic.store.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a data transfer object for a shopping cart containing a list of items,
 * the user associated with the cart, and metadata about the cart's creation.
 *
 * Attributes:
 * - cartId: Unique identifier for the cart.
 * - createdAt: The date and time when the cart was created.
 * - user: The user who owns the cart, represented by a UserDto.
 * - items: A list of items contained in the cart, represented by CartItemDto objects.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartDto {

    private String cartId;

    private Date createdAt;

    private UserDto user;

    private List<CartItemDto> items = new ArrayList<>();

}
