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
        return applyJobRepository.findAppliedJobByStatus(status);
    }

    @Override
    public JobApplicationResponse acceptJobApplication(Long applyJobId) {
        JobApplicationResponse response = new JobApplicationResponse();
        ApplyJob acceptedJobApplication = applyJobRepository.findJobApplicationByApplyJobId(applyJobId);

        if (acceptedJobApplication == null) {
            throw new JobApplicationNotFoundException("Job application not found!");
        }
        User applicant = acceptedJobApplication.getAppliedBy();
        UserProfile userProfile = applicant.getUserProfile();
        Job job = acceptedJobApplication.getAppliedJob();

        acceptedJobApplication.setStatus(EApplyJobStatus.ACCEPTED.toString());
        userProfile.setJobId(job);
        userProfileService.saveUpdateUserProfile(userProfile);
        applyJobRepository.save(acceptedJobApplication);

        String applicantFullname = applicant.getUserProfile().getFirstName() + " " + applicant.getUserProfile().getLastName();
        String jobName = job.getJobName();
        String companyName = job.getCompanyName();
        String jobLevel = job.getJobLevel();
        String jobTime = job.getJobTime();

        String message = "ACCEPTED: Job application from " + applicantFullname + " in applying for job ID: " +  job.getJobId() + ", name: " + jobName + ", company: " + companyName + ".";
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
        ApplyJob acceptedJobApplication = applyJobRepository.findJobApplicationByApplyJobId(applyJobId);

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


}
