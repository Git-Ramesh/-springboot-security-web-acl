package com.rs.app.model;

import org.springframework.security.acls.model.Permission;

public class CustomPermission implements Permission{

	@Override
	public int getMask() {
		return 0;
	}

	@Override
	public String getPattern() {
		return null;
	}

}
