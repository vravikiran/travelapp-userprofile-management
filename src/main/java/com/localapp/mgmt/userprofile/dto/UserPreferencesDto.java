package com.localapp.mgmt.userprofile.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesDto {

    private List<String> destinationTypes;
    private List<String> cuisinePreferences;
    private String vehicleType;
    private String accommodationType;
    private List<String> languages;
}
