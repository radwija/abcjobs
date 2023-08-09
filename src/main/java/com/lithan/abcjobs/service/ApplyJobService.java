package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;

public interface ApplyJobService {
    ApplyJob saveAppliedJob(Long jobId, ApplyJobRequest applyJobRequest, String username);
    void deleteApplyJobByAppliedJobId(Long jobId);
}
