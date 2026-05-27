package com.example.student_management.service;

import com.example.student_management.entity.Student;
import com.example.student_management.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository){
        this.repository = repository;
    }

    public List<Student> getAllStudents(){
        return repository.findAll();
    }

    public Student saveStudent(Student student){
        return repository.save(student);
    }

    public Student updateStudent(Student student, Long id){
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
        studentToUpdate.setDepartments(studentToUpdate.getDepartments());
        return repository.save(studentToUpdate);
    }

    public void deleteStudent(Long id){
        repository.deleteById(id);
    }
}
