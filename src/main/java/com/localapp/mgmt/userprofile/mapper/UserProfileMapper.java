package com.localapp.mgmt.userprofile.mapper;

import com.localapp.mgmt.userprofile.dto.UserProfileDto;
import com.localapp.mgmt.userprofile.entities.UserProfile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {UserPreferencesMapper.class})
public interface UserProfileMapper {
    @Mapping(ignore = true, target = "kycDetails")
    UserProfile userProfileDtoToUserProfile(UserProfileDto userProfileDto);

    @Mapping(ignore = true, target = "kycDetails")
    void updateUserProfileDto(UserProfileDto userProfileDto, @MappingTarget UserProfile userProfile);

    @AfterMapping
    default void linkPreferences(@MappingTarget UserProfile profile) {
        if (profile.getPreferences() != null) {
            profile.getPreferences().setUserProfile(profile);
        }
    }
}
