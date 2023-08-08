package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Job findJobByJobId(Long jobId);
}
