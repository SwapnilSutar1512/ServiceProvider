package com.localservice.constants;

/**
 * Application constants used throughout the application.
 */
public class AppConstants {

    public static final String API_SUCCESS = "Success";
    public static final String API_ERROR = "Error";
    public static final String API_VALIDATION_ERROR = "Validation Error";

    // Message Constants
    public static final String CATEGORY_CREATED_SUCCESSFULLY = "Category created successfully";
    public static final String CATEGORY_UPDATED_SUCCESSFULLY = "Category updated successfully";
    public static final String CATEGORY_DELETED_SUCCESSFULLY = "Category deleted successfully";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CATEGORY_ALREADY_EXISTS = "Category already exists with this name";

    public static final String PROVIDER_CREATED_SUCCESSFULLY = "Service provider created successfully";
    public static final String PROVIDER_UPDATED_SUCCESSFULLY = "Service provider updated successfully";
    public static final String PROVIDER_DELETED_SUCCESSFULLY = "Service provider deleted successfully";
    public static final String PROVIDER_NOT_FOUND = "Service provider not found";

    // Pagination Constants
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_SORT_BY = "id";
    public static final String SORT_DIRECTION_ASC = "asc";
    public static final String SORT_DIRECTION_DESC = "desc";

    // Regular Expressions
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PHONE_REGEX = "^[0-9]{10}$";

    private AppConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
