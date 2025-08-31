package com.localapp.mgmt.userprofile.controllers;

import java.io.IOException;
import java.util.List;
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

import com.localapp.mgmt.userprofile.entities.AgentService;
import com.localapp.mgmt.userprofile.entities.TravelAgentKycDetails;
import com.localapp.mgmt.userprofile.entities.TravelAgentProfile;
import com.localapp.mgmt.userprofile.exceptions.DuplicateUserException;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.services.FileService;
import com.localapp.mgmt.userprofile.services.TravelAgentProfileService;
import com.localapp.mgmt.userprofile.util.Constants;

@RestController
@RequestMapping("/travel-agent-profile")
public class TravelAgentProfileController {
    @Autowired
    TravelAgentProfileService agentProfileService;
    @Autowired
    FileService fileService;

    @GetMapping
    public ResponseEntity<TravelAgentProfile> getAgentProfile(long mobileNo) throws UserNotFoundException {
        TravelAgentProfile agentProfile = agentProfileService.getTravelAgentProfile(mobileNo);
        return ResponseEntity.ok(agentProfile);
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<String> deActivateAgentProfile(@RequestParam long mobileNo) throws UserNotFoundException {
        agentProfileService.deActivateTravelAgentProfile(mobileNo);
        return ResponseEntity.status(HttpStatus.OK).body("Travel Agent Profile deactivated successfully");
    }

    @PostMapping
    public ResponseEntity<TravelAgentProfile> createTravelAgent(@RequestBody TravelAgentProfile travelAgentProfile)
            throws DuplicateUserException {
        TravelAgentProfile agentProfile = agentProfileService.createTravelAgentProfile(travelAgentProfile);
        return ResponseEntity.ok(agentProfile);
    }

    @PatchMapping
    public ResponseEntity<TravelAgentProfile> updateTravelAgent(@RequestBody Map<String, String> valuesToUpdate,
                                                                @RequestParam long mobileNo) throws NoSuchElementException, UserNotFoundException, DuplicateUserException {
        TravelAgentProfile agentProfile = agentProfileService.updateTravelAgentProfile(valuesToUpdate, mobileNo);
        return ResponseEntity.ok(agentProfile);
    }

    @GetMapping("/email")
    public ResponseEntity<TravelAgentProfile> getAgentProfileByEmail(@RequestParam String email) {
        TravelAgentProfile agentProfile = agentProfileService.getAgentProfileByEmail(email);
        return ResponseEntity.ok(agentProfile);
    }

    @PatchMapping("/services")
    public ResponseEntity<List<AgentService>> updateAgentServices(@RequestBody List<Integer> serviceIds,
                                                                  @RequestParam long mobileNo) throws UserNotFoundException {
        List<AgentService> agentServices = agentProfileService.updateTravelAgentServices(serviceIds, mobileNo);
        return ResponseEntity.ok(agentServices);
    }

    @PatchMapping("/languages")
    public ResponseEntity<List<String>> updateAgentLanguages(@RequestBody List<String> languages,
                                                             @RequestParam long mobileNo) throws UserNotFoundException {
        List<String> updatedLanguages = agentProfileService.updateTravelAgentLanguages(languages, mobileNo);
        return ResponseEntity.ok(updatedLanguages);
    }

    @PostMapping("/kyc")
    public ResponseEntity<HttpStatus> updateKycDetails(@RequestParam long mobileNo,
                                                       @RequestBody TravelAgentKycDetails agentKycDetails) throws UserNotFoundException {
        agentProfileService.updateKycDetails(agentKycDetails, mobileNo);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/kycimage/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAgentKycImage(@RequestPart("photo") MultipartFile file,
                                                      @RequestParam long mobileNo, @RequestParam(defaultValue = "true") boolean isFrontImage)
            throws IOException, UserNotFoundException {
        String url = fileService.uploadTravelAgentKycImage(file, mobileNo, Constants.PROFILE_S3_BUCKET, isFrontImage);
        return ResponseEntity.ok(url);
    }

    @PostMapping(path = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAgentProfileImage(@RequestPart("photo") MultipartFile file,
                                                          @RequestParam long mobileNo, @RequestParam(defaultValue = "true") boolean isFrontImage)
            throws IOException, UserNotFoundException {
        String url = fileService.uploadTravelAgentProfileImage(file, mobileNo, Constants.PROFILE_S3_BUCKET);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TravelAgentProfile>> filterTravelAgents(
            @RequestParam(required = false) List<Integer> serviceIds, @RequestParam String location, @RequestParam(required = false) List<String> languages) {
        List<TravelAgentProfile> agentProfiles = agentProfileService.filterTravelAgents(location, languages, serviceIds);
        return ResponseEntity.ok(agentProfiles);
    }

	/*@GetMapping("/langauges")
	public ResponseEntity<List<TravelAgentProfile>> filterTravelAgentsByLanguage(@RequestParam List<String> languages, @RequestParam String location) {
		List<TravelAgentProfile> agentProfiles = agentProfileService.filterTravelAgentsByLanguages(languages, location.toUpperCase());
		return ResponseEntity.ok(agentProfiles);
	}*/
	
	/*@GetMapping("/location")
	public ResponseEntity<List<TravelAgentProfile>> filterTravelAgentsByLocation(@RequestParam String location) {
		List<TravelAgentProfile> agentProfiles = agentProfileService.filterTravelAgentsByLocation(location);
		return ResponseEntity.ok(agentProfiles);
	}*/
}
