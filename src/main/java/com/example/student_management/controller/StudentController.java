package com.example.student_management.controller;

import com.example.student_management.dto.UpdateRequest;
import com.example.student_management.entity.Student;
import com.example.student_management.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Student> getStudentList(){
        return studentService.getAllStudents();
    }


    @GetMapping("/{id}")
    public Student getStudentInfo(@PathVariable Long id){

        System.out.println("Fetching ONE Student");

        return studentService.findStudentById(id);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Student updateStudent(@RequestBody UpdateRequest request, @PathVariable Long id){
        return studentService.updateStudent(request, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
    }
}
