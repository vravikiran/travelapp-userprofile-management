package com.localapp.mgmt.userprofile.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import com.localapp.mgmt.userprofile.entities.AgentService;
import com.localapp.mgmt.userprofile.entities.TravelAgentKycDetails;
import com.localapp.mgmt.userprofile.entities.TravelAgentProfile;
import com.localapp.mgmt.userprofile.exceptions.DuplicateUserException;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.services.FileService;
import com.localapp.mgmt.userprofile.services.TravelAgentProfileService;
import com.localapp.mgmt.userprofile.util.Constants;

@Tag(name = "Travel Agent Profile Management", description = "creates and manages profiles of travel agents")
@RestController
@RequestMapping("/travel-agent-profile")
public class TravelAgentProfileController {
    @Autowired
    TravelAgentProfileService agentProfileService;
    @Autowired
    FileService fileService;

    /**
     * fetches travel agent profile based on the registered mobile number
     *
     * @param mobileNo - mobile number of registered travel agent
     * @return - TravelAgentProfile
     * @throws UserNotFoundException - exception thrown when user with mobile number not found
     */
    @Operation(method = "GET",
            description = "Fetches the profile of travel agent based on their registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "Travel Agent registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel Agent Profile fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Travel Agent Profile not found")
    })
    @GetMapping
    public ResponseEntity<TravelAgentProfile> getAgentProfile(long mobileNo) throws UserNotFoundException {
        TravelAgentProfile agentProfile = agentProfileService.getTravelAgentProfile(mobileNo);
        return ResponseEntity.ok(agentProfile);
    }

    /**
     * deactivates travel agent based on the registered mobile number
     *
     * @param mobileNo - mobile number of registered travel agent
     * @return success message
     * @throws UserNotFoundException - throws exception when travel agent profile not found with given mobile number
     */
    @Operation(
            method = "PUT",
            description = "Deactivates travel agent profile based on their registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "Travel Agent registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel Agent Profile deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Travel Agent Profile not found")
    })
    @PatchMapping("/deactivate")
    public ResponseEntity<String> deActivateAgentProfile(@RequestParam long mobileNo) throws UserNotFoundException {
        agentProfileService.deActivateTravelAgentProfile(mobileNo);
        return ResponseEntity.status(HttpStatus.OK).body("Travel Agent Profile deactivated successfully");
    }

    /**
     * creates travel agent profile
     *
     * @param travelAgentProfile - details of travel agent
     * @return - created travel agent profile
     * @throws DuplicateUserException - throws exception when profile exists with given details
     */
    @Operation(
            method = "POST",
            description = "creates travel agent profile",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Travel Agent payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TravelAgentProfile.class)
                    )
            ))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel Agent Profile created successfully"),
            @ApiResponse(responseCode = "409", description = "Travel Agent Profile exists with given details")
    })
    @PostMapping
    public ResponseEntity<TravelAgentProfile> createTravelAgent(@RequestBody TravelAgentProfile travelAgentProfile)
            throws DuplicateUserException {
        TravelAgentProfile agentProfile = agentProfileService.createTravelAgentProfile(travelAgentProfile);
        return ResponseEntity.ok(agentProfile);
    }

    /**
     * updates travel agent profile
     *
     * @param valuesToUpdate - the details which are to be updated
     * @param mobileNo       - registered mobile number of travel agent
     * @return updated TravelAgent Profile
     * @throws NoSuchElementException - data is irrelevant to travel agent
     * @throws UserNotFoundException  - throws exception when no travel agent found with given mobile number
     * @throws DuplicateUserException - throws exception if any travel agent exists with given details
     */
    @Operation(method = "PATCH",
            description = "updates details of travel agent",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "Travel Agent registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel Agent Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Travel Agent Profile not found"),
            @ApiResponse(responseCode = "400", description = "data is irrelevant to travel agent"),
            @ApiResponse(responseCode = "409", description = "Travel Agent Profile exists with given details")
    })
    @PatchMapping
    public ResponseEntity<TravelAgentProfile> updateTravelAgent(@RequestBody Map<String, String> valuesToUpdate,
                                                                @RequestParam long mobileNo) throws NoSuchElementException, UserNotFoundException, DuplicateUserException {
        TravelAgentProfile agentProfile = agentProfileService.updateTravelAgentProfile(valuesToUpdate, mobileNo);
        return ResponseEntity.ok(agentProfile);
    }

    /**
     * fetches travel agent profile based on given email
     *
     * @param email - email of registered travel agent
     * @return - TravelAgent Profile
     */
    @Operation(method = "GET",
            description = "fetches travel agent profile based on given email",
            parameters = {@Parameter(name = "email",
                    in = ParameterIn.QUERY,
                    description = "travel agent registered email",
                    required = true,
                    schema = @Schema(type = "string", format = "email"),
                    example = "test@email.com")
            }
    )
    @GetMapping("/email")
    public ResponseEntity<TravelAgentProfile> getAgentProfileByEmail(@RequestParam String email) {
        TravelAgentProfile agentProfile = agentProfileService.getAgentProfileByEmail(email);
        return ResponseEntity.ok(agentProfile);
    }

    /**
     * updates the services provided by travel agent
     *
     * @param serviceIds - list of services
     * @param mobileNo   - travel agent registered mobile number
     * @return list of updated services of travel agent
     * @throws UserNotFoundException - throws exception when no travel agent found with given mobile number
     */
    @Operation(method = "PATCH",
            description = "updates the services provided by travel agent",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Travel Agent services payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class)
                    )
            ),
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "Travel Agent registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel Agent services updated successfully"),
            @ApiResponse(responseCode = "404", description = "Travel Agent Profile not found")
    })
    @PatchMapping("/services")
    public ResponseEntity<List<AgentService>> updateAgentServices(@RequestBody List<Integer> serviceIds,
                                                                  @RequestParam long mobileNo) throws UserNotFoundException {
        List<AgentService> agentServices = agentProfileService.updateTravelAgentServices(serviceIds, mobileNo);
        return ResponseEntity.ok(agentServices);
    }

    /**
     * updates the languages travel agent can speak
     *
     * @param languages - list of languages travel agent can speak
     * @param mobileNo  - travel agent registered mobile number
     * @return list of updated languages by travel agent
     * @throws UserNotFoundException - throws exception when no travel agent found with given mobile number
     */
    @Operation(method = "PATCH",
            description = "updates the languages provided by travel agent",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Travel Agent languages payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class)
                    )
            ),
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "Travel Agent registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel Agent languages updated successfully"),
            @ApiResponse(responseCode = "404", description = "Travel Agent Profile not found")
    })
    @PatchMapping("/languages")
    public ResponseEntity<List<String>> updateAgentLanguages(@RequestBody List<String> languages,
                                                             @RequestParam long mobileNo) throws UserNotFoundException {
        List<String> updatedLanguages = agentProfileService.updateTravelAgentLanguages(languages, mobileNo);
        return ResponseEntity.ok(updatedLanguages);
    }

    /**
     * updates kyc details of travel agent
     *
     * @param mobileNo        - mobile number of registered travel agent
     * @param agentKycDetails - kyc details
     * @return status of request
     * @throws UserNotFoundException - throws exception when no travel agent found with given mobile number
     */
    @Operation(method = "POST",
            description = "updates kyc details of travel agent",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Travel Agent kyc details payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TravelAgentKycDetails.class)
                    )
            ),
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "Travel Agent registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}

    )
    @PostMapping("/kyc")
    public ResponseEntity<HttpStatus> updateKycDetails(@RequestParam long mobileNo,
                                                       @RequestBody TravelAgentKycDetails agentKycDetails) throws UserNotFoundException {
        agentProfileService.updateKycDetails(agentKycDetails, mobileNo);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param file         - kyc image file uploaded by travel agent
     * @param mobileNo     - mobile number of registered travel agent
     * @param isFrontImage - to indicate front image or back image
     * @return - status of request
     * @throws IOException           - exception occurred while uploading kyc image
     * @throws UserNotFoundException - throws exception when no travel agent found with given mobile number
     */
    @Operation(
            summary = "Upload Agent KYC Image",
            description = "Uploads front or back KYC image for the given agent mobile number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(
                            type = "object",
                            requiredProperties = {"photo", "mobileNo"},
                            properties = {
                                    @StringToClassMapItem(
                                            key = "photo",
                                            value = MultipartFile.class
                                    ),
                                    @StringToClassMapItem(
                                            key = "mobileNo",
                                            value = Long.class
                                    ),
                                    @StringToClassMapItem(
                                            key = "isFrontImage",
                                            value = Boolean.class
                                    )
                            }
                    )
            )
    )
    @PostMapping(path = "/kycimage/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAgentKycImage(@RequestPart("photo") MultipartFile file,
                                                      @RequestParam long mobileNo, @RequestParam(defaultValue = "true") boolean isFrontImage)
            throws IOException, UserNotFoundException {
        String url = fileService.uploadTravelAgentKycImage(file, mobileNo, Constants.PROFILE_S3_BUCKET, isFrontImage);
        return ResponseEntity.ok(url);
    }

    /**
     *
     * @param file     - image file uploaded by travel agent
     * @param mobileNo - mobile number of registered travel agent
     * @return status of request
     * @throws IOException           - exception occurred while uploading kyc image
     * @throws UserNotFoundException - throws exception when no travel agent found with given mobile number
     */
    @Operation(
            summary = "Upload Agent profile Image",
            description = "Uploads image of travel agent with the registered mobile number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(
                            type = "object",
                            requiredProperties = {"photo", "mobileNo"},
                            properties = {
                                    @StringToClassMapItem(
                                            key = "photo",
                                            value = MultipartFile.class
                                    ),
                                    @StringToClassMapItem(
                                            key = "mobileNo",
                                            value = Long.class
                                    )
                            }
                    )
            )
    )
    @PostMapping(path = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAgentProfileImage(@RequestPart("photo") MultipartFile file,
                                                          @RequestParam long mobileNo)
            throws IOException, UserNotFoundException {
        String url = fileService.uploadTravelAgentProfileImage(file, mobileNo, Constants.PROFILE_S3_BUCKET);
        return ResponseEntity.ok(url);
    }

    /**
     * fetches the list of available travel agents based on the location, services and languages selected by user
     *
     * @param serviceIds - list of selected services by user
     * @param location   - the location where user plans travel
     * @param languages  - list of preferred languages by user
     * @return - list of available travel agents based on selected filters
     */
    @Operation(
            description = "Fetches the list of available travel agents based on location, services and languages selected by user",
            parameters = {

                    @Parameter(
                            name = "serviceIds",
                            in = ParameterIn.QUERY,
                            description = "List of required service ids",
                            required = false,
                            array = @ArraySchema(
                                    schema = @Schema(type = "integer", format = "int32", example = "1")
                            )
                    ),

                    @Parameter(
                            name = "location",
                            in = ParameterIn.QUERY,
                            description = "Travel location selected by user",
                            required = true,
                            schema = @Schema(type = "string", example = "HYDERABAD")
                    ),

                    @Parameter(
                            name = "languages",
                            in = ParameterIn.QUERY,
                            description = "List of preferred languages",
                            required = false,
                            array = @ArraySchema(
                                    schema = @Schema(type = "string", example = "ENGLISH")
                            )
                    )
            }
    )
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
