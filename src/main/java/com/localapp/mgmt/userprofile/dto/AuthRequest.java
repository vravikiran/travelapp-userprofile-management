package com.localapp.mgmt.userprofile.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private long mobileNo;
    private String otp;
}