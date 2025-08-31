package com.localapp.mgmt.userprofile.services;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.mgmt.userprofile.entities.KycDetails;
import com.localapp.mgmt.userprofile.entities.Role;
import com.localapp.mgmt.userprofile.entities.UserPreferences;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import com.localapp.mgmt.userprofile.enums.RoleTypeEnum;
import com.localapp.mgmt.userprofile.exceptions.DuplicateUserException;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.repostories.KycDetailsRepository;
import com.localapp.mgmt.userprofile.repostories.UserPreferencesRepository;
import com.localapp.mgmt.userprofile.repostories.UserProfileRepository;
import com.localapp.mgmt.userprofile.util.Constants;
import com.localapp.mgmt.userprofile.util.HashGenerator;

@Service
public class UserProfileService {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    UserPreferencesRepository preferencesRepository;
    @Autowired
    KycDetailsRepository kycDetailsRepository;

    public UserProfile createUserProfile(UserProfile userProfile) throws DuplicateUserException {
        if (isUserExistsWithMobileNo(userProfile.getMobileNo())) {
            throw new DuplicateUserException(
                    "User already exists with given mobile number : " + userProfile.getMobileNo());
        }
        if (userProfile.getEmail() != null && isUserExistsWithEmail(userProfile.getEmail())) {
            throw new DuplicateUserException("User already exists with given email : " + userProfile.getEmail());
        }
        userProfile.setEmail_hash(HashGenerator.generateHashValueForEmail(userProfile.getEmail()));
        userProfile.setCreated_date(LocalDate.now());
        userProfile.setUpdated_date(LocalDate.now());
        userProfile.setMobile_no_hash(HashGenerator.generateHashValueForMobileNo(userProfile.getMobileNo()));
        userProfile.setRole(new Role(RoleTypeEnum.CUSTOMER.getRole_id(), RoleTypeEnum.CUSTOMER.getRole_name()));
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
        userProfileRepository.save(userProfile);
    }

    public UserProfile updateUserDetails(long mobileNo, Map<String, String> valuesToUpdate)
            throws DuplicateUserException, NoSuchElementException, UserNotFoundException {
        UserProfile userProfile = getUserProfile(mobileNo);
        if (valuesToUpdate.containsKey(Constants.MOBILE_NO)) {
            if (isUserExistsWithMobileNo(Long.parseLong(valuesToUpdate.get(Constants.MOBILE_NO)))) {
                throw new DuplicateUserException(
                        "User already exists with given mobile number : " + valuesToUpdate.get(Constants.MOBILE_NO));
            } else {
                valuesToUpdate.put(Constants.MOBILE_NO_HASH,
                        HashGenerator.generateHashValueForMobileNo(Long.parseLong(valuesToUpdate.get(Constants.MOBILE_NO))));
            }
        } else if (valuesToUpdate.containsKey(Constants.EMAIL)) {
            if (isUserExistsWithEmail((valuesToUpdate.get(Constants.EMAIL)))) {
                throw new DuplicateUserException("User already exists with given email : " + valuesToUpdate.get(Constants.EMAIL));
            } else {
                valuesToUpdate.put(Constants.EMAIL_HASH, HashGenerator.generateHashValueForEmail(valuesToUpdate.get(Constants.EMAIL)));
            }
        }
        try {
            userProfile.updateValues(userProfile, valuesToUpdate);
            userProfile.setUpdated_date(LocalDate.now());
        } catch (NoSuchElementException exception) {
            throw new NoSuchElementException("one or more fields are not valid");
        }
        return userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfileByEmail(String email) throws UserNotFoundException {
        if (isUserExistsWithEmail(email)) {
            return userProfileRepository.getUserProfileByEmail(HashGenerator.generateHashValueForEmail(email));
        } else {
            throw new UserNotFoundException("User profile doesn't exists with given email");
        }
    }

    public UserProfile updateUserPreferences(UserPreferences preferences, long mobileNo) throws UserNotFoundException {
        UserProfile profile = getUserProfile(mobileNo);
        preferences.setUserProfile(profile);
        preferencesRepository.save(preferences);
        return getUserProfile(mobileNo);
    }

    public void updateKycDetails(KycDetails kycDetails, long mobileNo) throws UserNotFoundException {
        UserProfile profile = getUserProfile(mobileNo);
        kycDetails.setKyc_date(LocalDate.now());
        profile.set_kyc_verified(true);
        profile.setUpdated_date(LocalDate.now());
        profile.setKycDetails(kycDetails);
        userProfileRepository.save(profile);
    }

    public UserPreferences getUserPreferences(long mobileNo) throws UserNotFoundException {
        return preferencesRepository.getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
    }

    private boolean isUserExistsWithMobileNo(long mobileNo) {
        return userProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo));
    }

    private boolean isUserExistsWithEmail(String email) {
        return userProfileRepository.isUserExistsWithEmail(HashGenerator.generateHashValueForEmail(email));
    }
}
