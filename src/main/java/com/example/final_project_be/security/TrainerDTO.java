package com.example.final_project_be.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class TrainerDTO extends User {

    private Long id;
    private String email;

    @JsonIgnore
    private String password;
    private String phone;
    private String name;
    private String userType;
    private String career;
    private List<String> certifications;
    private List<String> specialities;

    public TrainerDTO(
            Long id,
            String email,
            String password,
            String phone,
            String name,
            String userType,
            String career,
            List<String> certifications,
            List<String> specialities
    ) {
        // userType에 따라 단일 권한 부여 ("ROLE_TRAINER")
        super(email, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType)));
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.userType = userType;
        this.career = career;
        this.certifications = certifications;
        this.specialities = specialities;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("id", this.id);
        dataMap.put("email", this.email);
        dataMap.put("phone", this.phone);
        dataMap.put("name", this.name);
        dataMap.put("userType", this.userType);
        dataMap.put("career", this.career);
        dataMap.put("certifications", this.certifications);
        dataMap.put("specialities", this.specialities);

        return dataMap;
    }
} 