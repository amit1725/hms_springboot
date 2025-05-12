package com.example.room_service.service;

import com.example.room_service.exception.RoomNotFoundException;
import com.example.room_service.model.Room;
import com.example.room_service.model.RoomType;
import com.example.room_service.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes @Mock and @InjectMocks
    }

    private Room sampleRoom(Long id) {
        Room room = new Room();
        room.setId(id);
        room.setRoomType(RoomType.DELUXE);
        room.setPrice(1500.0);
        room.setCapacity(2);
        return room;
    }

    @Test
    void testCreateRoom() {
        Room room = sampleRoom(null);
        when(roomRepository.save(room)).thenReturn(room);

        Room result = roomService.createRoom(room);

        assertEquals(room, result);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testGetRoomById_found() throws RoomNotFoundException {
        Room room = sampleRoom(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room result = roomService.getRoomById(1L);

        assertEquals(1L, result.getId());
        verify(roomRepository).findById(1L);
    }

    @Test
    void testGetRoomById_notFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(1L));
    }

    @Test
    void testUpdateRoom() throws RoomNotFoundException {
        Room existing = sampleRoom(1L);
        Room updated = new Room();
        updated.setRoomType(RoomType.SUITE);
        updated.setPrice(2000.0);
        updated.setCapacity(3);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any(Room.class))).thenReturn(existing);

        Room result = roomService.updateRoom(1L, updated);

        assertEquals(RoomType.SUITE, result.getRoomType());
        assertEquals(2000.0, result.getPrice());
        assertEquals(3, result.getCapacity());
    }

    @Test
    void testDeleteRoom_success() throws RoomNotFoundException {
        Room room = sampleRoom(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        verify(roomRepository).delete(room);
    }

    @Test
    void testGetAvailableRooms() {
        Room r1 = sampleRoom(1L);
        Room r2 = sampleRoom(2L);

        when(roomRepository.findAll()).thenReturn(List.of(r1, r2));
        when(reservationClient.getBookedRoomIds(any(), any())).thenReturn(List.of(1L));

        List<Room> available = roomService.getAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(2));

        assertEquals(1, available.size());
        assertEquals(2L, available.get(0).getId());
    }
}
