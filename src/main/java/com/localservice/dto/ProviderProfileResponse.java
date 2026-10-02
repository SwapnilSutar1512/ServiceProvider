package com.localservice.dto;

import java.util.List;

public class ProviderProfileResponse {
    private Long id;
    private String businessName;
    private String fullName;
    private String categoryName;
    private String city;
    private String locality;
    private String workingHours;
    private Integer experience;
    private Double rating;
    private String approvalStatus;
    private List<String> serviceAreas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }
    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public List<String> getServiceAreas() { return serviceAreas; }
    public void setServiceAreas(List<String> serviceAreas) { this.serviceAreas = serviceAreas; }
}
