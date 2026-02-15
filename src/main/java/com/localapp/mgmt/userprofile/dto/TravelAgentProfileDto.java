package com.localapp.mgmt.userprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelAgentProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private Long mobileNo;
    private String orgName;
    private List<String> languages;
    private LocalDate dateOfBirth;
    private String location;
    private List<Integer> serviceIds;
}
