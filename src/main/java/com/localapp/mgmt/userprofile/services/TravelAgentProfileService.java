package com.localapp.mgmt.userprofile.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localapp.mgmt.userprofile.entities.AgentService;
import com.localapp.mgmt.userprofile.entities.Role;
import com.localapp.mgmt.userprofile.entities.TravelAgentKycDetails;
import com.localapp.mgmt.userprofile.entities.TravelAgentProfile;
import com.localapp.mgmt.userprofile.enums.RoleTypeEnum;
import com.localapp.mgmt.userprofile.exceptions.DuplicateUserException;
import com.localapp.mgmt.userprofile.exceptions.UserNotFoundException;
import com.localapp.mgmt.userprofile.repostories.ServiceRepository;
import com.localapp.mgmt.userprofile.repostories.TravelAgentProfileRepository;
import com.localapp.mgmt.userprofile.repostories.TravelAgentServicesRepository;
import com.localapp.mgmt.userprofile.util.Constants;
import com.localapp.mgmt.userprofile.util.HashGenerator;

@Service
public class TravelAgentProfileService {
	@Autowired
	TravelAgentProfileRepository travelAgentProfileRepository;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	TravelAgentServicesRepository agentServicesRepository;

	public TravelAgentProfile createTravelAgentProfile(TravelAgentProfile travelAgentProfile)
			throws DuplicateUserException {
		if (Long.valueOf(travelAgentProfile.getMobileno()) != null
				&& isAgentExistsByMobileNo(travelAgentProfile.getMobileno())) {
			throw new DuplicateUserException("Agent Profile already exists with given mobile number");
		}
		if (travelAgentProfile.getEmail() != null && isAgentExistsWithEmail(travelAgentProfile.getEmail())) {
			throw new DuplicateUserException(
					"Agent profile already exists with given email : " + travelAgentProfile.getEmail());
		}
		travelAgentProfile.setEmail_hash(HashGenerator.generateHashValueForEmail(travelAgentProfile.getEmail()));
		travelAgentProfile
				.setMobileno_hash(HashGenerator.generateHashValueForMobileNo(travelAgentProfile.getMobileno()));
		travelAgentProfile.setCreated_date(LocalDate.now());
		travelAgentProfile.setUpdated_date(LocalDate.now());
		travelAgentProfile.setIsactive(true);
		travelAgentProfile
				.setRole(new Role(RoleTypeEnum.TRAVEL_AGENT.getRoleid(),RoleTypeEnum.TRAVEL_AGENT.getRole_name()));
		List<AgentService> services = serviceRepository.findAllById(travelAgentProfile.getServiceids());
		travelAgentProfile.setServices(new HashSet<>(services));
		return travelAgentProfileRepository.save(travelAgentProfile);
	}

	public TravelAgentProfile getTravelAgentProfile(long mobileno) throws UserNotFoundException {
		if (isAgentExistsByMobileNo(mobileno)) {
			return travelAgentProfileRepository.getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileno));
		} else {
			throw new UserNotFoundException("No travel agent exists with given mobile number : " + mobileno);
		}
	}

	public void deActivateTravelAgentProfile(long mobileno) throws UserNotFoundException {
		TravelAgentProfile agentProfile = getTravelAgentProfile(mobileno);
		agentProfile.setUpdated_date(LocalDate.now());
		agentProfile.setIsactive(false);
	}

	public TravelAgentProfile updateTravelAgentProfile(Map<String, String> valuesToUpdate, long mobileno)
			throws UserNotFoundException, DuplicateUserException, NoSuchElementException {
		TravelAgentProfile travelAgentProfile = getTravelAgentProfile(mobileno);
		if (valuesToUpdate.containsKey(Constants.EMAIL)) {
			if (isAgentExistsWithEmail(valuesToUpdate.get(Constants.EMAIL))) {
				throw new DuplicateUserException(
						"Agent Profile already exists with given email : " + valuesToUpdate.get(Constants.EMAIL));
			} else {
				valuesToUpdate.put(Constants.EMAIL_HASH,
						HashGenerator.generateHashValueForEmail(valuesToUpdate.get(Constants.EMAIL)));
			}
		}
		try {
			travelAgentProfile.updateValues(travelAgentProfile, valuesToUpdate);
			travelAgentProfile.setUpdated_date(LocalDate.now());
		} catch (NoSuchElementException exception) {
			throw new NoSuchElementException("one or more fields are not valid");
		}
		return travelAgentProfileRepository.save(travelAgentProfile);
	}

	public Set<AgentService> updateTravelAgentServices(List<Integer> ids, long mobileno) throws UserNotFoundException {
		TravelAgentProfile agentProfile = getTravelAgentProfile(mobileno);
		List<AgentService> servicesToUpdt = serviceRepository.findAllById(ids);
		agentProfile.setServices(new HashSet<>(servicesToUpdt));
		agentProfile.setUpdated_date(LocalDate.now());
		travelAgentProfileRepository.save(agentProfile);
		return agentProfile.getServices();
	}

	public List<String> updateTravelAgentLanguages(List<String> languages, long mobileno) throws UserNotFoundException {
		TravelAgentProfile agentProfile = getTravelAgentProfile(mobileno);
		agentProfile.setLanguages(languages);
		travelAgentProfileRepository.save(agentProfile);
		return agentProfile.getLanguages();
	}

	public void updateKycDetails(TravelAgentKycDetails agentKycDetails, long mobileno) throws UserNotFoundException {
		TravelAgentProfile agentProfile = getTravelAgentProfile(mobileno);
		agentKycDetails.setKyc_date(LocalDate.now());
		agentProfile.setIs_kyc_verified(true);
		agentProfile.setUpdated_date(LocalDate.now());
		agentProfile.setAgentKycDetails(agentKycDetails);
		travelAgentProfileRepository.save(agentProfile);
	}

	
	/*public List<TravelAgentProfile> filterTravelAgentsByServices(List<Integer> ids, String location) {
		return travelAgentProfileRepository.filterTravelAgentsByServices(ids, location);
	}*/
	
	public List<TravelAgentProfile> filterTravelAgentsByServices(List<Integer> ids, List<TravelAgentProfile> agentProfiles) {
		List<TravelAgentProfile> filteredProfiles = null;
		if (agentProfiles.size() > 0) {
			filteredProfiles = new ArrayList<>();
			for (TravelAgentProfile agentProfile : agentProfiles) {
				List<AgentService> matchedServices = agentProfile.getServices().stream()
						.filter(service -> ids.stream().anyMatch(id -> id.equals(service.getService_id())))
						.collect(Collectors.toList());
				if (matchedServices.size() > 0) {
					filteredProfiles.add(agentProfile);
				}
			}
		}
		return filteredProfiles;
	}

	/*public List<TravelAgentProfile> filterTravelAgentsByLanguages(List<String> languages, String location) {
		return travelAgentProfileRepository.filterTravelAgentsByLanguages(languages, location);
	}*/
	
	public List<TravelAgentProfile> filterTravelAgentsByLanguages(List<String> languages, List<TravelAgentProfile> agentProfiles) {
		List<TravelAgentProfile> filteredProfiles = null;
		if (agentProfiles.size() > 0) {
			filteredProfiles = new ArrayList<>();
			for (TravelAgentProfile agentProfile : agentProfiles) {
				List<String> matchedLanguages = agentProfile.getLanguages().stream()
						.filter(language -> languages.stream().anyMatch(filterLang -> filterLang.equals(language)))
						.collect(Collectors.toList());
				if (matchedLanguages.size() > 0) {
					filteredProfiles.add(agentProfile);
				}
			}
		}
		return filteredProfiles;
	}

	public List<TravelAgentProfile> filterTravelAgents(String location, List<String> languages, List<Integer> ids) {
		List<TravelAgentProfile> filteredProfiles = null;
		List<TravelAgentProfile> agentProfiles = travelAgentProfileRepository.filterTravelAgentsByLocation(location);
		filteredProfiles = new ArrayList<>();
		filteredProfiles.addAll(agentProfiles);
		if (agentProfiles.size() > 0) {
			filteredProfiles = new ArrayList<>();
			filteredProfiles.addAll(agentProfiles);
			if ( ids != null && ids.size() > 0) {
				List<TravelAgentProfile> profilesByServices = filterTravelAgentsByServices(ids, agentProfiles);
				filteredProfiles.addAll(profilesByServices);
			}
			if (languages != null && languages.size() > 0) {
				List<TravelAgentProfile> profilesByLang = filterTravelAgentsByLanguages(languages, agentProfiles);
				filteredProfiles.addAll(profilesByLang);
			}
		}
		return filteredProfiles.stream().distinct().toList();
	}

	private boolean isAgentExistsWithEmail(String email) {
		return travelAgentProfileRepository.isUserExistsWithEmail(HashGenerator.generateHashValueForEmail(email));
	}

	private boolean isAgentExistsByMobileNo(long mobileno) {
		return travelAgentProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileno));
	}

	public TravelAgentProfile getAgentProfileByEmail(String email) {
		return travelAgentProfileRepository.getAgentProfileByEmail(HashGenerator.generateHashValueForEmail(email));
	}
}
