package com.example.student_management.dto;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {

    private String email;
    private String password;
}
