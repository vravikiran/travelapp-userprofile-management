package com.localapp.mgmt.userprofile.mapper;

import com.localapp.mgmt.userprofile.dto.TravelAgentProfileDto;
import com.localapp.mgmt.userprofile.entities.TravelAgentProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TravelAgentProfileMapper {
    @Mapping(ignore = true, target = "agentKycDetails")
    @Mapping(ignore = true, target = "services")
    TravelAgentProfile travelAgentProfileDtoToTravelAgentProfile(TravelAgentProfileDto travelAgentProfileDto);
}
