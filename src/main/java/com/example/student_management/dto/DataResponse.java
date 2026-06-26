package com.example.student_management.dto;
import com.example.student_management.entity.Student;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataResponse {
    private String name;
    private String role;
    private Long id;
    private List<Student> students;
}
