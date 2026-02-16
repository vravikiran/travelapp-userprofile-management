package com.localapp.mgmt.userprofile.dto;

import lombok.Data;

@Data
public class EmailUpdateRequest {
    private String oldEmail;
    private String newEmail;
}
