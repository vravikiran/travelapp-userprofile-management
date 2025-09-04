package com.localapp.mgmt.userprofile.enums;

import lombok.Getter;

@Getter
public enum RoleTypeEnum {
    ADMIN(1, "ADMIN"), TRAVEL_AGENT(2, "TRAVEL_AGENT"), CUSTOMER(3, "CUSTOMER");

    private final int role_id;
    private final String role_name;

    private RoleTypeEnum(int role_id, String role_name) {
        this.role_id = role_id;
        this.role_name = role_name;
    }
}
