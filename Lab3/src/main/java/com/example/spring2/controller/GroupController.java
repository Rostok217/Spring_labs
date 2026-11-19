
package com.example.spring2.controller;

import com.example.spring2.model.Group;
import com.example.spring2.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    // Ін'єкція залежності через конструктор
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // Перегляд списку всіх груп
    @GetMapping
    public String listGroups(Model model) {
        model.addAttribute("groups", groupService.getAllGroups());
        return "groups";
    }

    // Відкриття форми створення групи
    @GetMapping("/new")
    public String newGroup(Model model) {
        model.addAttribute("group", new Group());
        return "group-form";
    }

    // Відкриття форми редагування групи
    @GetMapping("/edit/{id}")
    public String editGroup(@PathVariable Long id, Model model) {
        model.addAttribute("group", groupService.getGroupById(id));
        return "group-form";
    }

    // Збереження нової або відредагованої групи
    @PostMapping("/save")
    public String saveGroup(@ModelAttribute Group group) {
        groupService.saveGroup(group);
        return "redirect:/groups";
    }

    // Видалення групи
    @PostMapping("/delete/{id}")
    public String deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return "redirect:/groups";
    }
}

