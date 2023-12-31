package com.lithan.abcjobs.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "username")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(unique = true)
    private String registrationCode;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private UserProfile userProfile;

    private String username;

    private String email;

    private String password;

    private String role;

    private Boolean isActive;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<ThreadPost> threadPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ThreadComment> threadComments = new ArrayList<>();

    // Posted jobs from admin
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Job> jobs = new ArrayList<>();

//    @OneToMany(mappedBy = "appliedBy", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ApplyJob> applyJobs = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userDetail) {
        this.userProfile = userDetail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<ThreadPost> getThreadPosts() {
        return threadPosts;
    }

    public void setThreadPosts(List<ThreadPost> threadPost) {
        this.threadPosts = threadPost;
    }

    public List<ThreadComment> getComments() {
        return threadComments;
    }

    public void setComments(List<ThreadComment> threadComments) {
        this.threadComments = threadComments;
    }
}
