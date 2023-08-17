package com.lithan.abcjobs.service;

import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.exception.RefusedActionException;
import com.lithan.abcjobs.payload.request.JobRequest;
import com.lithan.abcjobs.payload.response.JobResponse;
import com.lithan.abcjobs.repository.JobRepository;
import com.lithan.abcjobs.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JobServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveJobAsAdmin() {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName("Software Engineer");
        jobRequest.setJobLevel("senior");
        jobRequest.setJobTime("FULLTIME");
        jobRequest.setJobDescription("Job description");
        jobRequest.setCompanyName("ABC Company");

        User adminUser = new User();
        adminUser.setUsername("admin123");
        adminUser.setRole("ADMIN");

        when(userService.getUserByUsername("admin123")).thenReturn(adminUser);

        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        JobResponse jobResponse = jobService.saveJob(jobRequest, "admin123");

        assertNotNull(jobResponse);
        assertEquals("Software Engineer", jobResponse.getJob().getJobName());
        assertEquals("SENIOR", jobResponse.getJob().getJobLevel());
        assertEquals("FULLTIME", jobResponse.getJob().getJobTime());
        assertEquals("Job description", jobResponse.getJob().getJobDescription());
        assertEquals("ABC Company", jobResponse.getJob().getCompanyName());
        assertEquals(adminUser, jobResponse.getJob().getUser());
        assertEquals("New job posted successfully!", jobResponse.getMessage());
    }

    @Test
    public void testSaveJobAsNonAdminThrowsException() {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName("Software Engineer");
        jobRequest.setJobLevel("senior");
        jobRequest.setJobTime("FULLTIME");
        jobRequest.setJobDescription("Job description");
        jobRequest.setCompanyName("ABC Company");

        User nonAdminUser = new User();
        nonAdminUser.setUsername("user123");
        nonAdminUser.setRole("USER");

        when(userService.getUserByUsername("user123")).thenReturn(nonAdminUser);

        assertThrows(RefusedActionException.class, () -> {
            jobService.saveJob(jobRequest, "user123");
        });
    }

    @Test
    public void testUpdateJobAsAdmin() {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobId(1L);
        jobRequest.setJobName("Updated Software Engineer");
        jobRequest.setJobLevel("middle");
        jobRequest.setJobTime("PARTTIME");
        jobRequest.setJobDescription("Updated job description");
        jobRequest.setCompanyName("Updated ABC Company");

        User adminUser = new User();
        adminUser.setUsername("admin123");
        adminUser.setRole("ADMIN");

        Job existingJob = new Job();
        existingJob.setJobId(1L);
        existingJob.setJobName("Software Engineer");
        existingJob.setJobLevel("senior");
        existingJob.setJobTime("FULLTIME");
        existingJob.setJobDescription("Job description");
        existingJob.setCompanyName("ABC Company");
        existingJob.setUser(adminUser);

        when(userService.getUserByUsername("admin123")).thenReturn(adminUser);
        when(jobRepository.findJobByJobId(1L)).thenReturn(existingJob);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse jobResponse = jobService.saveJob(jobRequest, "admin123");

        assertNotNull(jobResponse);
        assertEquals(1L, jobResponse.getJob().getJobId());
        assertEquals("Updated Software Engineer", jobResponse.getJob().getJobName());
        assertEquals("MIDDLE", jobResponse.getJob().getJobLevel());
        assertEquals("PARTTIME", jobResponse.getJob().getJobTime());
        assertEquals("Updated job description", jobResponse.getJob().getJobDescription());
        assertEquals("Updated ABC Company", jobResponse.getJob().getCompanyName());
        assertEquals(adminUser, jobResponse.getJob().getUser());
        assertEquals("Job successfully updated!", jobResponse.getMessage());
    }

    @Test
    public void testUpdateJobAsNonAdminThrowsException() {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobId(1L);

        User nonAdminUser = new User();
        nonAdminUser.setUsername("user123");
        nonAdminUser.setRole("USER");

        when(userService.getUserByUsername("user123")).thenReturn(nonAdminUser);

        assertThrows(RefusedActionException.class, () -> {
            jobService.saveJob(jobRequest, "user123");
        });
    }
}
