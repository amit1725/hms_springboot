package com.example.billing_service.dto;

import lombok.Data;

@Data
public class ReservationDTO {
    private Long reservationId;
    private Long guestId;
    private Long roomId;
    private Integer numberOfNights;
    private Double totalPrice;
	
}