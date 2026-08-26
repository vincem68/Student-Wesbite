package com.example.student_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;
    private String course;
    private String parentName;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private String city;
    private String gender;
    private String departments;

    private String password;
    private boolean isAdmin;


    //might need to change this
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (isAdmin){
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    //this returns a list of roles
    @Override
    public String getUsername(){
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
