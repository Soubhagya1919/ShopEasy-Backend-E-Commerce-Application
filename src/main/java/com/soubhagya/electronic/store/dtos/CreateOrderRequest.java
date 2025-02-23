package com.soubhagya.electronic.store.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents a request to create a new order in the system. This class
 * contains all the necessary information needed to initiate the creation
 * of an order, including cart identification, user identification, order
 * and payment status, along with billing details.
 *
 * Attributes:
 * - cartId: The unique identifier of the shopping cart associated with the order.
 * - userId: The unique identifier of the user who is placing the order.
 * - orderStatus: The status of the order, defaulting to "PENDING".
 * - paymentStatus: The status of the payment, defaulting to "NOTPAID".
 * - billingAddress: The identifier of the billing address for the order.
 * - billingPhone: The phone number associated with the billing information.
 * - billingName: The name associated with the billing information.
 *
 * Validation:
 * - Each of cartId, userId, billingAddress, billingPhone, and billingName must not be blank.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateOrderRequest {

    @NotBlank(message = "Cart id is required !!")
    private String cartId;

    @NotBlank(message = "User id is required !!")
    private String userId;

    private String orderStatus="PENDING";

    private String paymentStatus="NOTPAID";

    @NotBlank(message = "Address id is required !!")
    private String billingAddress;

    @NotBlank(message = "Phone number is required !!")
    private String billingPhone;

    @NotBlank(message = "Billing name is required !!")
    private String billingName;

}
