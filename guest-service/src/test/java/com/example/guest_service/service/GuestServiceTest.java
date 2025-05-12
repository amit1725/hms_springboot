package com.example.guest_service.service;

import com.example.guest_service.exception.GuestNotFoundException;
import com.example.guest_service.model.Guest;
import com.example.guest_service.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;
    
    @InjectMocks
    private GuestService guestService;

    private Guest guest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        guest = new Guest();
        guest.setId(1L);
        guest.setName("Amit");
        guest.setPhoneNo("1234567890");
        guest.setCompany("ABC");
        guest.setEmail("amit@gmail.com");
        guest.setGender("Male");
        guest.setAddress("Pune");
    }

    @Test
    void testAddGuest() {
        when(guestRepository.save(guest)).thenReturn(guest);

        Guest savedGuest = guestService.addGuest(guest);

        assertEquals(guest, savedGuest);
        verify(guestRepository, times(1)).save(guest);
    }

    @Test
    void testUpdateGuest_Success() throws GuestNotFoundException {
        Guest updatedGuest = new Guest();
        updatedGuest.setName("Shubham");
        updatedGuest.setPhoneNo("9876543210");
        updatedGuest.setCompany("ABC");
        updatedGuest.setEmail("shubham@gmail.com");
        updatedGuest.setGender("Male");
        updatedGuest.setAddress("Mumbai");

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenReturn(updatedGuest);

        Guest result = guestService.updateGuest(1L, updatedGuest);

        assertEquals("Shubham", result.getName());
        assertEquals("9876543210", result.getPhoneNo());
        verify(guestRepository).save(any(Guest.class));
    }

    @Test
    void testUpdateGuest_NotFound() {
        Guest updatedGuest = new Guest();

        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GuestNotFoundException.class, () -> guestService.updateGuest(1L, updatedGuest));
    }

    @Test
    void testDeleteGuest_Success() throws GuestNotFoundException {
        when(guestRepository.existsById(1L)).thenReturn(true);

        guestService.deleteGuest(1L);

        verify(guestRepository).deleteById(1L);
    }

    @Test
    void testDeleteGuest_NotFound() {
        when(guestRepository.existsById(1L)).thenReturn(false);

        assertThrows(GuestNotFoundException.class, () -> guestService.deleteGuest(1L));
    }

    @Test
    void testGetGuestById_Success() throws GuestNotFoundException {
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        Guest result = guestService.getGuestById(1L);

        assertEquals(guest, result);
    }

    @Test
    void testGetGuestById_NotFound() {
        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GuestNotFoundException.class, () -> guestService.getGuestById(1L));
    }

    @Test
    void testGetAllGuests() {
        Guest anotherGuest = new Guest();
        anotherGuest.setId(2L);
        anotherGuest.setName("Alice");

        when(guestRepository.findAll()).thenReturn(Arrays.asList(guest, anotherGuest));

        List<Guest> guests = guestService.getAllGuests();

        assertEquals(2, guests.size());
        verify(guestRepository, times(1)).findAll();
    }
}
