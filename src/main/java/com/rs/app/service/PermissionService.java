package com.rs.app.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.rs.app.model.AclObjectIdentity;
import com.rs.app.model.Topic;
import com.rs.app.repository.AclRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PermissionService {
	@Autowired
	private AclService aclService;
	@Autowired
	AclRepo aclRepo;

	public void updatePermission(Class<?> objectType, Long oi, String authority, Boolean principal, boolean granting, List<Permission> permission) {
		MutableAcl acl = null;
		acl = getAclForObject(objectType, oi);
		Sid sid = null;
		if (principal) {
			sid = new PrincipalSid(authority);
		} else {
			sid = new GrantedAuthoritySid(authority);
		}
		if (acl != null) {
			List<AccessControlEntry> entries = acl.getEntries();
			for (AccessControlEntry entry : entries) {
				// acl.u
			}
		}
	}

	/**
	 * Removes the ACL permissions on given object id
	 * 
	 * @param objectType
	 *            acl_class
	 * @param oi
	 *            acl_object_identity
	 * @param authority
	 *            acl_class table sid
	 * @param principal
	 *            acl_class table principal value
	 * @return void
	 */
	public void deleteAccessToSid(Class<?> objectType, Long oi, String authority, Boolean principal) {
		MutableAcl acl = null;

		acl = getAclForObject(objectType, oi);
		String sid = null;
		if (principal) {
			sid = new PrincipalSid(authority).getPrincipal();

		} else {
			sid = new GrantedAuthoritySid(authority).getGrantedAuthority();
		}
		if (sid != null && acl != null) {
			List<AccessControlEntry> entries = acl.getEntries();
			if (entries != null) {
				int i = 0;
				for (AccessControlEntry entry : entries) {
					String aclSid = "";
					if (entry.getSid() instanceof GrantedAuthoritySid) {
						GrantedAuthoritySid gaSid = (GrantedAuthoritySid) entry.getSid();
						aclSid = gaSid.getGrantedAuthority();
					} else {
						PrincipalSid pSid = (PrincipalSid) entry.getSid();
						aclSid = pSid.getPrincipal();
					}
					if (sid.equals(aclSid))
						acl.deleteAce(i);
					else
						i++;
				}
				((MutableAclService) aclService).updateAcl(acl);
			}
		}
	}

	/**
	 * Get all sids available on oi
	 * 
	 * @param objectType
	 * @param oi(AclEntry
	 *            class id)
	 * @return list of permissions
	 */
	public List<String> getSidsOnObjectIdentiy(Class<?> objectType, Long oi) {
		System.out.println("getSidsOnObjectIdentiy******");
		System.out.println("Identifier: " + oi);
		MutableAcl acl = null;
		acl = getAclForObject(objectType, oi);
		if (acl != null) {
			System.out.println("AclEntries: " + acl.getEntries().size());
			System.out.println(acl.getEntries());
			return acl.getEntries().stream().map(entry -> entry.getSid()).map(sid -> {
				if (sid instanceof PrincipalSid) {
					return ((PrincipalSid) sid).getPrincipal();
				} else {
					return ((GrantedAuthoritySid) sid).getGrantedAuthority();
				}
			}).collect(Collectors.toList());
		} else {
			return null;
		}
	}

	// Get the permission available on particular objectid
	// http://localhost:4014/permission/4/ROLE_USER/false
	public Map<String, String> getPermissionsOnObjectIdentity(Class<?> objectType, Long oi, String authority,
			Boolean principal) {
		MutableAcl acl = null;
		final Map<String, String> principal_permission = new HashMap<>();
		acl = getAclForObject(objectType, oi);
		if (acl != null) {
			List<AccessControlEntry> entries = acl.getEntries();
			System.out.println("AclEntries: " + acl.getEntries().size());
			String sid = null;
			if (principal) {
				sid = new PrincipalSid(authority).getPrincipal();
			} else {
				sid = new GrantedAuthoritySid(authority).getGrantedAuthority();
			}
			System.out.println("sid" + sid);
			for (AccessControlEntry entry : entries) {
				String sidString = "";
				String permission = "";

				Sid aclsid = entry.getSid();
				if (aclsid instanceof PrincipalSid) {
					sidString = ((PrincipalSid) aclsid).getPrincipal();
				} else {
					sidString = ((GrantedAuthoritySid) aclsid).getGrantedAuthority();
				}
				System.out.println("sidString: " + sidString);
				if (sid.equalsIgnoreCase(sidString)) {
					System.out.println("sidString: " + sidString);
					int mask = entry.getPermission().getMask();
					switch (mask) {
					case 1:
						permission = "READ";
						break;
					case 2:
						permission = "WRITE";
						break;
					case 4:
						permission = "CREATE";
						break;
					case 8:
						permission = "DELETE";
						break;
					case 16:
						permission = "ADMINISTRATION";
						break;
					}
				}
				principal_permission.put(sid, permission);
			}
		}
		return principal_permission;
	}

	/**
	 * Returns the MutableAcl of securedObjectId
	 */
	public MutableAcl getAclForObject(Class<?> javaType, Long identifier) {
		ObjectIdentity oid = null;
		System.out.println("Identifier: " + identifier);
		oid = new ObjectIdentityImpl(Topic.class, identifier);
		if (oid instanceof ObjectIdentity)
			System.out.println("*****" + true + "********");
		else
			System.out.println("*****" + false + "********");
		MutableAcl acl = null;
		if (oid != null) {
			try {
				acl = (MutableAcl) aclService.readAclById(oid);
			} catch (NotFoundException nfe) {
				log.error(nfe.getMessage());
			}
		}
		return acl;
	}

	/**
	 * Retrieves the primary key from the acl_object_identity table for the passed
	 * ObjectIdentity.
	 * 
	 * @param oid
	 *            to find
	 *
	 * @return the object identity or null if not found
	 */
	protected AclObjectIdentity retrieveObjectIdentityByPrimaryKey(ObjectIdentity oid) {
		try {
			return aclRepo.getObjectIdentity(oid.getType(), oid.getIdentifier());
		} catch (NoResultException notFound) {
			return null;
		}
	}

	public Map<Long, Map<String, String>> getPermissionFromAclEntry(Class<?> clazz) {
		final Map<Long, Map<String, String>> map = new HashMap<>();

		List<Long> identifiers = aclRepo.getObjectSecuredIdentities(clazz);
		System.out.println(identifiers);
		System.out.println("Size: " + identifiers.size());
		for (Long identifier : identifiers) {
			MutableAcl acl = getAclForObject(clazz, identifier);
			List<AccessControlEntry> entries = acl.getEntries();
			for (AccessControlEntry entry : entries) {
				Sid aclsid = entry.getSid();
				System.out.println("Owner: "+ acl.getOwner());
				String sidString = "";
				if (aclsid instanceof PrincipalSid) {
					sidString = ((PrincipalSid) aclsid).getPrincipal();
				} else {
					sidString = ((GrantedAuthoritySid) aclsid).getGrantedAuthority();
				}
				int mask = entry.getPermission().getMask();
				String permission = "";
				System.out.println("Mask: " + mask);
				switch (mask) {
				case 1:
					permission = "READ";
					break;
				case 2:
					permission = "WRITE";
					break;
				case 4:
					permission = "CREATE";
					break;
				case 8:
					permission = "DELETE";
					break;
				case 16:
					permission = "ADMINISTRATION";
					break;
				}
				final Map<String, String> submap = new HashMap<>();
				submap.put(sidString, permission);
				map.put(identifier, submap);
			}
		}
		return map;
	}

	public void isPermissionGranted(Permission permission, Sid sid, MutableAcl acl) {
		try {
			acl.isGranted(Arrays.asList(permission), Arrays.asList(sid), false);
			System.out.println("index: "+ acl.getEntries().size());
		} catch (NotFoundException e) {
			acl.insertAce(acl.getEntries().size(), permission, sid, true);
		}
	}
	
	public void createAclAndGrantAccess(Long securedObjectId, Class clazz, String recipient, boolean isPrincipal, List<Permission> perms, boolean setOwner)
	{
		ObjectIdentity oi = new ObjectIdentityImpl(clazz, securedObjectId);
		
		//	Create or update the relevant ACL
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = ((MutableAclService) aclService).createAcl(oi);
		}
		
		Sid sid = (isPrincipal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
		for (Permission perm : perms)
			acl.insertAce(acl.getEntries().size(), perm, sid, true);
		
		if (setOwner)
			acl.setOwner(sid);
		
		((MutableAclService) aclService).updateAcl(acl);
	}
	public void createAParentAclForChild(Long parentObjectId, Class parentClass, Long securedObjectId, Class childClass)
	{
		MutableAcl parentAcl = getAclForObject(parentClass, parentObjectId);
		
		ObjectIdentity oi = new ObjectIdentityImpl(childClass, securedObjectId);
		//Create or update the relevant child object ACL
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = ((MutableAclService) aclService).createAcl(oi);
		}
		if (parentAcl != null)
		{
			acl.setParent(parentAcl);
			acl.setEntriesInheriting(true);
			acl.setOwner(parentAcl.getOwner());
		}
		((MutableAclService) aclService).updateAcl(acl);
	}
	public void deleteAcl(Long securedObjectId, Class clazz) {
		ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), securedObjectId);
		((MutableAclService) aclService).deleteAcl(oid, true);
		
	}
	public boolean isOwner(Long securedObjectId, Class clazz, String sid)
	{
		boolean isOwner = false;
		
		if (!StringUtils.isEmpty(sid))
		{
			ObjectIdentity oid = new ObjectIdentityImpl(clazz.getName(), securedObjectId);
			MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
			Sid ownerSid = acl.getOwner();
			if (ownerSid != null && ownerSid instanceof PrincipalSid)
				isOwner = sid.equals(((PrincipalSid) ownerSid).getPrincipal());
		}
		
		return isOwner;
	}
}
