package com.example.student_management.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String email;
    private String course;
    private String parentName;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private String city;
    private String gender;
    private String departments;
    private String password;
}
