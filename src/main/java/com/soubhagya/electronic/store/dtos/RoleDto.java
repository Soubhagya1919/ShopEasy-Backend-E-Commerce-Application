package com.soubhagya.electronic.store.dtos;

import lombok.Data;

/**
 * Represents a data transfer object for a user role in the system.
 * This class encapsulates the details of a role, including its unique identifier
 * and name, which are essential for defining user permissions and access levels.
 *
 * Attributes:
 * - roleId: A unique identifier for the role.
 * - name: The name of the role.
 */
@Data
public class RoleDto {
    private String roleId;
    private String name;
}
