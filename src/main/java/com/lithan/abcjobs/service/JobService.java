package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.payload.request.JobRequest;

public interface JobService {
    Job saveJob(JobRequest jobRequest, String postByUsername);
}
