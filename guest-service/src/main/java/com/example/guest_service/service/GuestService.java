package com.example.guest_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.guest_service.exception.GuestNotFoundException;
import com.example.guest_service.model.Guest;
import com.example.guest_service.repository.GuestRepository;

@Service
public class GuestService {
	
	@Autowired
	private GuestRepository guestRepository;
	
	public Guest addGuest(Guest guest) {
		return guestRepository.save(guest);
	}
	
	public Guest updateGuest(Long id,Guest updatedGuest) throws GuestNotFoundException {
		Guest existingGuest=guestRepository.findById(id)
				.orElseThrow(()->new GuestNotFoundException("Guest not found with id: "+id));
		existingGuest.setName(updatedGuest.getName());
		existingGuest.setPhoneNo(updatedGuest.getPhoneNo());
		existingGuest.setCompany(updatedGuest.getCompany());
		existingGuest.setEmail(updatedGuest.getEmail());
		existingGuest.setGender(updatedGuest.getGender());
		existingGuest.setAddress(updatedGuest.getAddress());
		return guestRepository.save(existingGuest);
	}
	
	public void deleteGuest(Long id) throws GuestNotFoundException {
		if(!guestRepository.existsById(id)) {
			throw new GuestNotFoundException("Guest not found with id: "+id);
		}
		guestRepository.deleteById(id);
	}
	
	public Guest getGuestById(Long id) throws GuestNotFoundException {
		return guestRepository.findById(id)
				.orElseThrow(()->new GuestNotFoundException("Guest not found with id: "+id));
	}
	
	public List<Guest> getAllGuests(){
		return guestRepository.findAll();
	}
}
