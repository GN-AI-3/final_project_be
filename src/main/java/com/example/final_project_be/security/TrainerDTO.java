package com.example.final_project_be.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class TrainerDTO extends User {

    private String email;
    private String password;
    private String phone;
    private String name;
    private String userType;
    private String career;
    private String speciality;

    public TrainerDTO(
            String email,
            String password,
            String phone,
            String name,
            String userType,
            String career,
            String speciality
    ) {
        // userType에 따라 단일 권한 부여 ("ROLE_TRAINER")
        super(email, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType)));
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.userType = userType;
        this.career = career;
        this.speciality = speciality;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", this.email);
        dataMap.put("password", this.password);
        dataMap.put("phone", this.phone);
        dataMap.put("name", this.name);
        dataMap.put("userType", this.userType);
        dataMap.put("career", this.career);
        dataMap.put("speciality", this.speciality);

        return dataMap;
    }
} 