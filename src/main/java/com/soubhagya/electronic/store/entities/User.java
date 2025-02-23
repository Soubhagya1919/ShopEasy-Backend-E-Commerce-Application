package com.soubhagya.electronic.store.entities;

import com.soubhagya.electronic.store.constants.Providers;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The User class represents a user entity in the system. It implements the UserDetails interface
 * to integrate with Spring Security for authentication and authorization purposes.
 *
 * This entity is mapped to a database table named "users" and includes fields to store
 * user-specific details like userId, name, email, password, gender, a brief description (about),
 * image name, and authentication provider.
 *
 * The User class has several relationships with other entities:
 * - One-to-Many with Order: Represents the orders placed by the user.
 * - Many-to-Many with Role: Represents the roles assigned to the user, used for permission management.
 * - One-to-One with Cart: Represents the shopping cart associated with the user.
 *
 * Each User can have multiple roles, and these roles are crucial for determining the user's
 * authorities in the application.
 *
 * It uses Lombok annotations for boilerplate code reduction, such as Getters, Setters,
 * Constructors, and Builder pattern implementation.
 *
 * The UserDetails interface methods are implemented to provide the essential information
 * required by the Spring Security framework to authenticate and authorize users.
 *
 * Fields:
 * - userId: A unique identifier for the user.
 * - name: The full name of the user.
 * - email: The user's unique email address, used as a username for authentication.
 * - password: The user's password, stored in an encrypted form.
 * - gender: The user's gender.
 * - about: A brief description or information about the user.
 * - imageName: The name of the image associated with the user.
 * - provider: The authentication provider used by the user.
 * - orders: The list of orders placed by the user.
 * - roles: The set of roles assigned to the user.
 * - cart: The shopping cart associated with the user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private String userId;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "user_password", length = 500)
    private String password;

    private String gender;

    @Column(length = 1000)
    private String about;

    @Column(name = "user_image_name")
    private String imageName;

    @Enumerated(EnumType.STRING)
    private Providers provider;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key referring to User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Foreign key referring to Role
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Cart cart;

    //important to get the roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities;
        authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //syntax for invoking the default method implementation
        //from the parent interface
        //return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
