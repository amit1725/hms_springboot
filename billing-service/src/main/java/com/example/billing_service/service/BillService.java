package com.example.billing_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.billing_service.dto.ReservationDTO;
import com.example.billing_service.exception.ReservationNotFoundException;
import com.example.billing_service.model.Bill;
import com.example.billing_service.repository.BillRepository;

@Service
public class BillService {

    @Autowired 
    private BillRepository billRepository;
    @Autowired 
    private ReservationClient reservationClient;

    public Double calculateTotalPrice(Long reservationId) throws ReservationNotFoundException {
        ReservationDTO dto = reservationClient.getReservationById(reservationId);
        if (dto == null) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }
        double base = dto.getTotalPrice();
        double tax = base * 0.18;
        return base + tax;
    }

    public Bill createBill(Long reservationId) throws ReservationNotFoundException {
        ReservationDTO dto = reservationClient.getReservationById(reservationId);
        if (dto == null) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }
        double tax = dto.getTotalPrice() * 0.18;

        Bill bill = new Bill();
        bill.setBillNo("BILL-" + LocalDate.now() + "-" + reservationId);
        bill.setReservationId(reservationId);
        bill.setGuestId(dto.getGuestId());
        bill.setRoomId(dto.getRoomId());
        bill.setBaseAmount(dto.getTotalPrice());
        bill.setTaxAmount(tax);
        bill.setTotalAmount(dto.getTotalPrice() + tax);
        bill.setDate(LocalDate.now());

        return billRepository.save(bill);
    }

	public List<Bill> getAllBills() {
		return billRepository.findAll();
	}
}