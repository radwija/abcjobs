package com.lithan.abcjobs.payload.response;

import com.lithan.abcjobs.entity.Education;
import com.lithan.abcjobs.entity.Experience;

public class EducationResponse {
    private Education education = null;
    private String message = null;

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
