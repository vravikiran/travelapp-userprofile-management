package com.localapp.mgmt.userprofile.controllers;

import com.localapp.mgmt.userprofile.entities.Role;
import com.localapp.mgmt.userprofile.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Role creation", description = "Add new roles to access the application")
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    /**
     * creates a new role
     *
     * @param role - details of role to be created
     * @return - newly created role
     */
    @Operation(method = "POST", description = "creates new role, only admin can create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Unauthorized access"),
            @ApiResponse(responseCode = "200", description = "Role creation successful")
    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<HttpStatus> createRole(@RequestBody Role role) {
        roleService.createRole(role);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
