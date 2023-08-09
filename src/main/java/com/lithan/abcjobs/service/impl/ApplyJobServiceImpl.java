package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.EApplyJobStatus;
import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.repository.ApplyJobRepository;
import com.lithan.abcjobs.service.ApplyJobService;
import com.lithan.abcjobs.service.JobService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplyJobServiceImpl implements ApplyJobService {
    @Autowired
    private ApplyJobRepository applyJobRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Override
    public ApplyJob saveAppliedJob(Long jobId, ApplyJobRequest applyJobRequest, String username) {
        ApplyJob applyJob = new ApplyJob();
        User appliedBy = userService.getUserByUsername(username);
        if (appliedBy.getRole().equals("ADMIN")) {
            throw new RefusedActionException("Admin unable to apply for any jobs!");
        }
        Job appliedJob = jobService.findJobByJobId(jobId);

        applyJob.setQualificationUrl(applyJobRequest.getQualificationUrl());
        applyJob.setAppliedBy(appliedBy);
        applyJob.setAppliedJob(appliedJob);
        applyJob.setStatus(EApplyJobStatus.PENDING.toString());

        return applyJobRepository.save(applyJob);
    }

    @Override
    public void deleteApplyJobByAppliedJobId(Long jobId) {
        applyJobRepository.deleteByAppliedJobByJobId(jobId);
    }

    @Override
    public List<ApplyJob> getAllAppliedJobs() {
        return applyJobRepository.findAll();
    }

    @Override
    public List<ApplyJob> findAppliedJobByStatus(String status) {
        return applyJobRepository.findAppliedJobByStatus(status);
    }

}
