package com.example.spring2.api;

import com.example.spring2.api.dto.*;
import com.example.spring2.model.Group;
import com.example.spring2.service.GroupService;
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
@RequestMapping("/api/groups")
@Tag(name="Групи",description="CRUD, фільтрація, пагінація і часткове оновлення навчальних груп")
public class GroupRestController {
 private final GroupService service;
 private static final Set<String> FIELDS=Set.of("name");
 public GroupRestController(GroupService service){this.service=service;}
 private Group get(Long id){return service.getAllGroups().stream().filter(g->g.getId().equals(id)).findFirst().orElseThrow(()->new NotFoundException("Групу "+id+" не знайдено"));}

 @GetMapping
 @Operation(summary="Список груп",description="Повертає всі групи; фільтр name шукає підрядок без урахування регістру. Пагінація починається з page=0.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Список отримано"),@ApiResponse(responseCode="400",description="Невірні параметри сторінки")})
 public PageResponse<GroupResponse> list(@RequestParam(required=false) String name,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){
  if(page<0||size<1||size>100)throw new IllegalArgumentException("page >= 0; size від 1 до 100");
  List<GroupResponse> data=service.getAllGroups().stream().filter(g->name==null||g.getName().toLowerCase().contains(name.toLowerCase()))
   .sorted(Comparator.comparing(Group::getId)).map(GroupResponse::from).toList();
  return PageResponse.of(data,page,size);
 }
 @GetMapping("/{id}")
 @Operation(summary="Отримати групу",description="Повертає навчальну групу за її числовим ID.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Знайдено"),@ApiResponse(responseCode="404",description="Групи немає"),@ApiResponse(responseCode="400",description="Некоректний ID")})
 public GroupResponse one(@PathVariable Long id){return GroupResponse.from(get(id));}
 @PostMapping
 @Operation(summary="Створити групу",description="Створює групу з непорожньою назвою. ID призначає сервер.")
 @ApiResponses({@ApiResponse(responseCode="201",description="Групу створено"),@ApiResponse(responseCode="400",description="Неправильні дані")})
 public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest request){
   Group group=service.saveGroup(new Group(null,request.name()));
   return ResponseEntity.created(URI.create("/api/groups/"+group.getId())).body(GroupResponse.from(group));
 }
 @PutMapping("/{id}")
 @Operation(summary="Повністю оновити групу",description="Замінює назву існуючої групи. У тілі необхідно передати name.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Оновлено"),@ApiResponse(responseCode="400",description="Невірне тіло"),@ApiResponse(responseCode="404",description="Групи немає")})
 public GroupResponse update(@PathVariable Long id,@Valid @RequestBody GroupRequest request){
   get(id);return GroupResponse.from(service.saveGroup(new Group(id,request.name())));
 }
 @DeleteMapping("/{id}")
 @Operation(summary="Видалити групу",description="Видаляє групу і всіх її студентів. Повторне видалення повертає 404.")
 @ApiResponses({@ApiResponse(responseCode="204",description="Видалено"),@ApiResponse(responseCode="404",description="Групи немає")})
 public ResponseEntity<Void> delete(@PathVariable Long id){get(id);service.deleteGroup(id);return ResponseEntity.noContent().build();}

 @PatchMapping(path="/{id}",consumes="application/merge-patch+json")
 @Operation(summary="JSON Merge Patch групи",description="Часткове оновлення за RFC 7386. Content-Type: application/merge-patch+json. Приклад: {\"name\":\"ІА-42\"}; null видаляє поле, тому для обов'язкового name призведе до 400.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Оновлено"),@ApiResponse(responseCode="400",description="Невірний PATCH"),@ApiResponse(responseCode="404",description="Групи немає")})
 public GroupResponse merge(@PathVariable Long id,@RequestBody Map<String,Object> patch){
  Group g=get(id);Map<String,Object> current=new LinkedHashMap<>();current.put("name",g.getName());
  var next=PatchHelper.merge(current,patch,FIELDS);
  return GroupResponse.from(service.saveGroup(new Group(id,PatchHelper.requiredText(next,"name"))));
 }
 @PatchMapping(path="/{id}",consumes="application/json-patch+json")
 @Operation(summary="JSON Patch групи",description="Часткове оновлення за RFC 6902. Content-Type: application/json-patch+json. Приклад: [{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"ІА-42\"}]. Підтримуються add, remove, replace, test, copy, move для поля /name.")
 @ApiResponses({@ApiResponse(responseCode="200",description="Оновлено"),@ApiResponse(responseCode="400",description="Некоректний PATCH"),@ApiResponse(responseCode="404",description="Групи немає"),@ApiResponse(responseCode="409",description="test не пройшов")})
 public GroupResponse json(@PathVariable Long id,@RequestBody List<Map<String,Object>> operations){
  Group g=get(id);Map<String,Object> current=new LinkedHashMap<>();current.put("name",g.getName());
  var next=PatchHelper.jsonPatch(current,operations,FIELDS);
  return GroupResponse.from(service.saveGroup(new Group(id,PatchHelper.requiredText(next,"name"))));
 }
}
