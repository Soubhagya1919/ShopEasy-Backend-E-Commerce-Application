package com.soubhagya.electronic.store.dtos;

import lombok.*;

import java.util.Date;

/**
 * Represents a request to update order details in an electronic store system.
 * This class encapsulates the fields that can be modified in an existing order,
 * such as order status, payment status, and billing information, along with the
 * delivery date.
 *
 * Attributes:
 * - orderStatus: A string representing the current status of the order.
 * - paymentStatus: A string representing the current status of the payment for the order.
 * - billingName: A string representing the name of the individual or entity to be billed.
 * - billingPhone: A string representing the contact phone number for billing purposes.
 * - billingAddress: A string representing the address where billing should be sent.
 * - deliveredDate: A Date object indicating when the order was delivered.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateRequest {

    private String orderStatus;
    private String paymentStatus;

    private String billingName;

    private String billingPhone;

    private String billingAddress;

    private Date deliveredDate;


}
