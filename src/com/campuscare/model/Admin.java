package com.campuscare.model;

import com.campuscare.model.enums.UserRole;
import java.time.LocalDateTime;

/**
 * Concrete Admin entity extending User.
 * Represents campus authorities managing departments, complaints, and escalations.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String username;
    private String roleTitle;

    public Admin() {
        super();
        this.role = UserRole.ADMIN;
    }

    public Admin(String id, String username, String passwordHash, String fullName, String email, String roleTitle) {
        super(id, fullName, email, passwordHash, "", UserRole.ADMIN);
        this.username = username;
        this.roleTitle = roleTitle != null ? roleTitle : "Campus Administrator";
    }

    public Admin(String id, String username, String passwordHash, String fullName, String email,
                 String roleTitle, LocalDateTime createdAt) {
        super(id, fullName, email, passwordHash, "", UserRole.ADMIN, createdAt);
        this.username = username;
        this.roleTitle = roleTitle != null ? roleTitle : "Campus Administrator";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    @Override
    public String getRoleBadge() {
        return "Admin: " + roleTitle;
    }

    @Override
    public String toString() {
        return String.format("Admin[User: %s, Name: %s, Title: %s]", username, name, roleTitle);
    }
}
