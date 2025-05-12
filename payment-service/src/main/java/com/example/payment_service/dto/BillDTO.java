package com.example.payment_service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BillDTO {
	private Long id;
    private String billNo;
    private Long reservationId;
    private Long guestId;
    private Long roomId;
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;
    private LocalDate date;
	
}
