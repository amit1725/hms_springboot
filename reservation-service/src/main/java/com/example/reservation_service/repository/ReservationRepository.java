package com.example.reservation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.reservation_service.model.Reservation;

import java.time.LocalDate;
import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	 List<Reservation> findByGuestId(Long guestId);
	 
	 @Query("SELECT r FROM Reservation r WHERE r.roomId = :roomId AND r.checkOutDate > :checkInDate AND r.checkInDate < :checkOutDate AND (r.status = 'PENDING' OR r.status = 'BOOKED')")
	 List<Reservation> findOverlappingReservations(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);

}
