package com.example.room_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.room_service.exception.RoomNotFoundException;
import com.example.room_service.model.Room;
import com.example.room_service.repository.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ReservationClient reservationClient;

    // CREATE a room
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    // READ all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // READ room by id
    public Room getRoomById(Long id) throws RoomNotFoundException {
        return roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException("Room not found"));
    }

    // UPDATE room details
    public Room updateRoom(Long id, Room roomDetails) throws RoomNotFoundException {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException("Room not found"));
        room.setRoomType(roomDetails.getRoomType());
        room.setPrice(roomDetails.getPrice());
        room.setCapacity(roomDetails.getCapacity());
        return roomRepository.save(room);
    }

    // DELETE a room
    public void deleteRoom(Long id) throws RoomNotFoundException {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException("Room not found"));
        roomRepository.delete(room);
    }
    
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Long> bookedRoomIds = reservationClient.getBookedRoomIds(checkInDate, checkOutDate);
        return roomRepository.findAll()
                .stream()
                .filter(room -> !bookedRoomIds.contains(room.getId()))
                .collect(Collectors.toList());
    }

}
