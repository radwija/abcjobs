package com.lithan.abcjobs.entity;

import javax.persistence.*;

@Entity
public class ApplyJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applyJobId;

    @ManyToOne
    @JoinColumn(name = "applied_by")
    private User appliedBy;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "applied_job")
    private Job appliedJob;

    private String qualificationUrl;

    public Long getApplyJobId() {
        return applyJobId;
    }

    public void setApplyJobId(Long applyJobId) {
        this.applyJobId = applyJobId;
    }

    public User getAppliedBy() {
        return appliedBy;
    }

    public void setAppliedBy(User appliedBy) {
        this.appliedBy = appliedBy;
    }

    public Job getAppliedJob() {
        return appliedJob;
    }

    public void setAppliedJob(Job appliedJob) {
        this.appliedJob = appliedJob;
    }

    public String getQualificationUrl() {
        return qualificationUrl;
    }

    public void setQualificationUrl(String qualificationUrl) {
        this.qualificationUrl = qualificationUrl;
    }
}
