package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;

import java.util.List;

public interface ApplyJobService {
    ApplyJob saveAppliedJob(Long jobId, ApplyJobRequest applyJobRequest, String username);
    void deleteApplyJobByAppliedJobId(Long jobId);
    List<ApplyJob> getAllAppliedJobs();
    List<ApplyJob> findAppliedJobByStatus(String status);
}
