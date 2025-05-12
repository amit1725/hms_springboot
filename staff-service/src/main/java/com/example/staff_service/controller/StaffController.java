package com.example.staff_service.controller;

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

import com.example.staff_service.model.Staff;
import com.example.staff_service.model.exception.StaffNotFoundException;
import com.example.staff_service.service.StaffService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
	
	@Autowired
	private StaffService staffService;
	
	@PostMapping
	public ResponseEntity<Staff> addStaff(@RequestBody Staff staff){
		return new ResponseEntity<>(staffService.addStaff(staff),HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Staff> updateStaff(@PathVariable Long id,@RequestBody Staff staff) throws StaffNotFoundException{
		return new ResponseEntity<>(staffService.updateStaff(id, staff),HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteStaff(@PathVariable Long id) throws StaffNotFoundException{
		staffService.deleteStaff(id);
		return new ResponseEntity<>("Staff with id "+id+" deleted successfully",HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Staff> getStaffById(@PathVariable Long id) throws StaffNotFoundException {
	     return new ResponseEntity<>(staffService.getStaffById(id),HttpStatus.OK);
	}
	
	@GetMapping
	  public ResponseEntity<List<Staff>> getAllStaff() {
        return new ResponseEntity<>(staffService.getAllStaff(),HttpStatus.OK);
    }
}
