package com.example.reservation_service.service;

import com.example.reservation_service.dto.GuestDTO;
import com.example.reservation_service.dto.ReservationStatusUpdateDTO;
import com.example.reservation_service.dto.RoomDTO;
import com.example.reservation_service.exception.RoomNotAvailableException;
import com.example.reservation_service.exception.GuestNotFoundException;
import com.example.reservation_service.exception.ReservationNotFoundException;
import com.example.reservation_service.exception.RoomCapacityExceededException;
import com.example.reservation_service.model.Reservation;
import com.example.reservation_service.repository.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomServiceClient roomServiceClient;

    @Autowired
    private GuestServiceClient guestServiceClient;
    
    @Autowired
    private NotificationPublisher notificationPublisher;
    
    
    public Reservation createReservation(Long roomId, Long guestId, Integer numberOfAdults,
                                         Integer numberOfChildren, String checkInDate, String checkOutDate) {

    	 LocalDate checkIn = LocalDate.parse(checkInDate);
    	 LocalDate checkOut = LocalDate.parse(checkOutDate);

    	 List<Reservation> overlappingReservations = reservationRepository
    	            .findOverlappingReservations(roomId, checkIn, checkOut);

    	 if (!overlappingReservations.isEmpty()) {
    	       throw new RoomNotAvailableException("Room is already booked for the selected dates");
    	 }

        RoomDTO room = roomServiceClient.getRoomById(roomId);
        int totalGuests = numberOfAdults + numberOfChildren;
        if (totalGuests > room.getCapacity()) {
            throw new RoomCapacityExceededException("Room capacity exceeded");
        }

        // Validate guest
        GuestDTO guest = guestServiceClient.getGuestById(guestId);
        if (guest == null) {
            throw new GuestNotFoundException("Guest not found");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        Reservation reservation = new Reservation(
                roomId,
                guestId,
                numberOfAdults,
                numberOfChildren,
                checkIn,
                checkOut,
                (int) nights,
                "PENDING",
                room.getPrice() * nights
        );
        
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }

    public Reservation updateReservation(Long id, Reservation updated) {
        Reservation existing = getReservationById(id);
        if(existing==null) {
        	throw new ReservationNotFoundException("Reservation not found");
        }
        existing.setNumberOfAdults(updated.getNumberOfAdults());
        existing.setNumberOfChildren(updated.getNumberOfChildren());
        existing.setCheckInDate(updated.getCheckInDate());
        existing.setCheckOutDate(updated.getCheckOutDate());

        long nights = ChronoUnit.DAYS.between(updated.getCheckInDate(), updated.getCheckOutDate());
        RoomDTO room = roomServiceClient.getRoomById(updated.getRoomId());

        existing.setNumberOfNights((int) nights);
        existing.setTotalPrice(room.getPrice() * nights);

        return reservationRepository.save(existing);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = getReservationById(id);
        if(reservation==null) {
        	throw new ReservationNotFoundException("Reservation not found");
        }
        reservationRepository.delete(reservation);
    }

    public Reservation checkoutReservation(Long id, ReservationStatusUpdateDTO reservationDto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }

        Reservation reservation = reservationOptional.get();
        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new IllegalStateException("Cannot checkout a reservation that is not booked");
        }
        reservation.setStatus(reservationDto.getStatus());
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationByGuestId(Long guestId) {
        List<Reservation> reservation = reservationRepository.findByGuestId(guestId);
        if (reservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        return reservation;
    }
	
	public Reservation updateReservationStatus(Long id, boolean paymentSuccess) {
	    Optional<Reservation> reservationOptional = reservationRepository.findById(id);

	    if (reservationOptional.isEmpty()) {
	        throw new ReservationNotFoundException("Reservation not found");
	    }

	    Reservation reservation = reservationOptional.get();
	    GuestDTO guest=guestServiceClient.getGuestById(reservation.getGuestId());

	    if (paymentSuccess) {
	        reservation.setStatus("BOOKED");
	        notificationPublisher.sendNotification(
	            guest.getEmail(),
	            "Booking Confirmed",
	            "Your booking for Room ID: " + reservation.getRoomId() + " has been confirmed."
	        );
	    } else {
	        reservation.setStatus("CANCELLED");
	        notificationPublisher.sendNotification(
	            guest.getEmail(),
	            "Booking Failed",
	            "Your booking for Room ID: " + reservation.getRoomId() + " has been cancelled due to payment failure."
	        );
	    }

	    return reservationRepository.save(reservation);
	}
	
	   public List<Long> getBookedRoomIds(LocalDate checkInDate, LocalDate checkOutDate) {
	        List<Reservation> allReservations = reservationRepository.findAll();

	        return allReservations.stream()
	                .filter(reservation ->
	                        (reservation.getStatus().equals("PENDING") || reservation.getStatus().equals("BOOKED")) &&
	                        reservation.getCheckOutDate().isAfter(checkInDate) &&
	                        reservation.getCheckInDate().isBefore(checkOutDate)
	                )
	                .map(Reservation::getRoomId)
	                .distinct()
	                .collect(Collectors.toList());
	    }

}
