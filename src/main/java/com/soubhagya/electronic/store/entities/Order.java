package com.soubhagya.electronic.store.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an Order entity in an e-commerce application.
 * This class is used to capture details about customer orders,
 * including order status, payment status, order amount, billing information,
 * and associated user and items.
 *
 * The Order class is mapped to a database table named "orders".
 * It has relationships with the User and OrderItem entities.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "orders")
public class Order {

    @Id
    private String orderId;

    //PENDING, DISPATCHED, DELIVERED
    //enum
    private String orderStatus;

    //NOT-PAID, PAID
    private String paymentStatus;

    private double orderAmount;

    @Column(length = 1000)
    private String billingAddress;

    private String billingPhone;

    private String billingName;

    private Date orderedDate;

    private Date deliveredDate;

    //user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    //add this to get user information with order
    private String razoryPayOrderId;
    private String paymentId;

}
