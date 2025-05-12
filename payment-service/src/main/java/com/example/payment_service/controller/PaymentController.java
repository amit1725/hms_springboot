package com.example.payment_service.controller;

import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
    private  PaymentService paymentService;

    // Endpoint to handle payment
    @PostMapping("/create/{reservationId}")
    public Map<String, Object> handlePayment(@PathVariable Long reservationId) {
        return paymentService.handlePayment(reservationId);
    }

    // Endpoint to handle payment success (callback)
    @PostMapping("/payment-success/{paymentId}")
    public Map<String, Object> paymentSuccess(@PathVariable Long paymentId) {
        return paymentService.handlePaymentSuccess(paymentId);
    }

    // Endpoint to handle payment failure (callback)
    @PostMapping("/payment-failure/{paymentId}")
    public Map<String, Object> paymentFailure(@PathVariable Long paymentId) {
        return paymentService.handlePaymentFailure(paymentId);
    }
}
