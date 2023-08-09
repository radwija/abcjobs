package com.lithan.abcjobs.payload.response;

import com.lithan.abcjobs.entity.ApplyJob;

public class JobApplicationResponse {
    private ApplyJob applyJob = null;
    private String message;

    public ApplyJob getApplyJob() {
        return applyJob;
    }

    public void setApplyJob(ApplyJob applyJob) {
        this.applyJob = applyJob;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
