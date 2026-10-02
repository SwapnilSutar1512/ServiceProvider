package com.localservice.dto;

public class AdminDashboardResponse {
    private long totalProviders;
    private long pendingProviders;
    private long approvedProviders;
    private long totalRequests;
    private long pendingRequests;
    private long completedRequests;
    private long totalCategories;
    private long totalAreas;

    public long getTotalProviders() { return totalProviders; }
    public void setTotalProviders(long totalProviders) { this.totalProviders = totalProviders; }
    public long getPendingProviders() { return pendingProviders; }
    public void setPendingProviders(long pendingProviders) { this.pendingProviders = pendingProviders; }
    public long getApprovedProviders() { return approvedProviders; }
    public void setApprovedProviders(long approvedProviders) { this.approvedProviders = approvedProviders; }
    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }
    public long getCompletedRequests() { return completedRequests; }
    public void setCompletedRequests(long completedRequests) { this.completedRequests = completedRequests; }
    public long getTotalCategories() { return totalCategories; }
    public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }
    public long getTotalAreas() { return totalAreas; }
    public void setTotalAreas(long totalAreas) { this.totalAreas = totalAreas; }
}
