package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.payload.response.JobResponse;

import java.util.List;

public interface JobService {
    Job findJobByJobId(Long jobId);
    JobResponse saveJob(JobRequest jobRequest, String postByUsername);
    Job mapJobRequestToJob(JobRequest jobRequest);
    void mapJobRequestToJob(JobRequest jobRequest, Job job);
    void convertJobLevel(String jobLevel, Job job);
    void convertJobTime(String jobTime, Job job);
    List<Job> getAllJobs();
    List<Job> searchForJobs(String keyword);
    List<Job> findJobsByJobLevel(String level);
    List<Job> findJobsByJobTime(String time);
    List<Job> showRequestedJobs(String requestParamName, String request);
    void deleteJob(Long jobId, String username);
}
