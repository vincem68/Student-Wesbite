package com.example.student_management.controller;

import com.example.student_management.dto.DataResponse;
import com.example.student_management.entity.Student;
import com.example.student_management.dto.Credentials;
import com.example.student_management.service.StudentService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service){
        this.service = service;
    }


    //this will return either all students in repo or a list of the single student
    //don't need to change student component
    @GetMapping
    public List<Student> getStudentList(@NonNull HttpSession session){

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long id = (Long) session.getAttribute("id");

        System.out.println("Admin: " + isAdmin + " Id: " + id);

        if (Boolean.TRUE.equals(isAdmin)){
            return service.getAllStudents();
        }

        Student student = service.findStudentById(id);

        if (student == null){ return List.of(); }

        return List.of(student);
    }

    //Used for authentication. Will look for Student with matching email and password and
    //send the needed data accordingly
    @PostMapping("/authorization")
    public ResponseEntity<String> login(@RequestBody Credentials credentials, HttpSession session){

        Student student = service.findStudentByCredentials(credentials.getEmail(), credentials.getPassword());

        if (student == null){
            return ResponseEntity.notFound().build();
        }

        //set session variables
        session.setAttribute("id", student.getId());
        session.setAttribute("name", student.getName());
        session.setAttribute("isAdmin", student.isAdmin());

        String message = student.isAdmin() ? "Admin:" + student.getId() : "Student:" + student.getId();

        return ResponseEntity.ok().body(message);
    }

    //Used for Logout. Will invalidate the current session
    @GetMapping("/authorization")
    public ResponseEntity<?> logout(@NonNull HttpSession session){
        session.invalidate();
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/session")
    public ResponseEntity<String> checkSessionStatus(HttpSession session){

        Long id = (Long) session.getAttribute("id");

        if (id == null){
            return ResponseEntity.status(401).build();
        }

        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        String name = (String) session.getAttribute("name");

        if (isAdmin){
            return ResponseEntity.ok().body("Admin:" + id + ":" + name);
        }

        return ResponseEntity.ok().body("Student:" + id + ":" + name);
    }

    //this will be used when the user submits the form for the new student
    @PostMapping("/registration")
    public Student addStudent(@RequestBody Student student){
        System.out.println("Request landed");
        System.out.println("Retrieved student " + student);
        return service.saveStudent(student);
    }

    @GetMapping("/{id}")
    public Student getStudentInfo(@PathVariable Long id, HttpSession session){

        //if session isn't active, user is not authorized
        if (session == null){
            return null;
        }

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        //if not an admin, not authorized to get/change info
        if (Boolean.FALSE.equals(isAdmin)){
            return null;
        }

        return service.findStudentById(id);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@RequestBody Student student, @PathVariable Long id, HttpSession session){
        //check again if session is active
        if (session == null){
            return null;
        }

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        //if not an admin, not authorized to get/change info
        if (Boolean.FALSE.equals(isAdmin)){
            return null;
        }
        return service.updateStudent(student, id);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id){
        service.deleteStudent(id);
    }
}
