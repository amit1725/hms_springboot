package com.example.staff_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.staff_service.model.Staff;
import com.example.staff_service.model.exception.StaffNotFoundException;
import com.example.staff_service.repository.StaffRepository;

@Service
public class StaffService {
	
	@Autowired
	private StaffRepository staffRepository;
	
	public Staff addStaff(Staff staff) {
		return staffRepository.save(staff);
	}
	
	public Staff updateStaff(Long id,Staff updatedStaff) throws StaffNotFoundException {
		Staff existingStaff=staffRepository.findById(id)
				.orElseThrow(()->new StaffNotFoundException("Staff not found with id: "+id));
		existingStaff.setStaffCode(updatedStaff.getStaffCode());
		existingStaff.setEmployeeName(updatedStaff.getEmployeeName());
		existingStaff.setEmployeeAddress(updatedStaff.getEmployeeAddress());
		existingStaff.setNic(updatedStaff.getNic());
		existingStaff.setSalary(updatedStaff.getSalary());
		existingStaff.setAge(updatedStaff.getAge());
		existingStaff.setOccupation(updatedStaff.getOccupation());
		existingStaff.setEmail(updatedStaff.getEmail());
		
		return staffRepository.save(existingStaff);
	}
	
	public void deleteStaff(Long id) throws StaffNotFoundException {
		if(!staffRepository.existsById(id)) {
			throw new StaffNotFoundException("Staff not found with id: "+id);
		}
		staffRepository.deleteById(id);
	}
	
	public Staff getStaffById(Long id) throws StaffNotFoundException {
		return staffRepository.findById(id)
				.orElseThrow(()->new StaffNotFoundException("Staff not found with id: "+id));
	}
	
	public List<Staff> getAllStaff(){
		return staffRepository.findAll();
	}
}
