package com.example.payment_service.service;

import com.example.payment_service.dto.BillDTO;
import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

	@Autowired
    private RazorpayService razorpayService;
	@Autowired
    private PaymentRepository paymentRepository;
	@Autowired
    private BillingClient billingClient;
	@Autowired
    private ReservationServiceClient reservationServiceClient;

    // Handle payment request and create Razorpay order
    public Map<String, Object> handlePayment(Long reservationId) {
        Double total = billingClient.getTotalPrice(reservationId);

        // Step 1: Create Razorpay order
        String receiptId = "txn_" + System.currentTimeMillis(); // Custom receipt ID
        Order order;
        try {
            order = razorpayService.createOrder(total, "INR", receiptId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }

        // Step 2: Save payment data in DB
        Payment payment = new Payment();
        payment.setReservationId(reservationId);
        payment.setAmountPaid(total);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setRazorpayOrderId(order.get("id").toString());
        payment.setPaymentSuccess(false); // Initially set payment as failed
        Payment saved = paymentRepository.save(payment);

        // Step 3: Generate bill and update reservation
        Map<String, Long> map = new HashMap<>();
        map.put("reservationId", reservationId);
        BillDTO bill = billingClient.generateBill(map);

        return Map.of(
            "message", "Razorpay order created. Proceed to pay using Razorpay UI",
            "orderId", order.get("id"),
            "amount", total,
            "currency", order.get("currency"),
            "paymentId", saved.getId(),
            "billNo", bill.getBillNo()
        );
    }

    // Handle payment success callback
    public Map<String, Object> handlePaymentSuccess(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setPaymentSuccess(true); // Mark payment as successful
        paymentRepository.save(payment);

        // Update reservation status to BOOKED
        reservationServiceClient.updateReservationStatus(payment.getReservationId(), true);

        return Map.of("message", "Payment successful", "paymentId", paymentId);
    }

    // Handle payment failure callback
    public Map<String, Object> handlePaymentFailure(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setPaymentSuccess(false); // Mark payment as failed
        paymentRepository.save(payment);

        // Update reservation status to CANCELLED
        reservationServiceClient.updateReservationStatus(payment.getReservationId(), false);

        return Map.of("message", "Payment failed, reservation cancelled", "paymentId", paymentId);
    }
}
