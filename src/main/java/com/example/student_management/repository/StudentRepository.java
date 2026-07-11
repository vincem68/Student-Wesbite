package com.example.student_management.repository;

import com.example.student_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    //find the requested student
    Student findByEmailAndPassword(String email, String password);

    List<Student> findByIsAdminTrue();

    Student findByEmail(String email);
}
