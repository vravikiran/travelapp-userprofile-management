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

    public void addAddressToProfile(long mobile_no, Address address) {
        UserProfile profile = profileRepository.getReferenceById(HashGenerator.generateHashValueForMobileNo(mobile_no));
        address.setCreated_date(LocalDate.now());
        address.setUpdated_date(LocalDate.now());
        address.setUserProfile(profile);
        addressRepository.save(address);
    }

    public void updateAddress(Address address) {
        address.setUpdated_date(LocalDate.now());
        addressRepository.save(address);
    }

    public void deleteAddressById(int id) {
        addressRepository.deleteById(id);
    }
}
