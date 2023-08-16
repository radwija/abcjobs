package com.lithan.abcjobs.entity;

import javax.persistence.*;
import java.util.Base64;

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

    @Transient
    private String qualificationSrc;

    public static String encodeQualificationInSrcHtml(byte[] base64Qualification) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(base64Qualification);
    }

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
