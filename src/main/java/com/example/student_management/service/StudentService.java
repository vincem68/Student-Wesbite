package com.example.student_management.service;

import com.example.student_management.dto.UpdateRequest;
import com.example.student_management.entity.Student;
import com.example.student_management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<Student> getAllStudents(){

        return repository.findAll();
    }

    public Student findStudentByCredentials(String email, String password) {
        return repository.findByEmailAndPassword(email, password);
    }

    public Student findStudentById(Long id){
        return repository.findById(id).orElseThrow();
    }

    public Student saveStudent(Student student){

        Optional<Student> dupStudent = repository.findByEmail(student.getEmail());
        if (dupStudent.isPresent()){
            return null;
        }
        return repository.save(student);
    }

    public Student updateStudent(UpdateRequest student, Long id){
        Student studentToUpdate = repository.findById(id).orElseThrow();
        studentToUpdate.setCourse(student.getCourse());
        studentToUpdate.setName(student.getName());
        studentToUpdate.setEmail(student.getEmail());
        studentToUpdate.setCity(student.getCity());
        studentToUpdate.setPhoneNumber(student.getPhoneNumber());
        studentToUpdate.setAddress(student.getAddress());
        studentToUpdate.setDateOfBirth(student.getDateOfBirth());
        studentToUpdate.setGender(student.getGender());
        studentToUpdate.setParentName(student.getParentName());
        studentToUpdate.setDepartments(student.getDepartments());
        return repository.save(studentToUpdate);
    }

    public List<Student> getAdmins(){
        return repository.findByIsAdminTrue();
    }

    public void deleteStudent(Long id){
        repository.deleteById(id);
    }
}
