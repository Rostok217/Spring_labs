
package com.example.spring2.config;

import java.util.UUID;

public class OperationInfo {

    private final String id;

    public OperationInfo() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
