
package com.example.spring2.service;

import com.example.spring2.model.Group;
import com.example.spring2.repository.GroupRepository;
import com.example.spring2.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    // Ін'єкція залежностей через конструктор
    public GroupService(GroupRepository groupRepository,
                        StudentRepository studentRepository) {
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
    }

    // Отримання всіх навчальних груп
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    // Пошук групи за ID
    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Групу не знайдено"));
    }

    // Створення або редагування групи
    public Group saveGroup(Group group) {

        if (group.getName() == null ||
                group.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Назва групи не може бути порожньою");
        }

        return groupRepository.save(group);
    }

    // Видалення групи та всіх її студентів
    public void deleteGroup(Long id) {

        // Перевіряємо, чи існує група
        getGroupById(id);

        // Видаляємо студентів цієї групи
        studentRepository.deleteByGroupId(id);

        // Видаляємо саму групу
        groupRepository.deleteById(id);
    }
}
