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
    @Lob
    @Column(name = "qualification")
    private byte[] qualification;

    @Lob
    private String base64Qualification;

    @Transient
    private String qualificationSrc;

    private String status;

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

    public byte[] getQualification() {
        return qualification;
    }

    public void setQualification(byte[] qualification) {
        this.qualification = qualification;
    }

    public String getBase64Qualification() {
        return base64Qualification;
    }

    public void setBase64Qualification(String base64Qualification) {
        this.base64Qualification = base64Qualification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQualificationSrc() {
        return qualificationSrc;
    }

    public void setQualificationSrc(String qualificationSrc) {
        this.qualificationSrc = qualificationSrc;
    }
}
