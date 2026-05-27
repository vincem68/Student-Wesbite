package com.example.student_management.controller;

import com.example.student_management.entity.Student;
import com.example.student_management.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin("*")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service){
        this.service = service;
    }

    @GetMapping
    public List<Student> getStudents(){
        return service.getAllStudents();
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student){
        return service.saveStudent(student);
    }

    //this will be used when the user submits the form for the new student
    @PostMapping("/registration")
    public Student registerStudent(@RequestBody Student student){
        System.out.println("Request landed");
        System.out.println("Retrieved student " + student);
        return service.saveStudent(student);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@RequestBody Student student, @PathVariable Long id){
        return service.updateStudent(student, id);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id){
        service.deleteStudent(id);
    }
}
