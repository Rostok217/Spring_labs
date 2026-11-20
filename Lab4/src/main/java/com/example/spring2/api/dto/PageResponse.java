package com.example.spring2.api.dto;
import java.util.List;
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
 public static <T> PageResponse<T> of(List<T> all, int page, int size) {
   int from=(int)Math.min((long)page*size,all.size());
   int to=(int)Math.min((long)from+size,all.size());
   return new PageResponse<>(List.copyOf(all.subList(from,to)),page,size,all.size(),
      (int)((all.size()+(long)size-1)/size));
 }
}
