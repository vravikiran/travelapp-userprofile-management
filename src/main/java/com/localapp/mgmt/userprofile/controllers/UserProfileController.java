package com.localapp.mgmt.userprofile.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.localapp.mgmt.userprofile.entities.KycDetails;
import com.localapp.mgmt.userprofile.entities.UserPreferences;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.exceptions.DuplicateUserException;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.services.FileService;
import com.localapp.mgmt.userprofile.services.UserProfileService;
import com.localapp.mgmt.userprofile.util.Constants;

@Tag(name = "User profile management", description = "creates and manages profiles of users,admin")
@RestController
@RequestMapping("/userprofile")
public class UserProfileController {
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    FileService fileService;

    @Operation(method = "POST", description = "creates user profile", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User creation payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserProfile.class)
            )
    ))
    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfile userProfile)
            throws DuplicateUserException {
        UserProfile createdUserProfile = userProfileService.createUserProfile(userProfile);
        return ResponseEntity.ok(createdUserProfile);
    }

    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@RequestParam long mobileNo) throws UserNotFoundException {
        UserProfile userProfile = userProfileService.getUserProfile(mobileNo);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/email")
    public ResponseEntity<UserProfile> getUserProfileByEmail(@RequestParam String email) throws UserNotFoundException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(email);
        return ResponseEntity.ok(userProfile);
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<HttpStatus> deActivateUser(@RequestParam long mobileNo) throws UserNotFoundException {
        userProfileService.deActivateUser(mobileNo);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<UserProfile> updateUser(@RequestParam long mobileNo,
                                                  @RequestBody Map<String, String> valuesToUpdate)
            throws NoSuchElementException, DuplicateUserException, UserNotFoundException {
        UserProfile userProfile = userProfileService.updateUserDetails(mobileNo, valuesToUpdate);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/preferences")
    public ResponseEntity<UserProfile> updateUserPreferences(@RequestBody UserPreferences preferences,
                                                             @RequestParam long mobileNo) throws UserNotFoundException {
        UserProfile updateUsrProfile = userProfileService.updateUserPreferences(preferences, mobileNo);
        return ResponseEntity.ok(updateUsrProfile);
    }

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferences> getUserPreferences(@RequestParam long mobileNo)
            throws UserNotFoundException {
        UserPreferences preferences = userProfileService.getUserPreferences(mobileNo);
        return ResponseEntity.ok(preferences);
    }

    @PostMapping("/kyc")
    public ResponseEntity<HttpStatus> updateKycDetails(@RequestParam long mobileNo, @RequestBody KycDetails kycDetails)
            throws UserNotFoundException {
        userProfileService.updateKycDetails(kycDetails, mobileNo);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfileImage(@RequestPart("photo") MultipartFile file,
                                                     @RequestParam long mobileNo) throws IOException, UserNotFoundException {
        String url = fileService.uploadUserProfileImage(file, mobileNo, Constants.PROFILE_S3_BUCKET);
        return ResponseEntity.ok(url);
    }

    @PostMapping(path = "/kyc/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCustomerKycImage(@RequestPart("photo") MultipartFile file,
                                                         @RequestParam long mobileNo, @RequestParam(defaultValue = "true") boolean isFrontImage)
            throws IOException, UserNotFoundException {
        String url = fileService.uploadUserKycImage(file, mobileNo, Constants.PROFILE_S3_BUCKET, isFrontImage);
        return ResponseEntity.ok(url);
    }
}
