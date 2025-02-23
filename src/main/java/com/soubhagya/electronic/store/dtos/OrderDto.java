package com.soubhagya.electronic.store.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a data transfer object for an order in an electronic store.
 * Contains details regarding order identification, status, payment, billing,
 * and the date associated with the order lifecycle. It also holds information
 * regarding order items and payment identifiers for tracking.
 *
 * Attributes:
 * - orderId: A unique identifier for the order.
 * - orderStatus: The current status of the order, default is "PENDING".
 * - paymentStatus: The status of the payment associated with the order, default is "NOTPAID".
 * - orderAmount: The total amount for the order.
 * - billingAddress: The billing address for the order.
 * - billingPhone: The contact phone number for billing.
 * - billingName: The name associated with the billing information.
 * - orderedDate: The date and time when the order was placed.
 * - deliveredDate: The date and time when the order was delivered.
 * - orderItems: A list of items included in the order, represented by OrderItemDto objects.
 * - razoryPayOrderId: An identifier for the order in the Razorpay payment system.
 * - paymentId: The identifier for the payment transaction.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDto {

    private String orderId;

    private String orderStatus="PENDING";

    private String paymentStatus="NOTPAID";

    private double orderAmount;

    private String billingAddress;

    private String billingPhone;

    private String billingName;

    private Date orderedDate;

    private Date deliveredDate;

    //private UserDto user;

    private List<OrderItemDto> orderItems = new ArrayList<>();

    //add this to get user information with order
    private String razoryPayOrderId;
    private String paymentId;
}
