package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.payload.response.JobApplicationResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.IOException;
import java.util.List;

public interface ApplyJobService {
    ApplyJob saveAppliedJob(Long jobId, ApplyJobRequest applyJobRequest, String username) throws IOException;
    void deleteApplyJobByAppliedJobId(Long jobId);
    void deleteApplyJobByAppliedById(Long userId);
    List<ApplyJob> getAllAppliedJobs();
    List<ApplyJob> findAppliedJobByStatus(String status);
    List<ApplyJob> findAppliedJobByAppliedJob(Job appliedJob);
    List<ApplyJob> findApplyJobByAppliedBy(User user);
    List<ApplyJob> findByAppliedByAndStatus(User user, String status);
    JobApplicationResponse acceptJobApplication(Long applyJobId);
    JobApplicationResponse declineJobApplication(Long applyJobId);
    boolean checkUserAlreadyApply(User appliedBy, Job appliedJob);
    ApplyJob findByAppliedByAndApplyAppliedJob(User user, Job applyJob);
}
