package com.example.spring2.api.dto;
import com.example.spring2.model.Student;
public record StudentResponse(Long id, String firstName, String lastName, Long groupId) {
 public static StudentResponse from(Student student) {
   return new StudentResponse(student.getId(),student.getFirstName(),student.getLastName(),student.getGroupId());
 }
}
