package com.example.spring2.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(basePackages="com.example.spring2.api")
public class ApiExceptionHandler {
 private ResponseEntity<Map<String,Object>> error(HttpStatus status,String message,WebRequest request) {
   Map<String,Object> body=new LinkedHashMap<>();
   body.put("timestamp",Instant.now().toString());
   body.put("status",status.value());
   body.put("error",status.getReasonPhrase());
   body.put("message",message);
   body.put("path",request.getDescription(false).replaceFirst("^uri=",""));
   return ResponseEntity.status(status).body(body);
 }
 @ExceptionHandler(PatchTestFailedException.class)
 public ResponseEntity<Map<String,Object>> failedTest(PatchTestFailedException e,WebRequest req){return error(HttpStatus.CONFLICT,e.getMessage(),req);}
 @ExceptionHandler(NotFoundException.class)
 public ResponseEntity<Map<String,Object>> missing(NotFoundException e,WebRequest req){return error(HttpStatus.NOT_FOUND,e.getMessage(),req);}
 @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
 public ResponseEntity<Map<String,Object>> invalid(Exception e,WebRequest req){return error(HttpStatus.BAD_REQUEST,e.getMessage(),req);}
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e,WebRequest req){
    String msg=e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).distinct().reduce((a,b)->a+"; "+b).orElse("Некоректні дані");
    return error(HttpStatus.BAD_REQUEST,msg,req);
 }
}
