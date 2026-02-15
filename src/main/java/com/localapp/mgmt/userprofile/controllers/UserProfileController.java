package com.localapp.mgmt.userprofile.controllers;

import java.io.IOException;

import com.localapp.mgmt.userprofile.dto.UserPreferencesDto;
import com.localapp.mgmt.userprofile.dto.UserProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    /**
     * creates user profile
     *
     * @param userProfileDto - details of user
     * @return created user profile
     * @throws DuplicateUserException - exception thrown when user with given details already exists
     */
    @Operation(method = "POST", description = "creates user profile", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User creation payload",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileDto.class)
            )
    ))
    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfileDto userProfileDto)
            throws DuplicateUserException {
        UserProfile createdUserProfile = userProfileService.createUserProfile(userProfileDto);
        return ResponseEntity.ok(createdUserProfile);
    }

    /**
     *
     * @param mobileNo - mobile number of registered user
     * @return - UserProfile
     * @throws UserNotFoundException - exception thrown when user with mobile number not found
     */
    @Operation(method = "GET",
            description = "Fetches the user profile based on their registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "User registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User Profile fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User Profile not found")
    })
    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@RequestParam long mobileNo) throws UserNotFoundException {
        UserProfile userProfile = userProfileService.getUserProfile(mobileNo);
        return ResponseEntity.ok(userProfile);
    }

    /**
     *
     * @param email - email of registered user
     * @return - user profile
     * @throws UserNotFoundException - exception thrown when user with email not found
     */
    @Operation(method = "GET",
            description = "fetches user profile based on given email",
            parameters = {@Parameter(name = "email",
                    in = ParameterIn.QUERY,
                    description = "user registered email",
                    required = true,
                    schema = @Schema(type = "string", format = "email"),
                    example = "test@email.com")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User Profile fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User Profile not found")
    })
    @GetMapping("/email")
    public ResponseEntity<UserProfile> getUserProfileByEmail(@RequestParam @Validated @Email String email) throws UserNotFoundException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(email);
        return ResponseEntity.ok(userProfile);
    }

    /**
     * deactivates user profile based on user registered mobile number
     *
     * @param mobileNo - mobile number of user
     * @return - status of request
     * @throws UserNotFoundException - exception thrown when user with mobile number not found
     */
    @Operation(method = "PATCH",
            description = "Deactivates the user profile based on their registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "User registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User Profile deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User Profile not found")
    })
    @PatchMapping("/deactivate")
    public ResponseEntity<HttpStatus> deActivateUser(@RequestParam long mobileNo) throws UserNotFoundException {
        userProfileService.deActivateUser(mobileNo);
        return ResponseEntity.ok().build();
    }

    /**
     * updates user preferences of a user based on their registered mobile number
     *
     * @param userPreferencesDto - language,cuisines and destination type preferences of user
     * @param mobileNo - registered mobile number of user
     * @return - updated userprofile
     * @throws UserNotFoundException - exception thrown when user with mobile number not found
     */
    @Operation(
            method = "POST",
            description = "updates user preferences",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User preferences payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserPreferencesDto.class)
                    )
            ))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User preferences updated successfully"),
            @ApiResponse(responseCode = "404", description = "User Profile not found")
    })
    @PostMapping("/preferences")
    public ResponseEntity<UserProfile> updateUserPreferences(@RequestBody UserPreferencesDto userPreferencesDto,
                                                             @RequestParam long mobileNo) throws UserNotFoundException {
        UserProfile updateUsrProfile = userProfileService.updateUserPreferences(userPreferencesDto, mobileNo);
        return ResponseEntity.ok(updateUsrProfile);
    }

    /**
     * fetches the user preferences based on their registered mobile number
     *
     * @param mobileNo - mobile number of registered user
     * @return - User preferences
     * @throws UserNotFoundException - exception thrown when user with mobile number not found
     */
    @Operation(method = "GET",
            description = "fetches the user preferences based on their registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "User registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User preferences fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User Profile not found")
    })
    @GetMapping("/preferences")
    public ResponseEntity<UserPreferences> getUserPreferences(@RequestParam long mobileNo)
            throws UserNotFoundException {
        UserPreferences preferences = userProfileService.getUserPreferences(mobileNo);
        return ResponseEntity.ok(preferences);
    }

    /**
     * updates kyc details of user
     *
     * @param mobileNo - mobile number of registered user
     * @param kycDetails - kyc details of user
     * @return - status of request
     * @throws UserNotFoundException - exception thrown when user with mobile number not found
     */
    @Operation(method = "POST",
            description = "updates kyc details of  the user based on their registered mobile number",
            parameters = {@Parameter(name = "mobileNo",
                    in = ParameterIn.QUERY,
                    description = "User registered mobile number",
                    required = true,
                    schema = @Schema(type = "integer", format = "int64"),
                    example = "9999999999")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = KycDetails.class)
                    )
            ))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "kyc details updated successfully"),
            @ApiResponse(responseCode = "404", description = "User Profile not found")
    })
    @PostMapping("/kyc")
    public ResponseEntity<HttpStatus> updateKycDetails(@RequestParam long mobileNo, @RequestBody KycDetails kycDetails)
            throws UserNotFoundException {
        userProfileService.updateKycDetails(kycDetails, mobileNo);
        return ResponseEntity.ok().build();
    }

    /**
     *
     * @param file - image file uploaded by user
     * @param mobileNo - mobile number of registered user
     * @return status of request
     * @throws IOException - exception occurred while uploading kyc image
     * @throws UserNotFoundException - throws exception when no user found with given mobile number
     */
    @Operation(
            summary = "Upload User Profile Image",
            description = "Uploads image of user profile with the registered mobile number."
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
    public ResponseEntity<String> uploadProfileImage(@RequestPart("photo") MultipartFile file,
                                                     @RequestParam long mobileNo) throws IOException, UserNotFoundException {
        String url = fileService.uploadUserProfileImage(file, mobileNo, Constants.PROFILE_S3_BUCKET);
        return ResponseEntity.ok(url);
    }

    /**
     *
     * @param file - kyc image file uploaded by user
     * @param mobileNo - mobile number of registered user
     * @param isFrontImage - to indicate front image or back image
     * @return - status of request
     * @throws IOException - exception occurred while uploading kyc image
     * @throws UserNotFoundException - throws exception when no user found with given mobile number
     */
    @Operation(
            summary = "Upload User KYC Image",
            description = "Uploads front or back KYC image for the given user based mobile number."
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
    @PostMapping(path = "/kyc/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCustomerKycImage(@RequestPart("photo") MultipartFile file,
                                                         @RequestParam long mobileNo, @RequestParam(defaultValue = "true") boolean isFrontImage)
            throws IOException, UserNotFoundException {
        String url = fileService.uploadUserKycImage(file, mobileNo, Constants.PROFILE_S3_BUCKET, isFrontImage);
        return ResponseEntity.ok(url);
    }
}
