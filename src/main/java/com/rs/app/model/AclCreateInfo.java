package com.rs.app.model;

import lombok.Data;

@Data
public class AclCreateInfo {
	//private String objectType;
	private int objectid;
	private String recipient;
	private boolean principal;
	private boolean setOwner;
	private int[] masks;
}
