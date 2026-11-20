
package com.example.spring2.controller;

import com.example.spring2.model.Student;
import com.example.spring2.model.Group;
import com.example.spring2.service.StudentService;
import com.example.spring2.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final GroupService groupService;

    public StudentController(StudentService studentService,
                             GroupService groupService) {
        this.studentService = studentService;
        this.groupService = groupService;
    }

    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String groupName,
            Model model) {

        List<Student> students = studentService.getAllStudents();

        if (lastName != null && !lastName.isBlank()) {
            students = studentService.searchByLastName(lastName);
        }

        if (groupName != null && !groupName.isBlank()) {
            List<Long> groupIds = groupService.getAllGroups()
                    .stream()
                    .filter(group -> group.getName()
                            .toLowerCase()
                            .contains(groupName.toLowerCase()))
                    .map(Group::getId)
                    .toList();

            students = students.stream()
                    .filter(student -> groupIds.contains(student.getGroupId()))
                    .toList();
        }

        model.addAttribute("students", students);
        model.addAttribute("groups", groupService.getAllGroups());
        model.addAttribute("lastName", lastName);
        model.addAttribute("groupName", groupName);

        return "students";
    }

    @GetMapping("/new")
    public String newStudent(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("groups", groupService.getAllGroups());
        return "student-form";
    }

    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("groups", groupService.getAllGroups());
        return "student-form";
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}
