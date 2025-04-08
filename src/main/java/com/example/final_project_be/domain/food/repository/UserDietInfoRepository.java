package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.entity.UserDietInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDietInfoRepository extends JpaRepository<UserDietInfo, Long> {

    @EntityGraph(attributePaths = {"member"})  // Member와 함께 로딩
    @Query("select u from UserDietInfo u where u.member = :member")
    Optional<UserDietInfo> findByMember(@Param("member") Long member);
}
