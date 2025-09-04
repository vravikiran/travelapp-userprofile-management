package com.localapp.mgmt.userprofile.services;

import com.localapp.mgmt.userprofile.entities.Role;
import com.localapp.mgmt.userprofile.repostories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public void createRole(Role role) {
        roleRepository.save(role);
    }
}
