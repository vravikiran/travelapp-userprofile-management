package com.localapp.mgmt.userprofile.controllers;


import com.localapp.mgmt.userprofile.services.TokenService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/key")
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @Hidden
    // API to fetch public key to other microservices or lambda functions
    @GetMapping("/public")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(tokenService.getJWKS());
    }
}