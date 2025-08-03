package com.localapp.mgmt.userprofile.repostories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localapp.mgmt.userprofile.entities.KycDetails;

public interface KycDetailsRepository extends JpaRepository<KycDetails, Integer> {

}
