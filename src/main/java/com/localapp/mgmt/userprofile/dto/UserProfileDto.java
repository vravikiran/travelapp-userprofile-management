package com.localapp.mgmt.userprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private Long mobileNo;
    private boolean isKycVerified;
    private LocalDate dateOfBirth;
    private boolean active;
    private UserPreferencesDto preferences;
}
