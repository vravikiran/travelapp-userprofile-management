package com.localapp.mgmt.userprofile.controllers;

import java.io.UnsupportedEncodingException;

import com.localapp.mgmt.userprofile.dto.AuthRequest;
import com.localapp.mgmt.userprofile.dto.EmailAuthRequest;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.services.MessageService;
import com.localapp.mgmt.userprofile.services.UserProfileService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.localapp.mgmt.userprofile.services.TokenService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    MessageService messageService;
    @Autowired
    UserProfileService userProfileService;


    @GetMapping("/mobile/otp")
    public ResponseEntity<String> generateMobileOtp(@RequestParam long mobileNo) {
        messageService.generateMobileOtp(mobileNo);
        return ResponseEntity.ok("otp generated successfully");
    }

    @GetMapping("/email/otp")
    public ResponseEntity<String> generateEmailOtp(@RequestParam String email)
            throws UnsupportedEncodingException, MessagingException {
        messageService.generateEmailOtp(email);
        return ResponseEntity.ok("otp sent to email successfully");
    }

    @PostMapping("/verify/mobile/otp")
    public ResponseEntity<String> validateMobileOtp(@RequestBody AuthRequest authRequest) throws JOSEException, UserNotFoundException {
        boolean isValidOtp = messageService.validateMobileOtp(authRequest);
        if (isValidOtp) {
            UserProfile userProfile = userProfileService.getUserProfile(authRequest.getMobileNo());
            String token = TokenService.generateToken(Long.toString(authRequest.getMobileNo()), userProfile, "mobileNo");
            return ResponseEntity.ok(token);
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid otp");
    }

    @PostMapping("/verify/email/otp")
    public ResponseEntity<String> validateEmailOtp(@RequestBody EmailAuthRequest authRequest) throws JOSEException, UserNotFoundException {
        boolean isValidOtp = messageService.validateEmailOtp(authRequest);
        if (isValidOtp) {
            UserProfile userProfile = userProfileService.getUserProfileByEmail(authRequest.getEmail());
            String token = TokenService.generateToken(authRequest.getEmail(), userProfile, "email");
            return ResponseEntity.ok(token);
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid otp");
    }
}