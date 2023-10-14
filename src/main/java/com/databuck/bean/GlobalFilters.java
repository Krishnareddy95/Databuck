package com.databuck.bean;

import java.util.Date;

public class GlobalFilters {
    private Long filterId;
    private String filterName;
    private String description;
    private String filterCondition;
    private String createdAt;
    private int domainId;
    private String domain;

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    public String getCreatedAt() {
        return createdAt.toString();
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
