package com.example.billing_service.service;

import com.example.billing_service.dto.ReservationDTO;
import com.example.billing_service.exception.ReservationNotFoundException;
import com.example.billing_service.model.Bill;
import com.example.billing_service.repository.BillRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillServiceTest {

    @InjectMocks
    private BillService billService;

    @Mock
    private BillRepository billRepository;

    @Mock
    private ReservationClient reservationClient;

    private ReservationDTO mockReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockReservation = new ReservationDTO();
        mockReservation.setReservationId(1L);
        mockReservation.setGuestId(100L);
        mockReservation.setRoomId(200L);
        mockReservation.setTotalPrice(1000.0);
    }

    @Test
    void testCalculateTotalPrice_shouldReturnTotalWithTax() throws ReservationNotFoundException {
        when(reservationClient.getReservationById(1L)).thenReturn(mockReservation);

        Double total = billService.calculateTotalPrice(1L);

        assertEquals(1180.0, total);
        verify(reservationClient, times(1)).getReservationById(1L);
    }

    @Test
    void testCalculateTotalPrice_whenReservationNotFound_shouldThrowException() {
        when(reservationClient.getReservationById(1L)).thenReturn(null);

        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> billService.calculateTotalPrice(1L)
        );

        assertEquals("Reservation with ID 1 not found.", exception.getMessage());
    }

    @Test
    void testCreateBill_shouldReturnSavedBill() throws ReservationNotFoundException {
        when(reservationClient.getReservationById(1L)).thenReturn(mockReservation);
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill bill = billService.createBill(1L);

        assertNotNull(bill);
        assertEquals("BILL-" + LocalDate.now() + "-1", bill.getBillNo());
        assertEquals(100L, bill.getGuestId());
        assertEquals(200L, bill.getRoomId());
        assertEquals(1000.0, bill.getBaseAmount());
        assertEquals(180.0, bill.getTaxAmount());
        assertEquals(1180.0, bill.getTotalAmount());
        assertEquals(LocalDate.now(), bill.getDate());

        verify(reservationClient).getReservationById(1L);
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    void testCreateBill_whenReservationNotFound_shouldThrowException() {
        when(reservationClient.getReservationById(1L)).thenReturn(null);

        assertThrows(ReservationNotFoundException.class, () -> billService.createBill(1L));
        verify(billRepository, never()).save(any());
    }
}
