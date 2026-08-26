package com.example.student_management.service;

import com.example.student_management.dto.AuthRequest;
import com.example.student_management.dto.AuthResponse;
import com.example.student_management.dto.RegisterRequest;
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

    public Student register(RegisterRequest request){

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

        Optional<Student> dupStudent = repository.findByEmail(request.getEmail());
        if (dupStudent.isPresent()){
            return null;
        }

        return repository.save(student);
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
