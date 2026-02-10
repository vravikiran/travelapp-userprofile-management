package com.localapp.mgmt.userprofile.dto;

import lombok.Data;

@Data
public class EmailAuthRequest {
    private String email;
    private String otp;
}