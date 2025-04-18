package com.example.final_project_be.domain.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.final_project_be.domain.member.entity.Member;

@Repository
public interface MemberGoalRepository extends JpaRepository<Member, Long> {

    @Modifying
    @Query("UPDATE Member m SET m.goal = :goal WHERE m.id = :memberId")
    void updateMemberGoal(@Param("memberId") Long memberId, @Param("goal") String goal);
}