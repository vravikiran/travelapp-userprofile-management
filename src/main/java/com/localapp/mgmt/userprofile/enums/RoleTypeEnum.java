package com.localapp.mgmt.userprofile.enums;

public enum RoleTypeEnum {
	ADMIN(1, "ADMIN"), TRAVEL_AGENT(2, "TRAVEL_AGENT"), CUSTOMER(3, "CUSTOMER");

	private final int roleid;
	private final String role_name;

	private RoleTypeEnum(int roleid, String role_name) {
		this.roleid = roleid;
		this.role_name = role_name;
	}

	public int getRoleid() {
		return roleid;
	}

	public String getRole_name() {
		return role_name;
	}
}
