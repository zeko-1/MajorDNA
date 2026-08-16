package com.majordna.api;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> invalid(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}
 @ExceptionHandler(SecurityException.class) public ResponseEntity<Map<String,String>> forbidden(SecurityException e){return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message",e.getMessage()));}
 @ExceptionHandler(java.util.NoSuchElementException.class) public ResponseEntity<Map<String,String>> missing(java.util.NoSuchElementException e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",e.getMessage()));}
}
