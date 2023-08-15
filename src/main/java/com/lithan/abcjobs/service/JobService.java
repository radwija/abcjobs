package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.payload.request.JobRequest;

import java.util.List;

public interface JobService {
    Job findJobByJobId(Long jobId);
    Job saveJob(JobRequest jobRequest, String postByUsername);
    List<Job> getAllJobs();
    List<Job> searchForJobs(String keyword);
    List<Job> findJobsByJobLevel(String level);
    List<Job> findJobsByJobTime(String time);
    List<Job> showRequestedJobs(String requestParamName, String request);
    void deleteJob(Long jobId, String username);
}
