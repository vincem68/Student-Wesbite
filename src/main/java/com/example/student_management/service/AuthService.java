package com.example.student_management.service;

import com.example.student_management.dto.AuthRequest;
import com.example.student_management.dto.AuthResponse;
import com.example.student_management.dto.RegisterRequest;
import com.example.student_management.dto.RegisterResponse;
import com.example.student_management.entity.Student;
import com.example.student_management.repository.StudentRepository;
import com.example.student_management.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository repository;
    private final JWTService jwtService;

    public RegisterResponse register(RegisterRequest request){

        //make sure no other student has the same email in repo
        Optional<Student> dupStudent = repository.findByEmail(request.getEmail());
        if (dupStudent.isPresent()){
            return new RegisterResponse(
                    null,
                    "Another student is already registered with that email address."
            );
        }

        //check to see if requested course is full before registering student
        Long courseCount = repository.countByCourse(request.getCourse());
        if (courseCount >= 50){
            return new RegisterResponse(
                    null,
                    "The requested course has no more open spots."
            );
        }

        Student student = new Student();
        student.setCity(request.getCity());
        student.setGender(request.getGender());
        student.setEmail(request.getEmail());
        student.setName(request.getName());
        student.setCourse(request.getCourse());
        student.setParentName(request.getParentName());
        student.setAddress(request.getAddress());
        student.setDepartments(request.getDepartments());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAdmin(false);
        student.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        repository.save(student);

        return new RegisterResponse(
                student,
            "Student successfully registered."
        );
    }

    public AuthResponse authenticate(AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Student student = repository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(student);

        //System.out.println("Student is " + student.getEmail() + " and token is " + token);

        return new AuthResponse(token, student.getName(), student.getId(), student.isAdmin());
    }
}
