package com.example.reservation_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.reservation_service.dto.RoomDTO;

@FeignClient(name = "ROOM-SERVICE")
public interface RoomServiceClient {

    @GetMapping("api/rooms/{id}")
    RoomDTO getRoomById(@PathVariable Long id);


}
