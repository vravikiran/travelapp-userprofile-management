package com.localapp.mgmt.userprofile.services;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    public TravelAgentProfile createTravelAgentProfile(TravelAgentProfile travelAgentProfile) throws DuplicateUserException {
        if (travelAgentProfile.getMobileNo() != null && isAgentExistsByMobileNo(travelAgentProfile.getMobileNo())) {
            throw new DuplicateUserException("Agent Profile already exists with given mobile number");
        }
        if (travelAgentProfile.getEmail() != null && isAgentExistsWithEmail(travelAgentProfile.getEmail())) {
            throw new DuplicateUserException("Agent profile already exists with given email : " + travelAgentProfile.getEmail());
        }
        travelAgentProfile.setEmail_hash(HashGenerator.generateHashValueForEmail(travelAgentProfile.getEmail()));
        travelAgentProfile.setMobile_no_hash(HashGenerator.generateHashValueForMobileNo(travelAgentProfile.getMobileNo()));
        travelAgentProfile.setCreated_date(LocalDate.now());
        travelAgentProfile.setUpdated_date(LocalDate.now());
        travelAgentProfile.setActive(true);
        travelAgentProfile.setRole(new Role(RoleTypeEnum.TRAVEL_AGENT.getRole_id(), RoleTypeEnum.TRAVEL_AGENT.getRole_name()));
        return travelAgentProfileRepository.save(travelAgentProfile);
    }

    public TravelAgentProfile getTravelAgentProfile(long mobileNo) throws UserNotFoundException {
        if (isAgentExistsByMobileNo(mobileNo)) {
            return travelAgentProfileRepository.getReferenceById(HashGenerator.generateHashValueForMobileNo(mobileNo));
        } else {
            throw new UserNotFoundException("No travel agent exists with given mobile number : " + mobileNo);
        }
    }

    public void deActivateTravelAgentProfile(long mobileNo) throws UserNotFoundException {
        TravelAgentProfile agentProfile = getTravelAgentProfile(mobileNo);
        agentProfile.setUpdated_date(LocalDate.now());
        agentProfile.setActive(false);
    }

    public TravelAgentProfile updateTravelAgentProfile(Map<String, String> valuesToUpdate, long mobileNo) throws UserNotFoundException, DuplicateUserException, NoSuchElementException {
        TravelAgentProfile travelAgentProfile = getTravelAgentProfile(mobileNo);
        if (valuesToUpdate.containsKey(Constants.EMAIL)) {
            if (isAgentExistsWithEmail(valuesToUpdate.get(Constants.EMAIL))) {
                throw new DuplicateUserException("Agent Profile already exists with given email : " + valuesToUpdate.get(Constants.EMAIL));
            } else {
                valuesToUpdate.put(Constants.EMAIL_HASH, HashGenerator.generateHashValueForEmail(valuesToUpdate.get(Constants.EMAIL)));
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

    public List<AgentService> updateTravelAgentServices(List<Integer> ids, long mobileNo) throws UserNotFoundException {
        TravelAgentProfile agentProfile = getTravelAgentProfile(mobileNo);
        List<AgentService> services = serviceRepository.findAllById(ids);
        List<AgentService> existingServices = agentProfile.getServices();
        existingServices.addAll(services);
        agentProfile.setServices(existingServices);
        agentProfile.setUpdated_date(LocalDate.now());
        travelAgentProfileRepository.save(agentProfile);
        return agentProfile.getServices();
    }

    public List<String> updateTravelAgentLanguages(List<String> languages, long mobileNo) throws UserNotFoundException {
        TravelAgentProfile agentProfile = getTravelAgentProfile(mobileNo);
        agentProfile.setLanguages(languages);
        travelAgentProfileRepository.save(agentProfile);
        return agentProfile.getLanguages();
    }

    public void updateKycDetails(TravelAgentKycDetails agentKycDetails, long mobileNo) throws UserNotFoundException {
        TravelAgentProfile agentProfile = getTravelAgentProfile(mobileNo);
        agentKycDetails.setKyc_date(LocalDate.now());
        agentProfile.set_kyc_verified(true);
        agentProfile.setUpdated_date(LocalDate.now());
        agentProfile.setAgentKycDetails(agentKycDetails);
        travelAgentProfileRepository.save(agentProfile);
    }

	
	/*public List<TravelAgentProfile> filterTravelAgentsByServices(List<Integer> ids, String location) {
		return travelAgentProfileRepository.filterTravelAgentsByServices(ids, location);
	}*/

    public List<TravelAgentProfile> filterTravelAgentsByServices(List<Integer> ids, List<TravelAgentProfile> agentProfiles) {
        List<TravelAgentProfile> filteredProfiles = null;
        if (!agentProfiles.isEmpty()) {
            filteredProfiles = new ArrayList<>();
            for (TravelAgentProfile agentProfile : agentProfiles) {
                List<AgentService> matchedServices = agentProfile.getServices().stream().filter(service -> ids.stream().anyMatch(id -> id.equals(service.getService_id()))).toList();
                if (!matchedServices.isEmpty()) {
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
        if (!agentProfiles.isEmpty()) {
            filteredProfiles = new ArrayList<>();
            for (TravelAgentProfile agentProfile : agentProfiles) {
                List<String> matchedLanguages = agentProfile.getLanguages().stream().filter(language -> languages.stream().anyMatch(filterLang -> filterLang.equals(language))).toList();
                if (!matchedLanguages.isEmpty()) {
                    filteredProfiles.add(agentProfile);
                }
            }
        }
        return filteredProfiles;
    }

    public List<TravelAgentProfile> filterTravelAgents(String location, List<String> languages, List<Integer> ids) {
        List<TravelAgentProfile> filteredProfiles = null;
        List<TravelAgentProfile> agentProfiles = travelAgentProfileRepository.filterTravelAgentsByLocation(location);
        filteredProfiles = new ArrayList<>(agentProfiles);
        if (!agentProfiles.isEmpty()) {
            filteredProfiles = new ArrayList<>(agentProfiles);
            if (ids != null && !ids.isEmpty()) {
                List<TravelAgentProfile> profilesByServices = filterTravelAgentsByServices(ids, agentProfiles);
                filteredProfiles.addAll(profilesByServices);
            }
            if (languages != null && !languages.isEmpty()) {
                List<TravelAgentProfile> profilesByLang = filterTravelAgentsByLanguages(languages, agentProfiles);
                filteredProfiles.addAll(profilesByLang);
            }
        }
        return filteredProfiles.stream().distinct().toList();
    }

    private boolean isAgentExistsWithEmail(String email) {
        return travelAgentProfileRepository.isUserExistsWithEmail(HashGenerator.generateHashValueForEmail(email));
    }

    private boolean isAgentExistsByMobileNo(long mobileNo) {
        return travelAgentProfileRepository.existsById(HashGenerator.generateHashValueForMobileNo(mobileNo));
    }

    public TravelAgentProfile getAgentProfileByEmail(String email) {
        return travelAgentProfileRepository.getAgentProfileByEmail(HashGenerator.generateHashValueForEmail(email));
    }
}
