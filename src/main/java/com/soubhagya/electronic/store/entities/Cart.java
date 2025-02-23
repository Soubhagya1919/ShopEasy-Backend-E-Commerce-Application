package com.soubhagya.electronic.store.entities;

import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a shopping cart in an e-commerce application.
 * Each cart is associated with a unique user and contains multiple
 * cart items.
 * This class is an entity and is mapped to a database table named "cart".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    private String cartId;

    private Date createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}
