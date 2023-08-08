package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Job findJobByJobId(Long jobId);
    @Query(value = "SELECT j FROM Job j WHERE j.jobName LIKE '%' || :keyword || '%'"
            + "OR j.jobLevel LIKE '%' || :keyword || '%'"
            + "OR j.jobTime LIKE '%' || :keyword || '%'"
            + "OR j.jobDescription LIKE '%' || :keyword || '%'"
            + "OR j.jobDescription LIKE '%' || :keyword || '%'"
            + "OR j.companyName LIKE '%' || :keyword || '%'"
    )
    List<Job> searchForJobs(@Param("keyword") String keyword);
}
