package com.localapp.mgmt.userprofile.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

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

@RestController
@RequestMapping("/userprofile")
public class UserProfileController {
	@Autowired
	UserProfileService userProfileService;
	@Autowired
	FileService fileService;

	@PostMapping
	public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfile userProfile)
			throws DuplicateUserException {
		UserProfile createdUserProfile = userProfileService.createUserProfile(userProfile);
		return ResponseEntity.ok(createdUserProfile);
	}

	@GetMapping
	public ResponseEntity<UserProfile> getUserProfile(@RequestParam long mobileno) throws UserNotFoundException {
		UserProfile userProfile = userProfileService.getUserProfile(mobileno);
		return ResponseEntity.ok(userProfile);
	}

	@GetMapping("/email")
	public ResponseEntity<UserProfile> getUserProfileByEmail(@RequestParam String email) throws UserNotFoundException {
		UserProfile userProfile = userProfileService.getUserProfileByEmail(email);
		return ResponseEntity.ok(userProfile);
	}

	@PatchMapping("/deactivate")
	public ResponseEntity<HttpStatus> deActivateUser(@RequestParam long mobileno) throws UserNotFoundException {
		userProfileService.deActivateUser(mobileno);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/update")
	public ResponseEntity<UserProfile> updateUser(@RequestParam long mobileno,
			@RequestBody Map<String, String> valuesToUpdate)
			throws NoSuchElementException, DuplicateUserException, UserNotFoundException {
		UserProfile userProfile = userProfileService.updateUserDetails(mobileno, valuesToUpdate);
		return ResponseEntity.ok(userProfile);
	}

	@PostMapping("/preferences")
	public ResponseEntity<UserProfile> updateUserPreferences(@RequestBody UserPreferences preferences,
			@RequestParam long mobileno) throws UserNotFoundException {
		UserProfile updtUsrProfile = userProfileService.updateUserPreferences(preferences, mobileno);
		return ResponseEntity.ok(updtUsrProfile);
	}

	@GetMapping("/preferences")
	public ResponseEntity<UserPreferences> getUserPreferences(@RequestParam long mobileno)
			throws UserNotFoundException {
		UserPreferences preferences = userProfileService.getUserPreferences(mobileno);
		return ResponseEntity.ok(preferences);
	}

	@PostMapping("/kycdetails")
	public ResponseEntity<HttpStatus> updateKycDetails(@RequestParam long mobileno, @RequestBody KycDetails kycDetails)
			throws UserNotFoundException {
		userProfileService.updateKycDetails(kycDetails, mobileno);
		return ResponseEntity.ok().build();
	}

	@PostMapping(path = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadProfileImage(@RequestPart("photo") MultipartFile file,
			@RequestParam long mobileno) throws IOException, UserNotFoundException {
		String url = fileService.uploadUserProfileImage(file, mobileno, Constants.PROFILE_S3_BUCKET);
		return ResponseEntity.ok(url);
	}

	@PostMapping(path = "/kycimage/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadCustomerKycImage(@RequestPart("photo") MultipartFile file,
			@RequestParam long mobileno, @RequestParam(defaultValue = "true") boolean isFrontImage)
			throws IOException, UserNotFoundException {
		String url = fileService.uploadUserKycImage(file, mobileno, Constants.PROFILE_S3_BUCKET, isFrontImage);
		return ResponseEntity.ok(url);
	}
}
