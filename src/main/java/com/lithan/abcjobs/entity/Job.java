package com.lithan.abcjobs.entity;

import javax.persistence.*;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private String jobName;
    private String jobLevel;
    private String jobTime;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String jobDescription;

    private String companyName;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "job")
    UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User user;


    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getJobTime() {
        return jobTime;
    }

    public void setJobTime(String jobTime) {
        this.jobTime = jobTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
