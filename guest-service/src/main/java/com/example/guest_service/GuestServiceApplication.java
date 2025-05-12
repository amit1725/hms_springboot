package com.example.guest_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GuestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuestServiceApplication.class, args);
	}

}
