
package com.example.spring2.service;

import com.example.spring2.model.Student;
import com.example.spring2.api.NotFoundException;
import com.example.spring2.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private GroupService groupService;

    // Ін'єкція залежності через сетер
    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Ін'єкція залежності через сетер
    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    // Отримання всіх студентів
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Пошук студента за ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Студента не знайдено"));
    }

    // Пошук студентів за прізвищем
    public List<Student> searchByLastName(String lastName) {
        return studentRepository.findByLastName(lastName);
    }

    // Пошук студентів за групою
    public List<Student> searchByGroupId(Long groupId) {
        return studentRepository.findByGroupId(groupId);
    }

    // Створення або редагування студента
    public Student saveStudent(Student student) {

        if (student.getFirstName() == null ||
                student.getFirstName().isBlank()) {
            throw new IllegalArgumentException(
                    "Ім'я студента не може бути порожнім");
        }

        if (student.getLastName() == null ||
                student.getLastName().isBlank()) {
            throw new IllegalArgumentException(
                    "Прізвище студента не може бути порожнім");
        }

        if (student.getGroupId() == null) {
            throw new IllegalArgumentException(
                    "Необхідно вибрати групу");
        }

        // Перевірка існування навчальної групи
        if (groupService.getAllGroups().stream().noneMatch(g -> g.getId().equals(student.getGroupId()))) {
            throw new NotFoundException("Навчальну групу не знайдено");
        }

        return studentRepository.save(student);
    }

    // Видалення студента
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // Видалення студентів певної групи
    public void deleteStudentsByGroupId(Long groupId) {
        studentRepository.deleteByGroupId(groupId);
    }
}
