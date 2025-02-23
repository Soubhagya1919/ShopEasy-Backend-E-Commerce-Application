package com.soubhagya.electronic.store.dtos;

import com.soubhagya.electronic.store.util.ImageNameValid;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a data transfer object for a product category.
 * This class contains essential information needed to handle
 * categories in the application, such as the category's unique
 * identifier, title, description, and cover image.
 *
 * Attributes:
 * - categoryId: A unique identifier for the category.
 * - title: The name of the category, must be at least four characters long.
 * - description: A brief description of the category.
 * - coverImage: The filename for the category's cover image, which must validate against the custom ImageNameValid constraint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CategoryDto {

    private String categoryId;

    @NotBlank(message = "title is required !!")
    @Size(min = 4, message = "title must be of minimum four characters !!")
    private String title;

    @NotBlank(message = "Description required !!")
    private String description;

    //@NotBlank(message = "cover image required")
    @ImageNameValid
    private String coverImage;
}
