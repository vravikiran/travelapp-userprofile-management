package com.localapp.mgmt.userprofile.repostories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localapp.mgmt.userprofile.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{
}
