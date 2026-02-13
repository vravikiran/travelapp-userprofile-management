package com.localapp.mgmt.userprofile.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Address update", description = "Add or update address of user, travel agent")
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    AddressService addressService;

    /**
     * update address of a specific user
     *
     * @param mobileNo - adds address of given user by mobile number
     * @param address  - the address details of user are added
     * @return - status of request
     */
    @Operation(method = "POST", description = "Adds address of user",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "User registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "999999999")})
    @PostMapping
    public ResponseEntity<HttpStatus> addAddressToUserProfile(@RequestParam long mobileNo,
                                                              @RequestBody Address address) {
        addressService.addAddressToProfile(mobileNo, address);
        return ResponseEntity.ok().build();
    }

    /**
     * updates existing address of a user/travel agent
     *
     * @param address - address to be updated
     * @return status of request
     */
    @Operation(method = "PUT", description = "updates existing address of an user/travel agent")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Address updated successfully")})
    @PutMapping
    public ResponseEntity<HttpStatus> updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return ResponseEntity.ok().build();
    }

    /**
     * delete address of  user/travel agent
     *
     * @param id - id of address
     * @return - status of request
     */
    @Operation(method = "DELETE", description = "deletes/deactivates a specific address")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Address deleted successfully")})
    @DeleteMapping("/id")
    public ResponseEntity<HttpStatus> deleteAddressById(int id) {
        addressService.deleteAddressById(id);
        return ResponseEntity.ok().build();
    }
}
