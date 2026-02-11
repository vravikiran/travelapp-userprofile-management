package com.localapp.mgmt.userprofile.controllers;

import java.io.UnsupportedEncodingException;

import com.localapp.mgmt.userprofile.dto.AuthRequest;
import com.localapp.mgmt.userprofile.dto.EmailAuthRequest;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.services.MessageService;
import com.localapp.mgmt.userprofile.services.UserProfileService;
import com.nimbusds.jose.JOSEException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.localapp.mgmt.userprofile.services.TokenService;

import jakarta.mail.MessagingException;

@Tag(name = "Authentication", description = "Authenticates and authorises users/travel agents/admin through mobile/email otp")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    MessageService messageService;
    @Autowired
    UserProfileService userProfileService;

    @Operation(method = "GET",
            description = "generates otp and sends it to user registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "User registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer",format = "int64"),
                    example = "9999999999")
            }
    )
    @GetMapping("/mobile/otp")
    public ResponseEntity<String> generateMobileOtp(@RequestParam long mobileNo) {
        messageService.generateMobileOtp(mobileNo);
        return ResponseEntity.ok("otp generated successfully");
    }

    @Operation(method = "GET",
            description = "generates otp and sends it to user registered email",
            parameters = {@Parameter(name = "email",
                    in = ParameterIn.QUERY,
                    description = "User registered email",
                    required = true,
                    schema = @Schema(type = "string",format = "email"),
                    example = "test@email.com")
            }
    )
    @GetMapping("/email/otp")
    public ResponseEntity<String> generateEmailOtp(@RequestParam String email)
            throws UnsupportedEncodingException, MessagingException {
        messageService.generateEmailOtp(email);
        return ResponseEntity.ok("otp sent to email successfully");
    }

    @Operation(method = "POST",
            description = "validates the otp entered by user sent to the mobile number and generates a token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "otp validation successful"),
            @ApiResponse(responseCode = "404", description = "Invalid otp")
    })
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

    @Operation(method = "POST", description = "validates the otp entered by user sent to the email and generates a token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "otp validation successful"),
            @ApiResponse(responseCode = "404", description = "Invalid otp")
    })
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