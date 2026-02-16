package com.localapp.mgmt.userprofile.services;

import java.time.LocalDate;

import com.localapp.mgmt.userprofile.dto.EmailUpdateRequest;
import com.localapp.mgmt.userprofile.dto.UserPreferencesDto;
import com.localapp.mgmt.userprofile.mapper.UserPreferencesMapper;
import com.localapp.mgmt.userprofile.mapper.UserProfileMapper;
import com.localapp.mgmt.userprofile.dto.UserProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.mgmt.userprofile.entities.KycDetails;
import com.localapp.mgmt.userprofile.entities.Role;
import com.localapp.mgmt.userprofile.entities.UserPreferences;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.enums.RoleTypeEnum;
import com.localapp.mgmt.userprofile.exceptions.DuplicateUserException;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.repostories.UserPreferencesRepository;
import com.localapp.mgmt.userprofile.repostories.UserProfileRepository;
import com.localapp.mgmt.userprofile.util.HashGenerator;

@Service
public class UserProfileService {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    UserPreferencesRepository preferencesRepository;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private UserPreferencesMapper userPreferencesMapper;

    public UserProfile createUserProfile(UserProfileDto userProfileDto) throws DuplicateUserException {
        if (isUserExistsWithMobileNo(userProfileDto.getMobileNo())) {
            throw new DuplicateUserException(
                    "User already exists with given mobile number : " + userProfileDto.getMobileNo());
        }
        if (isUserExistsWithEmail(userProfileDto.getEmail())) {
            throw new DuplicateUserException("User already exists with given email : " + userProfileDto.getEmail());
        }
        UserProfile userProfile = userProfileMapper.userProfileDtoToUserProfile(userProfileDto);
        userProfile.setEmailHash(HashGenerator.generateHashValueForEmail(userProfile.getEmail()));
        userProfile.setCreatedDate(LocalDate.now());
        userProfile.setUpdatedDate(LocalDate.now());
        userProfile.setMobileNoHash(HashGenerator.generateHashValueForMobileNo(userProfile.getMobileNo()));
        userProfile.setRole(new Role(RoleTypeEnum.CUSTOMER.getRole_id(), RoleTypeEnum.CUSTOMER.getRole_name()));
        if (userProfileDto.getPreferences() != null && !userProfileDto.getPreferences().getCuisinePreferences().isEmpty()) {
            UserPreferences userPreferences = userPreferencesMapper.userPreferencesDtoToUserPreferences(userProfileDto.getPreferences());
            userProfile.setPreferences(userPreferences);
        }
        userProfile.setActive(true);
        return userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfile(long mobileNo) throws UserNotFoundException {
        String mobile_no_hash = HashGenerator.generateHashValueForMobileNo(mobileNo);
        if (isUserExistsWithMobileNo(mobileNo)) {
            return userProfileRepository.getReferenceById(mobile_no_hash);
        } else {
            throw new UserNotFoundException("user not found with given mobile number : " + mobileNo);
        }
    }

    public void deActivateUser(long mobileNo) throws UserNotFoundException {
        UserProfile userProfile = getUserProfile(mobileNo);
        userProfile.setActive(false);
        userProfile.setUpdatedDate(LocalDate.now());
        userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfileByEmail(String email) throws UserNotFoundException {
        if (isUserExistsWithEmail(email)) {
            return userProfileRepository.getUserProfileByEmail(HashGenerator.generateHashValueForEmail(email));
        } else {
            throw new UserNotFoundException("User profile doesn't exists with given email");
        }
    }

    public UserProfile updateUserPreferences(UserPreferencesDto userPreferencesDto, long mobileNo) throws UserNotFoundException {
        UserProfile profile = getUserProfile(mobileNo);
        UserPreferences userPreferences = userPreferencesMapper.userPreferencesDtoToUserPreferences(userPreferencesDto);
        userPreferences.setUserProfile(profile);
        preferencesRepository.save(userPreferences);
        return getUserProfile(mobileNo);
    }

    public void updateKycDetails(KycDetails kycDetails, long mobileNo) throws UserNotFoundException {
        UserProfile profile = getUserProfile(mobileNo);
        kycDetails.setKyc_date(LocalDate.now());
        profile.setKycVerified(true);
        profile.setUpdatedDate(LocalDate.now());
        profile.setKycDetails(kycDetails);
        userProfileRepository.save(profile);
    }

    public UserPreferences getUserPreferences(long mobileNo) {
        return preferencesRepository.getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
    }

    public void updateEmail(EmailUpdateRequest emailUpdateRequest) throws UserNotFoundException, DuplicateUserException {
        UserProfile userProfile = getUserProfileByEmail(emailUpdateRequest.getOldEmail());
        if (!isUserExistsWithEmail(emailUpdateRequest.getNewEmail())) {
            userProfile.setEmail(emailUpdateRequest.getNewEmail());
            userProfile.setEmailHash(HashGenerator.generateHashValueForEmail(emailUpdateRequest.getNewEmail()));
            userProfile.setUpdatedDate(LocalDate.now());
            userProfileRepository.save(userProfile);
        } else {
            throw new DuplicateUserException("User already exists with given email:: " + emailUpdateRequest.getNewEmail());
        }
    }

    private boolean isUserExistsWithMobileNo(long mobileNo) {
        return userProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo));
    }

    private boolean isUserExistsWithEmail(String email) {
        return userProfileRepository.isUserExistsWithEmail(HashGenerator.generateHashValueForEmail(email));
    }

}
