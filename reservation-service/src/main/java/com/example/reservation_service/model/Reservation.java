package com.example.reservation_service.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;
    private Long guestId;

    private Integer numberOfAdults;
    private Integer numberOfChildren;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Integer numberOfNights;
    private String status;
    private Double totalPrice;

	public Reservation(Long roomId, Long guestId, Integer numberOfAdults, Integer numberOfChildren,
			LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfNights, String status, Double totalPrice) {
		super();
		this.roomId = roomId;
		this.guestId = guestId;
		this.numberOfAdults = numberOfAdults;
		this.numberOfChildren = numberOfChildren;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.numberOfNights = numberOfNights;
		this.status = status;
		this.totalPrice = totalPrice;
	}
	
}
