package com.localapp.mgmt.userprofile.repostories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localapp.mgmt.userprofile.entities.AgentService;

public interface ServiceRepository extends JpaRepository<AgentService, Integer> {

}
