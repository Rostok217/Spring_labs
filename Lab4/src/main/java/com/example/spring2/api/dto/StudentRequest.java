package com.example.spring2.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record StudentRequest(
  @NotBlank(message="Ім'я обов'язкове") String firstName,
  @NotBlank(message="Прізвище обов'язкове") String lastName,
  @NotNull(message="Потрібно вказати groupId") Long groupId) {}
