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
		if (isUserExistsWithMobileNo(userProfile.getMobileno())) {
			throw new DuplicateUserException(
					"User already exists with given mobile number : " + userProfile.getMobileno());
		}
		if (userProfile.getEmail() != null && isUserExistsWithEmail(userProfile.getEmail())) {
			throw new DuplicateUserException("User already exists with given email : " + userProfile.getEmail());
		}
		userProfile.setEmail_hash(HashGenerator.generateHashValueForEmail(userProfile.getEmail()));
		userProfile.setCreated_date(LocalDate.now());
		userProfile.setUpdated_date(LocalDate.now());
		userProfile.setMobileno_hash(HashGenerator.generateHashValueForMobileNo(userProfile.getMobileno()));
		userProfile.setRole(new Role(RoleTypeEnum.CUSTOMER.getRoleid(), RoleTypeEnum.CUSTOMER.getRole_name()));
		userProfile.setIsactive(true);
		return userProfileRepository.save(userProfile);
	}

	public UserProfile getUserProfile(long mobileno) throws UserNotFoundException {
		String mobileno_hash = HashGenerator.generateHashValueForMobileNo(mobileno);
		if (isUserExistsWithMobileNo(mobileno)) {
			return userProfileRepository.getReferenceById(mobileno_hash);
		} else {
			throw new UserNotFoundException("user not found with given mobileno : " + mobileno);
		}
	}

	public void deActivateUser(long mobileno) throws UserNotFoundException {
		UserProfile userProfile = getUserProfile(mobileno);
		userProfile.setIsactive(false);
		userProfileRepository.save(userProfile);
	}

	public UserProfile updateUserDetails(long mobileno, Map<String, String> valuesToUpdate)
			throws DuplicateUserException, NoSuchElementException, UserNotFoundException {
		UserProfile userProfile = getUserProfile(mobileno);
		if (valuesToUpdate.containsKey(Constants.MOBILENO)) {
			if (isUserExistsWithMobileNo(Long.valueOf(valuesToUpdate.get(Constants.MOBILENO)))) {
				throw new DuplicateUserException(
						"User already exists with given mobile number : " + valuesToUpdate.get(Constants.MOBILENO));
			} else {
				valuesToUpdate.put(Constants.MOBILENO_HASH,
						HashGenerator.generateHashValueForMobileNo(Long.valueOf(valuesToUpdate.get(Constants.MOBILENO))));
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
	
	public UserProfile updateUserPreferences(UserPreferences preferences,long mobileno) throws UserNotFoundException {
		UserProfile profile = getUserProfile(mobileno);
		preferences.setUserProfile(profile);
		preferencesRepository.save(preferences);
		return getUserProfile(mobileno);
	}
	
	public void updateKycDetails(KycDetails kycDetails,long mobileno) throws UserNotFoundException {
		UserProfile profile = getUserProfile(mobileno);
		kycDetails.setKyc_date(LocalDate.now());
		profile.setIs_kyc_verified(true);
		profile.setUpdated_date(LocalDate.now());
		profile.setKycDetails(kycDetails);
		userProfileRepository.save(profile);
	}
	
	public UserPreferences getUserPreferences(long mobileno) throws UserNotFoundException {
		UserProfile userProfile = getUserProfile(mobileno);
		return userProfile.getPreferences();
	}

	private boolean isUserExistsWithMobileNo(long mobileno) {
		return userProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileno));
	}

	private boolean isUserExistsWithEmail(String email) {
		return userProfileRepository.isUserExistsWithEmail(HashGenerator.generateHashValueForEmail(email));
	}
}
