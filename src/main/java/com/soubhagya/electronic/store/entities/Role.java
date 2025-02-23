package com.soubhagya.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Role in the system. This class is used to manage user roles such as
 * NORMAL or ADMIN. It is an entity mapped to a database table named "roles".
 *
 * Each Role can be associated with multiple User entities and is represented by a unique
 * roleId and a name.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private String roleId;
    private String name; //NORMAL, ADMIN

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();
}
