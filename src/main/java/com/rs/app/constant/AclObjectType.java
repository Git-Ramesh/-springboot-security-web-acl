package com.rs.app.constant;

import com.rs.app.model.Topic;

public class AclObjectType {
	public static final Class<Topic> TOPIC = Topic.class;
	public static Class<?> getObjectType(String objectType) {
		Class<?> otype = null;
		if(objectType.equalsIgnoreCase("TOPIC")) {
			otype = Topic.class;
		}
		return otype;
	}
	private AclObjectType() {
		
	}
}
