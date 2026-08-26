package com.example.student_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

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

}
