
package com.example.spring2.repository;

import com.example.spring2.model.Group;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GroupRepository {

    private final List<Group> groups = new ArrayList<>();
    private long nextId = 1;

    public GroupRepository() {
        groups.add(new Group(nextId++, "ІА-43"));
        groups.add(new Group(nextId++, "ІМ-44"));
        groups.add(new Group(nextId++, "ІС-41"));
    }

    public List<Group> findAll() {
        return new ArrayList<>(groups);
    }

    public Optional<Group> findById(Long id) {
        return groups.stream()
                .filter(group -> group.getId().equals(id))
                .findFirst();
    }

    public Group save(Group group) {
        if (group.getId() == null) {
            group.setId(nextId++);
            groups.add(group);
        } else {
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).getId().equals(group.getId())) {
                    groups.set(i, group);
                    return group;
                }
            }
            throw new IllegalArgumentException("Групу не знайдено");
        }
        return group;
    }

    public void deleteById(Long id) {
        groups.removeIf(group -> group.getId().equals(id));
    }
}
