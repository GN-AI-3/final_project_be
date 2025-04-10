package com.example.final_project_be.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

@Getter
@Setter
@ToString
public class MemberDTO extends User {

    private Long id;
    private String email;
    private String password;
    private String phone;
    private String name;
    private String userType;
    private List<String> goals = new ArrayList<>();

    public MemberDTO(
            Long id,
            String email,
            String password,
            String phone,
            String name,
            String userType,
            List<String> goals
    ) {
        // userType에 따라 단일 권한 부여 ("ROLE_MEMBER" 또는 "ROLE_TRAINER")
        super(email, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType)));
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.userType = userType;
        this.goals = goals;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("id", this.id);
        dataMap.put("email", this.email);
        dataMap.put("password", this.password);
        dataMap.put("phone", this.phone);
        dataMap.put("name", this.name);
        dataMap.put("userType", this.userType);
        dataMap.put("goals", this.goals);

        return dataMap;
    }
}
