package com.example.student_management.controller;

import com.example.student_management.dto.AuthRequest;
import com.example.student_management.dto.AuthResponse;
import com.example.student_management.dto.RegisterRequest;
import com.example.student_management.entity.Student;
import com.example.student_management.service.AuthService;
import com.example.student_management.service.EmailService;
import com.example.student_management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){

        Student student = authService.register(request);

        if (student == null){
            return ResponseEntity.badRequest().body("Student Registration Failed");
        }

        emailService.sendRegistrationEmail(request.getEmail(), request.getName());

        //get list of admin accounts and send them email about registration
        List<Student> admins = studentService.getAdmins();
        for (Student admin : admins){
            emailService.sendAdminRegisterEmail(student, admin.getEmail());
        }

        return ResponseEntity.ok().body("Student Registered Successfully");
    }

    //this will be called when we try to auth the user
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request){
        System.out.println("Inside the auth controller");
        AuthResponse response = authService.authenticate(request);
        System.out.println("response details are " +
                response.getName() + " " +
                response.getToken() + " " +
                response.isAdmin()
        );
        return ResponseEntity.ok().body(response);
    }
}
