package com.example.staff_service.service;

import com.example.staff_service.model.Staff;
import com.example.staff_service.model.exception.StaffNotFoundException;
import com.example.staff_service.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    private Staff sampleStaff;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleStaff = new Staff();
        sampleStaff.setId(1L);
        sampleStaff.setStaffCode("STF001");
        sampleStaff.setEmployeeName("Ishaan");
        sampleStaff.setEmployeeAddress("Mumbai");
        sampleStaff.setNic("NIC123");
        sampleStaff.setSalary(50000.0);
        sampleStaff.setAge(30);
        sampleStaff.setOccupation("Waiter");
        sampleStaff.setEmail("ishaan@gmail.com");
    }

    @Test
    void testAddStaff() {
        when(staffRepository.save(sampleStaff)).thenReturn(sampleStaff);
        Staff result = staffService.addStaff(sampleStaff);
        assertEquals(sampleStaff, result);
        verify(staffRepository, times(1)).save(sampleStaff);
    }

    @Test
    void testUpdateStaff_Success() throws StaffNotFoundException {
        Staff updated = new Staff();
        updated.setStaffCode("STF002");
        updated.setEmployeeName("Ishaan");
        updated.setEmployeeAddress("Pune");
        updated.setNic("NIC002");
        updated.setSalary(60000.0);
        updated.setAge(31);
        updated.setOccupation("Manager");
        updated.setEmail("ishaan@gmail.com");

        when(staffRepository.findById(1L)).thenReturn(Optional.of(sampleStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(updated);

        Staff result = staffService.updateStaff(1L, updated);
        assertEquals("STF002", result.getStaffCode());
        assertEquals("Ishaan", result.getEmployeeName());
    }

    @Test
    void testUpdateStaff_ThrowsException() {
        when(staffRepository.findById(1L)).thenReturn(Optional.empty());
        Staff updated = new Staff();

        assertThrows(StaffNotFoundException.class, () -> staffService.updateStaff(1L, updated));
    }

    @Test
    void testDeleteStaff_Success() throws StaffNotFoundException {
        when(staffRepository.existsById(1L)).thenReturn(true);
        staffService.deleteStaff(1L);
        verify(staffRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteStaff_ThrowsException() {
        when(staffRepository.existsById(1L)).thenReturn(false);
        assertThrows(StaffNotFoundException.class, () -> staffService.deleteStaff(1L));
    }

    @Test
    void testGetStaffById_Success() throws StaffNotFoundException {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(sampleStaff));
        Staff result = staffService.getStaffById(1L);
        assertEquals(sampleStaff, result);
    }

    @Test
    void testGetStaffById_ThrowsException() {
        when(staffRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StaffNotFoundException.class, () -> staffService.getStaffById(1L));
    }

    @Test
    void testGetAllStaff() {
        List<Staff> staffList = Arrays.asList(sampleStaff, new Staff());
        when(staffRepository.findAll()).thenReturn(staffList);
        List<Staff> result = staffService.getAllStaff();
        assertEquals(2, result.size());
    }
}
