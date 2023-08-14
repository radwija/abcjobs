package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.ApplyJob;
import com.lithan.abcjobs.entity.Job;
import com.lithan.abcjobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ApplyJob a WHERE a.appliedJob.jobId = :jobId")
    void deleteByAppliedJobId(Long jobId);
    @Modifying
    @Transactional
    @Query("DELETE FROM ApplyJob a WHERE a.appliedBy.userId = :userId")
    void deleteByUserId(Long userId);

    List<ApplyJob> findAppliedJobByStatus(String status);
    List<ApplyJob> findAppliedJobByAppliedJob(Job appliedJob);
    ApplyJob findJobApplicationByApplyJobId(Long applyJobId);
    List<ApplyJob> findByAppliedBy(User user);
    List<ApplyJob> findByAppliedByAndStatus(User user, String status);
    ApplyJob findByAppliedByAndAppliedJob(User user, Job applyJob);
    boolean existsByAppliedByAndAppliedJob(User appliedBy, Job appliedJob);

}
