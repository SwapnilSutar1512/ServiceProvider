package com.localservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating a new ServiceProvider.
 */

public class ServiceProviderRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    private String businessName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience must be 0 or more")
    @Max(value = 70, message = "Experience cannot exceed 70 years")
    private Integer experience;

    @NotBlank(message = "Street address is required")
    private String street;

    @NotBlank(message = "Locality is required")
    private String locality;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
    private String pincode;

    private Double latitude;

    private Double longitude;

    @Size(max = 500, message = "Working hours must not exceed 500 characters")
    private String workingHours;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Category ID must be valid")
    private Long categoryId;

    @NotNull(message = "Active status is required")
    private Boolean active;

    
    
    
	public ServiceProviderRequest(
			@NotBlank(message = "Full name is required") @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters") String fullName,
			@NotBlank(message = "Business name is required") @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters") String businessName,
			@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits") String phoneNumber,
			@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
			@NotNull(message = "Experience is required") @Min(value = 0, message = "Experience must be 0 or more") @Max(value = 70, message = "Experience cannot exceed 70 years") Integer experience,
			@NotBlank(message = "Street address is required") String street,
			@NotBlank(message = "Locality is required") String locality,
			@NotBlank(message = "City is required") String city, @NotBlank(message = "State is required") String state,
			@NotBlank(message = "Pincode is required") @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits") String pincode,
			Double latitude, Double longitude,
			@Size(max = 500, message = "Working hours must not exceed 500 characters") String workingHours,
			@NotNull(message = "Category ID is required") @Min(value = 1, message = "Category ID must be valid") Long categoryId,
			@NotNull(message = "Active status is required") Boolean active) {
		super();
		this.fullName = fullName;
		this.businessName = businessName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.experience = experience;
		this.street = street;
		this.locality = locality;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.workingHours = workingHours;
		this.categoryId = categoryId;
		this.active = active;
	}

	public ServiceProviderRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getExperience() {
		return experience;
	}

	public void setExperience(Integer experience) {
		this.experience = experience;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

    
    
}
