package com.lithan.abcjobs.payload.response;

import com.lithan.abcjobs.entity.Job;

public class JobResponse {
    private Job job = null;
    private String message = null;

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
}
