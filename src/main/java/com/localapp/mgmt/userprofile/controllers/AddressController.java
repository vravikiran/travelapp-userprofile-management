package com.localapp.mgmt.userprofile.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.localapp.mgmt.userprofile.entities.Address;
import com.localapp.mgmt.userprofile.services.AddressService;

@RestController
@RequestMapping("/address")
public class AddressController {
	@Autowired
	AddressService addressService;

	@PostMapping
	public ResponseEntity<HttpStatus> addAddressToUserProfile(@RequestParam long mobileno,
			@RequestBody Address address) {
		addressService.addAddressToProfile(mobileno, address);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping
	public ResponseEntity<HttpStatus> updateAddress(@RequestBody Address address) {
		addressService.updateAddress(address);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/id")
	public ResponseEntity<Address> deleteAddressById(int id) {
		addressService.deleteAddressById(id);
		return ResponseEntity.ok().build();
	}
}
