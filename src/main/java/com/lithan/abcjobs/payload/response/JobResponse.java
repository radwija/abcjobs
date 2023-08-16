package com.lithan.abcjobs.payload.response;

import com.lithan.abcjobs.entity.Job;

public class JobResponse {
    private Job job = null;
    private String message = null;
    private Long jobId = null;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
