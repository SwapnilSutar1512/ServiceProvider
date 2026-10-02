package com.localservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a new Category.
 */

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String categoryName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Active status is required")
    private Boolean active;

	public CategoryRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CategoryRequest(
			@NotBlank(message = "Category name is required") @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters") String categoryName,
			@Size(max = 500, message = "Description must not exceed 500 characters") String description,
			@NotNull(message = "Active status is required") Boolean active) {
		super();
		this.categoryName = categoryName;
		this.description = description;
		this.active = active;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
}
