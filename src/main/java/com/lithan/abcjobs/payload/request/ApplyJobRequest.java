package com.lithan.abcjobs.payload.request;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

public class ApplyJobRequest {
    private String qualificationUrl;

    public String getQualificationUrl() {
        return qualificationUrl;
    }

    public void setQualificationUrl(String qualificationUrl) {
        this.qualificationUrl = qualificationUrl;
    }
}
