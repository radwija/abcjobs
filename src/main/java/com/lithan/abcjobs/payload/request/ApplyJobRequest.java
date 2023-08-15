package com.lithan.abcjobs.payload.request;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

public class ApplyJobRequest {
    private MultipartFile qualification;

    public MultipartFile getQualification() {
        return qualification;
    }

    public void setQualification(MultipartFile qualificationUrl) {
        this.qualification = qualificationUrl;
    }
}
