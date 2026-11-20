package com.example.spring2.api.dto;
import jakarta.validation.constraints.NotBlank;
public record GroupRequest(@NotBlank(message="Назва групи обов'язкова") String name) {}
