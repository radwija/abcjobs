package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.EApplyJobStatus;
import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.JobApplicationNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.ApplyJobRequest;
import com.lithan.abcjobs.payload.response.JobApplicationResponse;
import com.lithan.abcjobs.repository.ApplyJobRepository;
import com.lithan.abcjobs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ApplyJobServiceImpl implements ApplyJobService {
    @Autowired
    private ApplyJobRepository applyJobRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public ApplyJob saveAppliedJob(Long jobId, ApplyJobRequest applyJobRequest, String username) throws IOException {
        ApplyJob applyJob = new ApplyJob();
        User appliedBy = userService.getUserByUsername(username);

        if (appliedBy.getRole().equals("ADMIN")) {
            throw new RefusedActionException("Admin unable to apply for any jobs!");
        }
        Job appliedJob = jobService.findJobByJobId(jobId);
        boolean isUserAlreadyApply = applyJobRepository.existsByAppliedByAndAppliedJob(appliedBy, appliedJob);
        if (isUserAlreadyApply) {
            throw new RefusedActionException("You already applied for this job");
        }

        long maxSize = 2 * 1024 * 1024; // 2MB in bytes
        if (applyJobRequest.getQualification().getSize() > maxSize) {
            throw new RuntimeException("File size exceeds the allowed limit. File must be under 2 MB.");
        }
        byte[] rawQualification = applyJobRequest.getQualification().getBytes();
        if (!applyJobRequest.getQualification().isEmpty()) {
            applyJob.setQualification(rawQualification);
        }
        applyJob.setAppliedBy(appliedBy);
        applyJob.setAppliedJob(appliedJob);
        applyJob.setStatus(EApplyJobStatus.PENDING.toString());

        return applyJobRepository.save(applyJob);
    }

    @Override
    public void deleteApplyJobByAppliedJobId(Long jobId) {
        applyJobRepository.deleteByAppliedJobId(jobId);
    }

    @Override
    public void deleteApplyJobByAppliedById(Long userId) {
        applyJobRepository.deleteByUserId(userId);
    }

    @Override
    public List<ApplyJob> getAllAppliedJobs() {
        return applyJobRepository.findAll();
    }

    @Override
    public List<ApplyJob> findAppliedJobByStatus(String status) {
        return applyJobRepository.findByStatus(status);
    }

    @Override
    public List<ApplyJob> findAppliedJobByAppliedJob(Job appliedJob) {
        return applyJobRepository.findByAppliedJob(appliedJob);
    }

    @Override
    public List<ApplyJob> findApplyJobByAppliedBy(User user) {
        return applyJobRepository.findByAppliedBy(user);
    }

    @Override
    public List<ApplyJob> findByAppliedByAndStatus(User user, String status) {
        return applyJobRepository.findByAppliedByAndStatus(user, status);
    }

    @Override
    public JobApplicationResponse acceptJobApplication(Long applyJobId) {
        JobApplicationResponse response = new JobApplicationResponse();
        ApplyJob acceptedJobApplication = applyJobRepository.findByApplyJobId(applyJobId);

        if (acceptedJobApplication == null) {
            throw new JobApplicationNotFoundException("Job application not found!");
        }
        User applicant = acceptedJobApplication.getAppliedBy();
        UserProfile userProfile = applicant.getUserProfile();
        Job job = acceptedJobApplication.getAppliedJob();

        acceptedJobApplication.setStatus(EApplyJobStatus.ACCEPTED.toString());
        userProfile.setJob(job);
        userProfileService.saveUpdateUserProfile(userProfile);
        applyJobRepository.save(acceptedJobApplication);

        String applicantFullname = applicant.getUserProfile().getFirstName() + " " + applicant.getUserProfile().getLastName();
        String jobName = job.getJobName();
        String companyName = job.getCompanyName();
        String jobLevel = job.getJobLevel();
        String jobTime = job.getJobTime();

        String message = "ACCEPTED: Job application from " + applicantFullname + " in applying for job ID: " + job.getJobId() + ", name: " + jobName + ", company: " + companyName + ".";
        response.setApplyJob(acceptedJobApplication);
        response.setMessage(message);
        emailSenderService.sendMail(applicant.getEmail(),
                "Accepted Job Application | ABC Jobs Portal",
                "Congratulations, " + applicantFullname + "!" + " Your job application has been accepted." +
                        "\n" +
                        "Job Name: " + jobName + "\n" +
                        "Job Level: " + jobLevel + "\n" +
                        "Job Time: " + jobTime + "\n" +
                        "Company Name: " + companyName
        );

        return response;
    }

    @Override
    public JobApplicationResponse declineJobApplication(Long applyJobId) {
        JobApplicationResponse response = new JobApplicationResponse();
        ApplyJob acceptedJobApplication = applyJobRepository.findByApplyJobId(applyJobId);

        if (acceptedJobApplication == null) {
            throw new JobApplicationNotFoundException("Job application not found!");
        }

        acceptedJobApplication.setStatus(EApplyJobStatus.DECLINED.toString());
        applyJobRepository.save(acceptedJobApplication);

        User applicant = acceptedJobApplication.getAppliedBy();
        String applicantFullname = applicant.getUserProfile().getFirstName() + " " + applicant.getUserProfile().getLastName();
        String jobName = acceptedJobApplication.getAppliedJob().getJobName();
        String companyName = acceptedJobApplication.getAppliedJob().getCompanyName();
        String jobLevel = acceptedJobApplication.getAppliedJob().getJobLevel();
        String jobTime = acceptedJobApplication.getAppliedJob().getJobTime();

        String message = "DECLINED: Job application from " + applicantFullname + " in applying for job ID: " + applyJobId + ", name: " + jobName + ", company: " + companyName + ".";
        response.setApplyJob(acceptedJobApplication);
        response.setMessage(message);
        emailSenderService.sendMail(applicant.getEmail(),
                "Declined Job Application | ABC Jobs Portal",
                "Thank you for your interest. Unfortunately, your application for the position has been declined. Best regards." +
                        "\n" +
                        "Job Name: " + jobName + "\n" +
                        "Job Level: " + jobLevel + "\n" +
                        "Job Time: " + jobTime + "\n" +
                        "Company Name: " + companyName
        );

        return response;
    }

    @Override
    public boolean checkUserAlreadyApply(User appliedBy, Job appliedJob) {
        return applyJobRepository.existsByAppliedByAndAppliedJob(appliedBy, appliedJob);
    }

    @Override
    public ApplyJob findByAppliedByAndApplyAppliedJob(User user, Job applyJob) {
        return applyJobRepository.findByAppliedByAndAppliedJob(user, applyJob);
    }


}
