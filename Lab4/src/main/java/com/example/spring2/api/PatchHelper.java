package com.example.spring2.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** JSON Merge Patch (RFC 7386) and JSON Patch (RFC 6902) for flat resource fields. */
public final class PatchHelper {
 private PatchHelper() {}
 public static Map<String,Object> merge(Map<String,Object> current,Map<String,Object> patch,Set<String> allowed) {
  if(patch==null) throw new IllegalArgumentException("Тіло PATCH не може бути null");
  Map<String,Object> result=new LinkedHashMap<>(current);
  for(var entry:patch.entrySet()){
   requireField(entry.getKey(),allowed);
   if(entry.getValue()==null) result.remove(entry.getKey());
   else result.put(entry.getKey(),entry.getValue());
  }
  return result;
 }
 public static Map<String,Object> jsonPatch(Map<String,Object> current,List<Map<String,Object>> operations,Set<String> allowed) {
  if(operations==null)throw new IllegalArgumentException("Очікується масив операцій JSON Patch");
  Map<String,Object> result=new LinkedHashMap<>(current);
  for(Map<String,Object> op:operations){
   if(op==null)throw new IllegalArgumentException("Порожня операція JSON Patch");
   String action=requiredString(op,"op");
   String path=fieldFromPath(requiredString(op,"path"),allowed);
   switch(action){
    case "add" -> {if(!op.containsKey("value"))throw new IllegalArgumentException("add потребує value");result.put(path,op.get("value"));}
    case "replace" -> {if(!result.containsKey(path))throw new IllegalArgumentException("Поля немає: "+path);if(!op.containsKey("value"))throw new IllegalArgumentException("replace потребує value");result.put(path,op.get("value"));}
    case "remove" -> {if(!result.containsKey(path))throw new IllegalArgumentException("Поля немає: "+path);result.remove(path);}
    case "test" -> {if(!op.containsKey("value") || !result.containsKey(path) || !sameValue(result.get(path),op.get("value")))throw new PatchTestFailedException("Операція test не пройдена: "+path);}
    case "copy", "move" -> {
      String from=fieldFromPath(requiredString(op,"from"),allowed);
      if(!result.containsKey(from))throw new IllegalArgumentException("Поля немає: "+from);
      Object value=result.get(from);
      if(action.equals("move"))result.remove(from);
      result.put(path,value);
    }
    default -> throw new IllegalArgumentException("Невідома операція JSON Patch: "+action);
   }
  }
  return result;
 }
 private static boolean sameValue(Object a,Object b){
   if(a instanceof Number x && b instanceof Number y) {
     return new java.math.BigDecimal(x.toString()).compareTo(new java.math.BigDecimal(y.toString()))==0;
   }
   return Objects.equals(a,b);
 }
 private static String requiredString(Map<String,Object> obj,String key){
  if(!(obj.get(key) instanceof String s) || s.isBlank())throw new IllegalArgumentException("Обов'язкове поле: "+key);
  return s;
 }
 private static String fieldFromPath(String path,Set<String> allowed){
  if(!path.startsWith("/") || path.length()<2 || path.indexOf('/',1)!=-1) throw new IllegalArgumentException("Дозволені лише поля верхнього рівня: "+path);
  String key=path.substring(1).replace("~1","/").replace("~0","~");
  requireField(key,allowed);
  return key;
 }
 private static void requireField(String field,Set<String> allowed){
  if(!allowed.contains(field))throw new IllegalArgumentException("Поле недоступне для PATCH: "+field);
 }
 public static String requiredText(Map<String,Object> obj,String key){
   Object v=obj.get(key);
   if(!(v instanceof String s) || s.isBlank())throw new IllegalArgumentException("Некоректне значення: "+key);
   return s;
 }
 public static Long requiredLong(Map<String,Object> obj,String key){
   Object v=obj.get(key);
   if(!(v instanceof Number n) || n.doubleValue()!=n.longValue() || n.longValue()<1)throw new IllegalArgumentException("Некоректне значення: "+key);
   return n.longValue();
 }
}
