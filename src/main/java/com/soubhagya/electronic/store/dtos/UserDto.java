package com.soubhagya.electronic.store.dtos;

import com.soubhagya.electronic.store.constants.Providers;
import com.soubhagya.electronic.store.util.ImageNameValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Represents a data transfer object for a user within the system.
 * This class encapsulates user information including credentials,
 * personal details, and user roles, which are essential for managing
 * user accounts and authentication.
 *
 * Attributes:
 * - userId: Unique identifier for the user.
 * - name: Username satisfying specific format constraints.
 * - email: User's email address which must comply with a defined pattern.
 * - password: The user's password, which is a required attribute.
 * - gender: Gender of the user with restricted length constraints.
 * - about: A brief description about the user. This field cannot be empty.
 * - roles: A list of roles associated with the user, represented by RoleDto objects.
 * - imageName: Name of the user's profile image validated by a custom constraint.
 * - provider: The authentication provider used for user login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String userId;

    /**
     * Represents the username of a newly created user.
     * This username is subject to specific format constraints,
     * requiring a length between 3 and 19 characters.
     * The field is read-only as specified within the schema configuration.
     */
    @Size(min = 3, max = 19, message = "Invalid name format !!")
    @Schema(name = "username", accessMode = Schema.AccessMode.READ_ONLY, description = "username of newly created user!")
    private String name;

    /**
     * Represents the user's email address within the system.
     *
     * This email attribute must adhere to specific validation constraints:
     * - It cannot be left blank and is a required field.
     * - It must match a specific pattern ensuring the format adheres to standard email guidelines,
     *   starting with lowercase letters or digits, followed by optional hyphens, underscores, or dots,
     *   and ending with a domain name.
     *
     * The annotations used for validation are:
     * - @Email to ensure the string follows a valid email structure.
     * - @Pattern to impose additional regex constraints on the email format.
     * - @NotBlank ensures that the email is provided and not empty in any scenarios where it is mandatory.
     */
    @Email(message = "Email id cannot be blank !!")
    @Pattern(regexp = "^[a-z0-9][-a-z0-9._]+@([-a-z0-9]+\\.)+[a-z]{2,5}$", message = "Email is invalid !!")
    @NotBlank(message = "Email is a mandatory field, cannot be empty !!")
    private String email;

    @NotBlank(message = "Password is required !!")
//    @Pattern(
//            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{9,}$",
//            message = "Password must be at least 9 characters long and include at least one uppercase letter, one digit, and one special character."
//    )
    private String password;

    @Size(min = 4, max = 6, message = "Invalid gender !!")
    private String gender;

    @NotBlank(message = "About cannot be empty !!")
    private String about;

    private List<RoleDto> roles;
    //@Pattern
    //Custom validator
    @ImageNameValid
    private String imageName;

    private Providers provider;

}
