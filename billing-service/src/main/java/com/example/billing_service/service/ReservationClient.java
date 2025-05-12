package com.example.billing_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.billing_service.dto.ReservationDTO;

@FeignClient(name = "reservation-service")
public interface ReservationClient {
    @GetMapping("/api/reservations/{id}")
    ReservationDTO getReservationById(@PathVariable Long id);
}