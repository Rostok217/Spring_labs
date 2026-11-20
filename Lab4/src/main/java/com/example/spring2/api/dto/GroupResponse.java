package com.example.spring2.api.dto;
import com.example.spring2.model.Group;
public record GroupResponse(Long id, String name) {
 public static GroupResponse from(Group group) { return new GroupResponse(group.getId(), group.getName()); }
}
