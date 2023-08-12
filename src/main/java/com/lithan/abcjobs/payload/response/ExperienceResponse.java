package com.lithan.abcjobs.payload.response;

import com.lithan.abcjobs.entity.Experience;

public class ExperienceResponse {
    private Experience experience = null;
    private String message = null;

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
