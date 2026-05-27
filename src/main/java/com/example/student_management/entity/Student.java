package com.example.student_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

}
