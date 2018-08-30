package com.rs.app.acl;

import java.util.List;
import java.util.Map;

import org.springframework.security.acls.model.Permission;

public interface AclService {
	void createAclAndGrantAccess(Object obj, String recipient, boolean principal, int[] masks, boolean setOwner);

	void  updateAclPermission(Object obj, int[] masks);

	//void deleteAclPermissionToSid(Class<?> objectType, Long securedObjectId, String sid);

	void updateObjectOwner(Long securedObjectId, Class<?> objectType, String sid, boolean principal);

	void deleteAcl(Class<?> objectType, Long securedObjectId);

	public Map<Long, Map<String, String>> getPermissionsFromAclEntry(Class<?> clazz);
}
