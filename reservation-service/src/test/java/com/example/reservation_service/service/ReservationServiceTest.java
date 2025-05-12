package com.example.reservation_service.service;

import com.example.reservation_service.dto.GuestDTO;
import com.example.reservation_service.dto.ReservationStatusUpdateDTO;
import com.example.reservation_service.dto.RoomDTO;
import com.example.reservation_service.exception.*;
import com.example.reservation_service.model.Reservation;
import com.example.reservation_service.repository.ReservationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomServiceClient roomServiceClient;

    @Mock
    private GuestServiceClient guestServiceClient;

    @Mock
    private NotificationPublisher notificationPublisher;

    private RoomDTO room;
    private GuestDTO guest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        room = new RoomDTO(1L, "Deluxe", 1000.0, 4);
        guest = new GuestDTO(1L, "Amit", "amit@gmail.com");
    }

    @Test
    void testCreateReservation_Success() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(2);

        when(reservationRepository.findOverlappingReservations(1L, checkIn, checkOut)).thenReturn(Collections.emptyList());
        when(roomServiceClient.getRoomById(1L)).thenReturn(room);
        when(guestServiceClient.getGuestById(1L)).thenReturn(guest);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation reservation = reservationService.createReservation(
                1L, 1L, 2, 1, checkIn.toString(), checkOut.toString());

        assertEquals(3, reservation.getNumberOfAdults() + reservation.getNumberOfChildren());
        assertEquals("PENDING", reservation.getStatus());
    }

    @Test
    void testCreateReservation_RoomNotAvailable() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(2);

        when(reservationRepository.findOverlappingReservations(1L, checkIn, checkOut))
                .thenReturn(List.of(new Reservation()));

        assertThrows(RoomNotAvailableException.class, () ->
                reservationService.createReservation(1L, 1L, 2, 1, checkIn.toString(), checkOut.toString()));
    }

    @Test
    void testGetReservationById_NotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () ->
                reservationService.getReservationById(99L));
    }

    @Test
    void testUpdateReservation_Success() {
        Reservation existing = new Reservation(1L, 1L, 2, 1, LocalDate.now(), LocalDate.now().plusDays(2), 2, "PENDING", 2000.0);
        Reservation updated = new Reservation(1L, 1L, 3, 0, LocalDate.now(), LocalDate.now().plusDays(3), 3, "PENDING", 3000.0);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomServiceClient.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation result = reservationService.updateReservation(1L, updated);

        assertEquals(3, result.getNumberOfAdults());
        assertEquals(3, result.getNumberOfNights());
    }

    @Test
    void testDeleteReservation_NotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () ->
                reservationService.deleteReservation(1L));
    }

    @Test
    void testUpdateReservationStatus_Booked() {
        Reservation reservation = new Reservation(1L, 1L, 2, 0, LocalDate.now(), LocalDate.now().plusDays(1), 1, "PENDING", 1000.0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(guestServiceClient.getGuestById(1L)).thenReturn(guest);
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Reservation result = reservationService.updateReservationStatus(1L, true);

        assertEquals("BOOKED", result.getStatus());
        verify(notificationPublisher, times(1)).sendNotification(anyString(), anyString(), contains("confirmed"));
    }

    @Test
    void testUpdateReservationStatus_Cancelled() {
        Reservation reservation = new Reservation(1L, 1L, 2, 0, LocalDate.now(), LocalDate.now().plusDays(1), 1, "PENDING", 1000.0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(guestServiceClient.getGuestById(1L)).thenReturn(guest);
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Reservation result = reservationService.updateReservationStatus(1L, false);

        assertEquals("CANCELLED", result.getStatus());
        verify(notificationPublisher, times(1)).sendNotification(anyString(), anyString(), contains("cancelled"));
    }

    @Test
    void testCheckoutReservation_Success() {
        Reservation reservation = new Reservation(1L, 1L, 2, 1, LocalDate.now(), LocalDate.now().plusDays(2), 2, "BOOKED", 2000.0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ReservationStatusUpdateDTO dto = new ReservationStatusUpdateDTO("CHECKED_OUT");

        Reservation result = reservationService.checkoutReservation(1L, dto);
        assertEquals("CHECKED_OUT", result.getStatus());
    }

    @Test
    void testGetBookedRoomIds() {
        Reservation reservation = new Reservation(1L, 1L, 2, 1,
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 5, 15),
                5, "BOOKED", 5000.0);

        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<Long> bookedRoomIds = reservationService.getBookedRoomIds(
                LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 14));

        assertTrue(bookedRoomIds.contains(1L));
    }
}
