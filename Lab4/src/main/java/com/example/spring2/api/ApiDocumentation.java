package com.example.spring2.api;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ApiDocumentation {
 @Bean
 public OpenAPI deanApi(){
  return new OpenAPI().info(new Info().title("Електронний деканат — REST API")
    .version("1.0.0").description("Лабораторна робота №4. RESTful сервіс для навчальних груп та студентів. Дані зберігаються в оперативній пам'яті."));
 }
}
