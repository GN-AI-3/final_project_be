package com.example.final_project_be.domain.member.repository;

import com.example.final_project_be.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.email = :email")
    Optional<Member> getWithRoles(@Param("email") String email);

    @Query("select m from Member m where m.email = :email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select case when count(m) > 0 then true else false end from Member m where m.email = :email")
    Boolean existsByEmail(@Param("email") String email);

    /**
     * 이름에 특정 문자열이 포함된 회원들을 찾습니다.
     * 
     * @param name 검색할 이름
     * @return 이름에 검색어가 포함된 회원 목록
     */
    @Query("select m from Member m where m.name like %:name%")
    List<Member> findByNameContaining(@Param("name") String name);
}
