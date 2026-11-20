package com.example.spring2.api;

import com.example.spring2.api.dto.*;
import com.example.spring2.model.Student;
import com.example.spring2.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name="Студенти",description="CRUD, фільтрація, пагінація і RFC 6902/7386 для студентів")
public class StudentRestController {
 private final StudentService service;
 private static final Set<String> FIELDS=Set.of("firstName","lastName","groupId");
 public StudentRestController(StudentService service){this.service=service;}
 private Student get(Long id){return service.getAllStudents().stream().filter(s->s.getId().equals(id)).findFirst().orElseThrow(()->new NotFoundException("Студента "+id+" не знайдено"));}
 private Student save(Long id,Map<String,Object> next){
  return service.saveStudent(new Student(id,PatchHelper.requiredText(next,"firstName"),PatchHelper.requiredText(next,"lastName"),PatchHelper.requiredLong(next,"groupId")));
 }
 private Map<String,Object> fields(Student s){
  Map<String,Object> result=new LinkedHashMap<>();result.put("firstName",s.getFirstName());result.put("lastName",s.getLastName());result.put("groupId",s.getGroupId());return result;
 }
 @GetMapping
 @Operation(summary="Список студентів",description="Фільтр lastName шукає частину прізвища без урахування регістру; groupId фільтрує за групою. page з нуля, size від 1 до 100. Дані повертаються у content із totalElements та totalPages.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Успішно"),@ApiResponse(responseCode="400",description="Некоректні параметри")})
 public PageResponse<StudentResponse> list(@RequestParam(required=false) String lastName,@RequestParam(required=false) Long groupId,
    @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){
  if(page<0||size<1||size>100)throw new IllegalArgumentException("page >= 0; size від 1 до 100");
  List<StudentResponse> data=service.getAllStudents().stream()
    .filter(s->lastName==null||s.getLastName().toLowerCase().contains(lastName.toLowerCase()))
    .filter(s->groupId==null||s.getGroupId().equals(groupId))
    .sorted(Comparator.comparing(Student::getId)).map(StudentResponse::from).toList();
  return PageResponse.of(data,page,size);
 }
 @GetMapping("/{id}")
 @Operation(summary="Отримати студента",description="Повертає студента за ID із назвою ідентифікатора групи groupId.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Знайдено"),@ApiResponse(responseCode="404",description="Не знайдено"),@ApiResponse(responseCode="400",description="Некоректний ID")})
 public StudentResponse one(@PathVariable Long id){return StudentResponse.from(get(id));}
 @PostMapping
 @Operation(summary="Створити студента",description="Створює студента. Потрібні firstName, lastName, groupId; група має існувати. ID призначає сервер.")
 @ApiResponses({@ApiResponse(responseCode="201",description="Створено"),@ApiResponse(responseCode="400",description="Невірні дані"),@ApiResponse(responseCode="404",description="Група не існує")})
 public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request){
  Student s=service.saveStudent(new Student(null,request.firstName(),request.lastName(),request.groupId()));
  return ResponseEntity.created(URI.create("/api/students/"+s.getId())).body(StudentResponse.from(s));
 }
 @PutMapping("/{id}")
 @Operation(summary="Повністю оновити студента",description="Замінює всі поля існуючого студента: firstName, lastName, groupId.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Оновлено"),@ApiResponse(responseCode="400",description="Невірні дані"),@ApiResponse(responseCode="404",description="Студента або групу не знайдено")})
 public StudentResponse update(@PathVariable Long id,@Valid @RequestBody StudentRequest request){
  get(id);return StudentResponse.from(service.saveStudent(new Student(id,request.firstName(),request.lastName(),request.groupId())));
 }
 @DeleteMapping("/{id}")
 @Operation(summary="Видалити студента",description="Видаляє студента з указаним ID.")
 @ApiResponses({@ApiResponse(responseCode="204",description="Видалено"),@ApiResponse(responseCode="404",description="Студента немає")})
 public ResponseEntity<Void> delete(@PathVariable Long id){get(id);service.deleteStudent(id);return ResponseEntity.noContent().build();}
 @PatchMapping(path="/{id}",consumes="application/merge-patch+json")
 @Operation(summary="JSON Merge Patch студента",description="Часткове оновлення RFC 7386; Content-Type: application/merge-patch+json. Приклад: {\"lastName\":\"Петренко\"}. null видаляє поле, але обов'язкові поля видаляти не можна.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Оновлено"),@ApiResponse(responseCode="400",description="Некоректний PATCH"),@ApiResponse(responseCode="404",description="Студента/групу не знайдено")})
 public StudentResponse merge(@PathVariable Long id,@RequestBody Map<String,Object> patch){
  return StudentResponse.from(save(id,PatchHelper.merge(fields(get(id)),patch,FIELDS)));
 }
 @PatchMapping(path="/{id}",consumes="application/json-patch+json")
 @Operation(summary="JSON Patch студента",description="Часткове оновлення RFC 6902; Content-Type: application/json-patch+json. Приклад: [{\"op\":\"replace\",\"path\":\"/lastName\",\"value\":\"Петренко\"}]. Доступні add, remove, replace, test, copy, move.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Оновлено"),@ApiResponse(responseCode="400",description="Некоректний PATCH"),@ApiResponse(responseCode="404",description="Студента/групу не знайдено"),@ApiResponse(responseCode="409",description="test не пройшов")})
 public StudentResponse json(@PathVariable Long id,@RequestBody List<Map<String,Object>> operations){
  return StudentResponse.from(save(id,PatchHelper.jsonPatch(fields(get(id)),operations,FIELDS)));
 }
}
