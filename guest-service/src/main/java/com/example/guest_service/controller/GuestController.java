package com.example.guest_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.guest_service.exception.GlobalExceptionHandler;
import com.example.guest_service.exception.GuestNotFoundException;
import com.example.guest_service.model.Guest;
import com.example.guest_service.service.GuestService;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

	@Autowired
	private GuestService guestService;

	@PostMapping
	public ResponseEntity<Guest> addGuest(@RequestBody Guest guest){
		return new ResponseEntity<>(guestService.addGuest(guest),HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Guest> updateGuest(@PathVariable Long id,@RequestBody Guest guest) throws GuestNotFoundException{
		return new ResponseEntity<>(guestService.updateGuest(id, guest),HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteGuest(@PathVariable Long id) throws GuestNotFoundException{
		 guestService.deleteGuest(id);
		 return new ResponseEntity<>("Staff with id: "+id+" deleted successfully",HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Guest> getGuestById(@PathVariable Long id) throws GuestNotFoundException{
		return new ResponseEntity<>(guestService.getGuestById(id),HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<List<Guest>> getAllGuests(){
		return new ResponseEntity<>(guestService.getAllGuests(),HttpStatus.OK);
	}
}
