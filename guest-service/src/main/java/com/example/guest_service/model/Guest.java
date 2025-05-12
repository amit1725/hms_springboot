package com.example.guest_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="guests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false,unique = true)
	private String memberCode;
	
	private String name;
	private String phoneNo;
	private String company;
	private String email;
	private String gender;
	private String address;
	
}
