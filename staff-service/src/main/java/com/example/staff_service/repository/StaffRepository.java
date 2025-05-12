package com.example.staff_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.staff_service.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long>{
	Optional<Staff> findByStaffCode(String staffCode);
}
