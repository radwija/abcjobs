package com.lithan.abcjobs.repository;

import com.lithan.abcjobs.entity.User;
import com.lithan.abcjobs.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u " +
            "JOIN u.userProfile p " +
            "WHERE " +
            "u.username LIKE '%' || :keyword || '%' OR " +
            "p.firstName LIKE '%' || :keyword || '%' OR " +
            "p.lastName LIKE '%' || :keyword || '%' OR " +
            "p.title LIKE '%' || :keyword || '%' OR " +
            "p.city LIKE '%' || :keyword || '%' OR " +
            "p.country LIKE '%' || :keyword || '%'")
    List<User> searchForUsers(String keyword);
    List<User> findUserByRole(String role);

    User getUserByUsername(String username);

    UserProfile getUserProfileByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User findByEmail(String email);

}
