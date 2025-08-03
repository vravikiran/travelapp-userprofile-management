package com.localapp.mgmt.userprofile.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.mgmt.userprofile.entities.Address;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.repostories.AddressRepository;
import com.localapp.mgmt.userprofile.repostories.UserProfileRepository;
import com.localapp.mgmt.userprofile.util.HashGenerator;

@Service
public class AddressService {
	@Autowired
	UserProfileRepository profileRepository;
	@Autowired
	AddressRepository addressRepository;

	public UserProfile addAddressToProfile(long mobileno, Address address) {
		UserProfile profile = profileRepository.getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileno));
		address.setCreated_date(LocalDate.now());
		address.setUpdated_date(LocalDate.now());
		address.setUserProfile(profile);
		addressRepository.save(address);
		return profile;
	}

	public Address updateAddress(Address address) {
		address.setUpdated_date(LocalDate.now());
		return addressRepository.save(address);
	}
	
	public void deleteAddressById(int id) {
		addressRepository.deleteById(id);
	}
}
