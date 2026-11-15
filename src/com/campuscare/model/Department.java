package com.campuscare.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a university service department (e.g. Electrical, Hostel, IT, Water/Estate).
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String name;
    private String code;
    private String contactEmail;
    private String description;
    private LocalDateTime createdAt;

    public Department() {
        this.createdAt = LocalDateTime.now();
    }

    public Department(int departmentId, String name, String code, String contactEmail, String description) {
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.contactEmail = contactEmail;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return departmentId == that.departmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId);
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
