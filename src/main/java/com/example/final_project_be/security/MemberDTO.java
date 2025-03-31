package com.example.final_project_be.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class MemberDTO extends User {

    private String email;
    private String password;
    private String phone;
    private String name;
    private List<String> roles = new ArrayList<>();
    private List<String> goals = new ArrayList<>();

    public MemberDTO(
            String email,
            String password,
            String phone,
            String name,
            List<String> roles,
            List<String> goals
    ) {
        super(email, password, roles.stream().map(str -> new SimpleGrantedAuthority ("ROLE_" + str)).collect(Collectors.toList()));
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.roles = roles;
        this.goals = goals;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", this.email);
        dataMap.put("password", this.password);
        dataMap.put("phone", this.phone);
        dataMap.put("name", this.name);
        dataMap.put("roles", this.roles);
        dataMap.put("goals", this.goals);

        return dataMap;
    }
}
