package com.example.spring2.repository;

import com.example.spring2.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private long nextId = 1;

    public StudentRepository() {
        students.add(new Student(nextId++, "Ростислав", "Плисюк", 1L));
        students.add(new Student(nextId++, "Дарія", "Тетерук", 2L));
        students.add(new Student(nextId++, "Анна", "Стяглюк", 3L));
        students.add(new Student(nextId++, "Максим", "Шаповалов", 3L));
    }

    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    public Optional<Student> findById(Long id) {
        return students.stream()
                .filter(student -> student.getId().equals(id))
                .findFirst();
    }

    public List<Student> findByLastName(String lastName) {
        return students.stream()
                .filter(student -> student.getLastName()
                        .toLowerCase()
                        .contains(lastName.toLowerCase()))
                .toList();
    }

    public List<Student> findByGroupId(Long groupId) {
        return students.stream()
                .filter(student -> student.getGroupId().equals(groupId))
                .toList();
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(nextId++);
            students.add(student);
        } else {
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getId().equals(student.getId())) {
                    students.set(i, student);
                    return student;
                }
            }
            throw new IllegalArgumentException("Студента не знайдено");
        }
        return student;
    }

    public void deleteById(Long id) {
        students.removeIf(student -> student.getId().equals(id));
    }

    public void deleteByGroupId(Long groupId) {
        students.removeIf(student -> student.getGroupId().equals(groupId));
    }
}
