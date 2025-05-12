package com.example.room_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(RoomNotFoundException.class)
 public ResponseEntity<Map<String, Object>> handleRoomNotFound(RoomNotFoundException ex) {
     Map<String, Object> body = new HashMap<>();
     body.put("timestamp", LocalDateTime.now());
     body.put("message", ex.getMessage());
     body.put("status", HttpStatus.NOT_FOUND.value());

     return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<Map<String, Object>> handleGlobal(Exception ex) {
     Map<String, Object> body = new HashMap<>();
     body.put("timestamp", LocalDateTime.now());
     body.put("message", "Something went wrong");
     body.put("details", ex.getMessage());
     body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

     return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
 }
}
