package com.localapp.mgmt.userprofile.mapper;

import com.localapp.mgmt.userprofile.dto.UserPreferencesDto;
import com.localapp.mgmt.userprofile.entities.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {UserPreferencesMapper.class})
public interface UserPreferencesMapper {
    UserPreferences userPreferencesDtoToUserPreferences(UserPreferencesDto userPreferencesDto);
}
