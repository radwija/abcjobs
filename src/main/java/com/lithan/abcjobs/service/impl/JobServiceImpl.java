package com.lithan.abcjobs.service.impl;

import com.lithan.abcjobs.constraint.EJobLevel;
import com.lithan.abcjobs.constraint.EJobTime;
import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import com.lithan.abcjobs.exception.JobNotFoundException;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.repository.ApplyJobRepository;
import com.lithan.abcjobs.repository.JobRepository;
import com.lithan.abcjobs.repository.UserProfileRepository;
import com.lithan.abcjobs.service.JobService;
import com.lithan.abcjobs.service.UserProfileService;
import com.lithan.abcjobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ApplyJobRepository applyJobRepository;

    public Job findJobByJobId(Long jobId) {
        Job job = jobRepository.findJobByJobId(jobId);
        if (job == null) {
            throw new JobNotFoundException("Job not found");
        }
        return jobRepository.findJobByJobId(jobId);
    }

    @Override
    public Job saveJob(JobRequest jobRequest, String postByUsername) {
        User postBy = userService.getUserByUsername(postByUsername);
        if (!postBy.getRole().equals("ADMIN")) {
            throw new RefusedActionException("Only ADMIN can post jobs!");
        }

        Job newJob = new Job();
        newJob.setUser(postBy);
        newJob.setJobName(jobRequest.getJobName());
        newJob.setCompanyName(jobRequest.getCompanyName());
        if (jobRequest.getJobTime().equals("fullTime")) {
            newJob.setJobTime(EJobTime.FULLTIME.toString());
        } else if (jobRequest.getJobTime().equals("partTime")) {
            newJob.setJobTime(EJobTime.PARTTIME.toString());
        }

        switch (jobRequest.getJobLevel()) {
            case "junior" -> newJob.setJobLevel(EJobLevel.JUNIOR.toString());
            case "middle" -> newJob.setJobLevel(EJobLevel.MIDDLE.toString());
            case "senior" -> newJob.setJobLevel(EJobLevel.SENIOR.toString());
            case "internship" -> newJob.setJobLevel(EJobLevel.INTERNSHIP.toString());
        }

        newJob.setJobDescription(jobRequest.getJobDescription());

        return jobRepository.save(newJob);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public List<Job> searchForJobs(String keyword) {
        return jobRepository.searchForJobs(keyword);
    }

    @Override
    public List<Job> findJobsByJobLevel(String level) {
        return jobRepository.findByJobLevel(level.toUpperCase());
    }

    @Override
    public List<Job> findJobsByJobTime(String time) {
        return jobRepository.findByJobTime(time.toUpperCase());
    }

    @Override
    public List<Job> showRequestedJobs(String requestParamName, String request) {
        return switch (requestParamName) {
            case "q" -> jobRepository.searchForJobs(request);
            case "level" -> jobRepository.findByJobLevel(request);
            case "time" -> jobRepository.findByJobTime(request);
            default -> null;
        };
    }

    @Override
    public void deleteJob(Long jobId, String username) {
        boolean isAdmin = userService.getUserByUsername(username).getRole().equals("ADMIN");
        if (!isAdmin) {
            throw new RefusedActionException("You're not allowed to delete jobs!");
        }
        Job job = jobRepository.findJobByJobId(jobId);

        List<UserProfile> userProfiles = userProfileService.findUserProfileByAppliedJob(job);
        for (UserProfile userProfile : userProfiles) {
            userProfile.setJob(null);
            userProfileRepository.save(userProfile);
        }
        List<ApplyJob> jobApplications = applyJobRepository.findByAppliedJob(job);
        for (ApplyJob jobApplication : jobApplications) {
            jobApplication.setAppliedBy(null);
            applyJobRepository.save(jobApplication);
        }

        applyJobRepository.deleteByAppliedJobId(jobId);
        jobRepository.deleteById(jobId);
    }
}
