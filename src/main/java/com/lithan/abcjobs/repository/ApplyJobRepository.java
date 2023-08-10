package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.ApplyJob;
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
    ApplyJob findJobApplicationByApplyJobId(Long applyJobId);
}
