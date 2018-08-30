package com.rs.app.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.springframework.security.acls.model.ObjectIdentity;

import com.rs.app.model.AclClass;
import com.rs.app.model.AclEntry;
import com.rs.app.model.AclObjectIdentity;
import com.rs.app.model.AclSid;

public interface AclRepo {

	List<ObjectIdentity> findChildren(Serializable identifier, String type);

	AclObjectIdentity getObjectIdentity(String type, Serializable identifier);

	void createObjectIdentity(AclObjectIdentity identity);

	List<AclSid> findAclSidList(Boolean valueOf, String sidName);

	AclSid createAclSid(AclSid sid2);

	List<AclClass> findAclClassList(String type);

	AclClass createAclClass(AclClass clazz);

	void deleteEntries(AclObjectIdentity oidPrimaryKey);

	void deleteObjectIdentity(AclObjectIdentity oidPrimaryKey);

	void createEntries(List<AclEntry> entries);

	boolean updateObjectIdentity(AclObjectIdentity aclObject);

	AclSid findAclSid(String principal);
	List<Long> getObjectSecuredIdentities(Class<?> clazz);
	public void updateMask(int mask, long identifier);
	List<BigInteger> getDistinctObjdentities() ;
	List<AclEntry> getACEList(long securedObjectIdentity);

}
