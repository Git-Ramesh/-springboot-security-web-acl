package com.rs.app.acl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.rs.app.model.AbstractSecureObject;
import com.rs.app.model.Topic;
import com.rs.app.repository.AclRepo;
import com.rs.app.repository.TopicRepository;

@Service
public class AclServiceImpl implements AclService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AclService.class);
	@Autowired
	private MutableAclService aclService;
	@Autowired
	private AclRepo aclRepo;
	@Autowired
	private PermissionFactory permissionFactory;
	@Autowired
	private TopicRepository topicRepository;
	// @Autowired
	// private PermissionEvaluator permissionEvaluator;
	//

	// @Override
	@Transactional
	public void createAclAndGrantAccess(Object obj, String recipient, boolean principal, int[] masks, boolean setOwner,
			boolean inherit, boolean hasParent) {

		MutableAcl acl = null;
		final List<Permission> permissions = new ArrayList<>();
		for (int mask : masks) {
			permissions.add(permissionFactory.buildFromMask(mask));
		}
		ObjectIdentity oid = new ObjectIdentityImpl(obj);
		try {
			acl = (MutableAcl) aclService.readAclById(oid);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oid);
		}

		Sid aclSid = (principal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
		if (hasParent) {
			Object parent = null;

			try {
				Method method = obj.getClass().getMethod("getParent", new Class[] {});
				parent = method.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (parent != null) {
				MutableAcl parentAcl = this.getOrCreateAcl(parent);
				acl.setParent(parentAcl);
			}

			Object inheritPermissions = false;

			try {
				Method method = obj.getClass().getMethod("isPermissionsInherited", new Class[] {});
				inheritPermissions = method.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (inheritPermissions != null) {
				acl.setEntriesInheriting((boolean) inheritPermissions);
			}
		}
		for (Permission permission : permissions) {
			acl.insertAce(acl.getEntries().size(), permission, aclSid, true);
		}
		if (setOwner) {
			// acl.setOwner(aclSid);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				if (auth.getPrincipal() instanceof UserDetails) {
					acl.setOwner(new PrincipalSid(((UserDetails) auth.getPrincipal()).getUsername()));
				} else {
					acl.setOwner(new PrincipalSid(auth.getPrincipal().toString()));
				}
			}
		}
		// Changes an existing <code>Acl</code> in the database.
		aclService.updateAcl(acl);
	}

	// Final
	@Transactional
	public void updateAclPermission(Object obj, int[] masks, String recipient, boolean principal) {
		System.out.println(obj);
		final List<Permission> permissions = new ArrayList<>();
		for (int mask : masks) {
			permissions.add(permissionFactory.buildFromMask(mask));
		}
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(obj);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
		}
		List<AccessControlEntry> entries = acl.getEntries();
		Sid aclSid = (principal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
		int i = 0;
		/*
		 * for (AccessControlEntry entry : entries) {
		 * 
		 * System.out.println("ID " + ((Long)entry.getId()).intValue() +" Sid: "
		 * +entry.getSid()+" Granting  "+
		 * entry.isGranting()+" Mask "+entry.getPermission().getMask());
		 * acl.updateAce(((Long)entry.getId()).intValue(), permissions.get(i)); i++;
		 * 
		 * // acl.updateAce(entries.size(), BasePermission.CREATE); //
		 * System.out.println("ID " + ((Long) entry.getId()).intValue() + " Sid: " +
		 * entry.getSid() + " Granting  " // + entry.isGranting() + " Mask " +
		 * entry.getPermission().getMask()); // aclRepo.updateMask(masks[i], (long)
		 * entry.getId()); String _aclSid = ""; if (entry.getSid() instanceof
		 * GrantedAuthoritySid) { GrantedAuthoritySid gaSid = (GrantedAuthoritySid)
		 * entry.getSid(); _aclSid = gaSid.getGrantedAuthority(); } else { PrincipalSid
		 * pSid = (PrincipalSid) entry.getSid(); _aclSid = pSid.getPrincipal(); }
		 * if(recipient.equals(aclSid)) { //acl.updateAce(1, permissions.get(i));
		 * //acl.updateAce(i, permissions.get(i));
		 * acl.insertAce(acl.getEntries().size(), permissions.get(i), aclSid, true); } }
		 */
		for (Permission perm : permissions) {
			acl.insertAce(acl.getEntries().size(), perm, aclSid, true);
		}
		aclService.updateAcl(acl);
	}

	/*
	 * @Override public void deleteAclPermissionToSid(Class<?> objectType, Long
	 * securedObjectId, String sid) { System.out.println(objectType);
	 * System.out.println(securedObjectId); System.out.println(sid); if
	 * (!StringUtils.isEmpty(sid)) { MutableAcl acl =
	 * getMutableAclFromSecuredObjectId(objectType, securedObjectId);
	 * List<AccessControlEntry> entries = acl.getEntries(); if (entries != null) {
	 * int i = 0; for (AccessControlEntry entry : entries) { String aclSid = ""; if
	 * (entry.getSid() instanceof GrantedAuthoritySid) { GrantedAuthoritySid gaSid =
	 * (GrantedAuthoritySid) entry.getSid(); aclSid = gaSid.getGrantedAuthority(); }
	 * else { PrincipalSid pSid = (PrincipalSid) entry.getSid(); aclSid =
	 * pSid.getPrincipal(); } if (sid.equals(aclSid)) {
	 * System.out.println("*******   " + i); acl.deleteAce(i); } else i++; }
	 * aclService.updateAcl(acl); } } }
	 */
	// FINAL
	public void deleteAclPermissionToSid(Object entity, String sid) {
		System.out.println(sid);
		if (!StringUtils.isEmpty(sid)) {
			ObjectIdentity boi = new ObjectIdentityImpl(entity);
			MutableAcl acl = null;
			try {
				acl = (MutableAcl) aclService.readAclById(boi);
			} catch (NotFoundException nfe) {
				nfe.printStackTrace();
			}
			List<AccessControlEntry> entries = acl.getEntries();
			if (entries != null) {
				int i = 0;
				for (AccessControlEntry entry : entries) {
					LOGGER.info("Processing ACE: " + entry.getId());
					String aclSid = "";
					LOGGER.info("Available ACEs for given object: {} " + entries.size());
					if (entry.getSid() instanceof GrantedAuthoritySid) {
						GrantedAuthoritySid gaSid = (GrantedAuthoritySid) entry.getSid();
						aclSid = gaSid.getGrantedAuthority();
					} else {
						PrincipalSid pSid = (PrincipalSid) entry.getSid();
						aclSid = pSid.getPrincipal();
					}
					if (sid.equals(aclSid)) {
						acl.deleteAce(i);
					} else
						i++;
				}
				aclService.updateAcl(acl);
			}
		}
	}

	public boolean isOwner(Long securedObjectId, Class<?> clazz, String sid) {
		boolean isOwner = false;

		if (!StringUtils.isEmpty(sid)) {
			ObjectIdentity oid = new ObjectIdentityImpl(clazz.getName(), securedObjectId);
			MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
			Sid ownerSid = acl.getOwner();
			if (ownerSid != null && ownerSid instanceof PrincipalSid)
				isOwner = sid.equals(((PrincipalSid) ownerSid).getPrincipal());
		}

		return isOwner;
	}

	public void updateObjectOwner(Long securedObjectId, Class<?> objectType, String sid, boolean principal) {
		MutableAcl acl = getMutableAclFromSecuredObjectId(objectType, securedObjectId);
		Sid aclSid = (principal) ? new PrincipalSid(sid) : new GrantedAuthoritySid(sid);
		acl.setOwner(aclSid);
		aclService.updateAcl(acl);
	}

	@Override
	public void deleteAcl(Class<?> objectType, Long securedObjectId) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectType.getCanonicalName(), securedObjectId);
		aclService.deleteAcl(oid, true);
	}

	public Map<Long, Map<String, String>> getPermissionsFromAclEntry(Class<?> clazz) {
		final Map<Long, Map<String, String>> map = new HashMap<>();
		List<Long> ids = aclRepo.getObjectSecuredIdentities(clazz);
		for (Long identifier : ids) {
			MutableAcl acl = getMutableAclFromSecuredObjectId(clazz, identifier);
			List<AccessControlEntry> entries = acl.getEntries();
			for (AccessControlEntry entry : entries) {
				Sid aclsid = entry.getSid();
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

	protected MutableAcl getMutableAclFromSecuredObjectId(final Class<?> objectType, final Serializable identifier) {
		ObjectIdentity oid = new ObjectIdentityImpl(objectType, identifier);
		MutableAcl acl = null;

		try {
			acl = (MutableAcl) aclService.readAclById(oid);
		} catch (NotFoundException nfe) {
			LOGGER.error(nfe.getMessage());
		}
		return acl;
	}

	/*
	 * protected MutableAcl getNewMutableAclFromSecuredObjectIdentity(final Class<?>
	 * objectType, final Serializable identifier) { MutableAcl acl = null;
	 * ObjectIdentity oid = acl = aclService.createAcl(objectIdentity); }
	 */
	private MutableAcl getOrCreateAcl(Object obj) {

		ObjectIdentity oi = null;
		if (obj instanceof ObjectIdentity) {
			oi = (ObjectIdentity) obj;
		} else {
			oi = new ObjectIdentityImpl(obj);
		}
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = ((MutableAclService) aclService).createAcl(oi);
		}
		return acl;

	}

	public void doCreate(Object obj, String recipient, boolean principal) {
		MutableAcl acl = aclService.createAcl(new ObjectIdentityImpl(obj));
		Sid sid = (principal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
		acl.insertAce(0, BasePermission.CREATE, sid, true);
		aclService.updateAcl(acl);
	}

	public void listAces(Object obj) {
		final List<Permission> permissions = new ArrayList<>();

		ObjectIdentity objectIdentity = new ObjectIdentityImpl(obj);
		System.out.println("ObjectIdentity: " + objectIdentity.getIdentifier());
		MutableAcl acl = getOrCreateAcl(obj);
		List<AccessControlEntry> entries = acl.getEntries();
		System.out.println(entries.size());
		for (AccessControlEntry entry : entries) {
			System.out.println("ID " + entry.getId() + " Sid: " + entry.getSid() + " Granting" + entry.isGranting()
					+ " Mask" + entry.getPermission().getMask());
		}
	}

	@Override
	public void createAclAndGrantAccess(Object obj, String recipient, boolean principal, int[] masks,
			boolean setOwner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAclPermission(Object obj, int[] masks) {
		// TODO Auto-generated method stub

	}

	// Verified
	public Map<String, Set<String>> permissionsOnNode(long securedObjectId, String nodeType) {
		ObjectIdentity objectIdentity = null;
		MutableAcl acl = null;
		final Map<String, Set<String>> sidPermMap = new HashMap<>();
		Class<?> javaType = getJavaType(nodeType);
		objectIdentity = new ObjectIdentityImpl(javaType, securedObjectId);
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
		}
		if (acl != null) {
			acl.getEntries().forEach(ace -> {
				Sid _sid = ace.getSid();
				String sid = "";
				if (_sid instanceof PrincipalSid) {
					sid = ((PrincipalSid) _sid).getPrincipal();
				} else {
					sid = ((GrantedAuthoritySid) _sid).getGrantedAuthority();
				}
				String perm = getStringPermissionFromMask(ace.getPermission().getMask());
				if (!sidPermMap.containsKey(sid)) {
					Set<String> s = new HashSet<>();
					s.add(perm);
					sidPermMap.put(sid, s);
				} else {
					sidPermMap.get(sid).add(perm);
				}

			});
		}
		return sidPermMap;
	}

	// Verified
	private Class<?> getJavaType(String nodeType) {
		Class<?> clazz = null;
		if (!StringUtils.isEmpty(nodeType)) {
			if (nodeType.equalsIgnoreCase("Topic")) {
				clazz = Topic.class;
			}
		}
		return clazz;
	}

	// Verified
	private String getStringPermissionFromMask(int mask) {
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
		return permission;
	}

	public void updatePermission1(long securedObjectId, String nodeType, List<String> permissions) {
		ObjectIdentity objectIdentity = null;
		MutableAcl acl = null;
		Class<?> javaType = getJavaType(nodeType);
		objectIdentity = new ObjectIdentityImpl(javaType, securedObjectId);
		final List<Permission> permissionList = new ArrayList<>();
		// convert permissions from string to ACL know format <code>Permission<code>
		permissions.forEach(permission -> {
			permissionList.add(permissionFactory.buildFromName(permission));
		});
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
		} catch (NotFoundException nfe) {
			nfe.printStackTrace();
		}

		if (acl != null) {
			List<AccessControlEntry> entries = acl.getEntries();
			/*
			 * int atIndex = 0; for (AccessControlEntry ace : entries) { acl.deleteAce(2);//
			 * Delete the AccessControlEntry in memory atIndex++; }
			 */
			acl.deleteAce(2);
			// aclService.deleteAcl(objectIdentity, true);
			aclService.updateAcl(acl);
			// aclService.readAclsById(objects, sids)
			// for(Permission perm : permissionList) {
			// inse
			// }
		}
	}

	public void deletePermission(AbstractSecureObject securedObject, Sid recipient, Permission permission,
			Class clazz) {
		System.out.println("==============================");
		System.out.println(securedObject);
		ObjectIdentity oid = new ObjectIdentityImpl(clazz.getCanonicalName(), securedObject.getId());
		MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

		// Remove all permissions associated with this particular recipient (string
		// equality used to keep things simple)
		List<AccessControlEntry> entries = acl.getEntries();
		for (int i = 0; i < entries.size(); i++) {

			if (entries.get(i).getSid().equals(recipient) && entries.get(i).getPermission().equals(permission)) {
				System.out.println("Match found to delete");
				System.out.println("Deleting ace");
				acl.deleteAce(i);
			}
		}

		aclService.updateAcl(acl);

		LOGGER.debug("Deleted securedObject " + securedObject + " ACL permissions for recipient " + recipient);
	}

	// 15-Aug
	/**
	 * Adds the permissions for given Sid on entity
	 * 
	 * @param entity
	 *            Entity object to apply acl
	 * @param recipient
	 *            {@link PrincipalSid} or {@link GrantedAuthoritySid} owning
	 *            permissions
	 * @param principal
	 *            If recipient is {@link PrincipalSid} then true otherwise set to
	 *            false
	 * @param permissions
	 *            Permissions you are giving
	 * @param granting
	 *            Are you granting permissions
	 * @param setParent
	 *            Parent ACL for this ACL
	 * @param setOwner
	 *            Set the owner for this ACL
	 * @param inherit
	 *            Inherit permissions
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	public void addCummulativePermission(Object entity, String recipient, boolean principal, List<String> permissions,
			boolean granting, boolean setParent, boolean setOwner, boolean inherit) {
		LOGGER.info("Adding CummulativePermission");
		MutableAcl acl = null;
		ObjectIdentity objectIdentity = null;
		if ((permissions == null) || (permissions.isEmpty())) {
			LOGGER.error("No permissions found");
			throw new IllegalArgumentException("Please provide permissions");
		}
		objectIdentity = new ObjectIdentityImpl(entity);
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
			LOGGER.info("Using existing MutableAcl");
		} catch (NotFoundException nfe) {
			LOGGER.info("Creating new MutableAcl");
			acl = aclService.createAcl(objectIdentity);
		}
		addCummulativePermission(acl, entity.getClass(), recipient, principal, permissions, granting, setParent,
				setOwner, inherit);
	}

	// 15-Aug
	/**
	 * Adds the permissions for given Sid on entity
	 * 
	 * @param securedObjectType
	 *            Entity class name on which ACL is applied/applying
	 * @param nodeId
	 *            Entity Object Id (Entity object related table primary key)
	 * @param recipient
	 *            {@link PrincipalSid} or {@link GrantedAuthoritySid} owning
	 *            permissions
	 * @param principal
	 *            If recipient is {@link PrincipalSid} then true otherwise set to
	 *            false
	 * @param permissions
	 *            Permissions you are giving
	 * @param granting
	 *            Are you granting permissions
	 * @param setParent
	 *            Parent ACL for this ACL
	 * @param setOwner
	 *            Set the owner for this ACL
	 * @param inherit
	 *            Inherit permissions
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	@Transactional
	public void addCummulativePermission(String securedObjectType, int securedObjectId, String recipient, boolean principal,
			List<String> permissions, boolean granting, boolean setParent, boolean setOwner, boolean inherit) {
		LOGGER.info("Adding CummulativePermission");
		MutableAcl acl = null;
		ObjectIdentity objectIdentity = null;
		Class<?> javaType = getJavaType(securedObjectType);

		if ((permissions == null) || (permissions.isEmpty())) {
			LOGGER.error("No permissions found");
			throw new IllegalArgumentException("Please provide permissions");
		}
		System.out.println("JavaType: " + javaType);
		System.out.println("ObjectIdentity: " + securedObjectType);
		objectIdentity = new ObjectIdentityImpl(securedObjectType, securedObjectId);
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
			LOGGER.info("Using existing MutableAcl");
		} catch (NotFoundException nfe) {
			LOGGER.info("Creating new MutableAcl");
			acl = aclService.createAcl(objectIdentity);
		}
		addCummulativePermission(acl, javaType, recipient, principal, permissions, granting, setParent, setOwner,
				inherit);
	}

	/**
	 * Adds the given permissions for recipient on acl
	 * 
	 * @param acl
	 *            MutableAcl of ObjectIdentity
	 * @param clazz
	 *            Entity type
	 * @param recipient
	 *            {@link PrincipalSid} or {@link GrantedAuthoritySid} owning
	 *            permissions
	 * @param principal
	 *            If recipient is {@link PrincipalSid} then true otherwise set to
	 *            false
	 * @param permissions
	 *            Permissions you are giving
	 * @param granting
	 *            Are you granting permissions
	 * @param setParent
	 *            Parent ACL for this ACL
	 * @param setOwner
	 *            Set the owner for this ACL
	 * @param inherit
	 *            Inherit permissions
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	// 15-Aug
	public void addCummulativePermission(MutableAcl acl, Class<?> clazz, String recipient, boolean principal,
			List<String> permissions, boolean granting, boolean setParent, boolean setOwner, boolean inherit) {
		if (acl != null) {
			CumulativePermission cp = new CumulativePermission();
			if ((permissions == null) || (permissions.isEmpty())) {
				LOGGER.error("No permissions found");
				throw new IllegalArgumentException("Please provide permissions");
			}
			// Prepare CumulativePermission
			permissions.forEach(permission -> cp.set(getPermissionObject(permission)));
			Sid sid = (principal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
			List<AccessControlEntry> entries = acl.getEntries();
			LOGGER.info("Entries available for ACL: {}", entries.size());
			acl.insertAce(entries.size(), cp, sid, granting);
			LOGGER.info("ACE added to mem");
			if (setOwner) {
				LOGGER.info("Setting Owner for ACL");
				acl.setOwner(new PrincipalSid(getUsername()));
			}
			if (setParent) {
				Object parentObjectIdentity = null;

				try {
					Method method = clazz.getMethod("getParent", new Class[] {});
					parentObjectIdentity = method.invoke(clazz);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (parentObjectIdentity != null) {
					MutableAcl parentAcl = null;
					if (parentObjectIdentity instanceof ObjectIdentity) {
						try {
							parentAcl = (MutableAcl) aclService.readAclById((ObjectIdentity) parentObjectIdentity);
						} catch (NotFoundException nfe) {
							LOGGER.error("Parent ACL not found!");
							System.out.println(nfe.getMessage());
							nfe.printStackTrace();
						}
						if (parentAcl != null) {
							LOGGER.info("Setting parent acl");
							acl.setParent(parentAcl);
						}
					}
				}
			}

			if (inherit) {
				Object isInherit = null;

				try {
					Method method = clazz.getMethod("isPermissionsInherited", new Class[] {});
					isInherit = method.invoke(clazz);
				} catch (Exception e) {
					System.out.println("Error in getting inherit from " + clazz.getClass().getName());
					e.printStackTrace();
				}
				if (isInherit != null) {
					LOGGER.info("Setting inherit");
					acl.setEntriesInheriting((boolean) isInherit);
				}
			}
			LOGGER.info("Updating mem changes to DB");
			aclService.updateAcl(acl);
		}
	}

	//15-Aug
	/**
	 * Updates the particular sid permissions by 
	 * removing old permissions and adds new given list of permissions
	 * @param securedObjectType
	 * @param securedObjectId
	 * @param recipient
	 * @param principal
	 * @param permissions
	 * @param granting
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	public void updatePermissions(String securedObjectType, int securedObjectId, String recipient, boolean principal,
			List<String> permissions, boolean granting) {
		LOGGER.info("updatePermissions");
		MutableAcl acl = null;
		ObjectIdentity objectIdentity = null;
		Class<?> javaType = getJavaType(securedObjectType);

		if ((permissions == null) || (permissions.isEmpty())) {
			LOGGER.error("No permissions found");
			throw new IllegalArgumentException("Please provide permissions");
		}

		objectIdentity = new ObjectIdentityImpl(javaType, securedObjectId);
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
			LOGGER.info("Using existing MutableAcl");
		} catch (NotFoundException nfe) {
			LOGGER.info("No ACL's available for given nodeType: {} with nodeId: " + securedObjectType, securedObjectId);
		}
		if (acl != null) {
			deletePermissions(securedObjectType, securedObjectId, recipient, principal, permissions);
			addCummulativePermission(acl, javaType, recipient, principal,permissions, granting, false, false, false);
			LOGGER.info("Permissions updated");
		}
	}
	//15-Aug
	/**
	 * Delete the perticular sid ACE from ACL
	 * @param securedObjectType
	 * @param securedObjectId
	 * @param recipient
	 * @param principal
	 * @param permissions
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	public void deletePermissions(String securedObjectType, int securedObjectId, String recipient, boolean principal,
			List<String> permissions) {
		ObjectIdentity oid = new ObjectIdentityImpl(getJavaType(securedObjectType).getCanonicalName(), securedObjectId);
		MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
		Sid sid = (principal) ? new PrincipalSid(recipient) : new GrantedAuthoritySid(recipient);
		// Remove all permissions associated with this particular recipient
		List<AccessControlEntry> entries = acl.getEntries();
		List<Permission> perms = permissions.stream().map(permission -> getPermissionObject(permission))
				.collect(Collectors.toList());
		CumulativePermission cp =new CumulativePermission();
		//Prepare CumulativePermission
		perms.forEach(perm -> cp.set(perm));
		//for (Permission perm : perms) {
			for (int i = 0; i < entries.size(); i++) {
				//if (entries.get(i).getSid().equals(sid) && entries.get(i).getPermission().equals(cp)) {
				if (entries.get(i).getSid().equals(sid) ) {
					acl.deleteAce(i);
				}
			}
		//}
		aclService.updateAcl(acl);
		LOGGER.info("Deleted securedObject " + securedObjectId + " ACL permissions for recipient " + recipient);
	}
	//15-Aug
	/**
	 * Get the all permissions corresponds to each sid on securedObjectIdentity
	 * @param securedObjectId
	 * @param securedObjectType
	 * @return
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	public Map<String, List<String>> permissionsOnEntity(int securedObjectId, String securedObjectType) {
		ObjectIdentity objectIdentity = null;
		MutableAcl acl = null;
		Map<String, List<String>> sidPermMap = null;
		Class<?> javaType = getJavaType(securedObjectType);
		objectIdentity = new ObjectIdentityImpl(javaType, securedObjectId);
		try {
			acl = (MutableAcl) aclService.readAclById(objectIdentity);
		} catch (NotFoundException nfe) {
			LOGGER.error("Acl not found for object id: {}", securedObjectId);
			nfe.printStackTrace();
		}
		if (acl != null) {
			sidPermMap = permissions1(acl);
		}
		return sidPermMap;
	}
	// 15-Aug
	/**
	 * Gets the authenticated username
	 * 
	 * @return authenticated username
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	private String getUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth.getPrincipal() instanceof UserDetails) {
			return ((UserDetails) auth.getPrincipal()).getUsername();
		} else {
			return auth.getPrincipal().toString();
		}
	}

	// 15-Aug
	/**
	 * Get the available permissions of Sid from given MutableAcl
	 * 
	 * @param sid
	 *            Sid of the principal or authority
	 * @param acl
	 *            MutableAcl on which get the permissions
	 * @return list of permissions
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	private List<String> permissions(Sid sid, MutableAcl acl) {
		List<String> convertedPermissions = null;
		if (acl != null && sid != null) {
			List<AccessControlEntry> entries = acl.getEntries();
			for (AccessControlEntry entry : entries) {
				if (entry.getSid().equals(sid)) {
					String permissionPattern = entry.getPermission().getPattern();
					List<Character> permCharacters = permissionPattern.chars().mapToObj(c -> (char) c)
							.collect(Collectors.toList());
					permCharacters.removeIf(c -> c == '.');
					convertedPermissions = permCharacters.stream().map(c -> {
						String permission = null;
						if ('C' == c) {
							permission = "CREATE";
						} else if ('D' == c) {
							permission = "DELETE";
						} else if ('R' == c) {
							permission = "READ";
						} else if ('W' == c) {
							permission = "WRITE";
						} else if ('A' == c) {
							permission = "ADMINISTRATION";
						}
						return permission;
					}).collect(Collectors.toList());
				}
			}
		}
		return convertedPermissions;
	}
	//15-Aug
	/**
	 * Gets all available sid permissions on give acl
	 * @param acl
	 * @return sid and its permissions
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	private Map<String, List<String>> permissions1(MutableAcl acl) {
		final Map<String, List<String>> sidPermMap = new HashMap<>();
		if (acl != null) {
			List<AccessControlEntry> entries = acl.getEntries();
			for (AccessControlEntry entry : entries) {
				List<String> convertedPermissions = null;
				Sid _sid = entry.getSid();
				String sid = "";
				if (_sid instanceof PrincipalSid) {
					sid = ((PrincipalSid) _sid).getPrincipal();
				} else {
					sid = ((GrantedAuthoritySid) _sid).getGrantedAuthority();
				}
				String permissionPattern = entry.getPermission().getPattern();
				List<Character> permCharacters = permissionPattern.chars().mapToObj(c -> (char) c)
						.collect(Collectors.toList());
				permCharacters.removeIf(c -> c == '.');
				convertedPermissions = permCharacters.stream().map(c -> {
					String permission = null;
					if ('C' == c) {
						permission = "CREATE";
					} else if ('D' == c) {
						permission = "DELETE";
					} else if ('R' == c) {
						permission = "READ";
					} else if ('W' == c) {
						permission = "WRITE";
					} else if ('A' == c) {
						permission = "ADMINISTRATION";
					}
					return permission;
				}).collect(Collectors.toList());
				sidPermMap.put(sid, convertedPermissions);
			}
		}
		return sidPermMap;
	}
	//15-Aug
	/**
	 * Converts the given string permission to {@link BasePermission}
	 * @param permission
	 * @return {@link BasePermission}
	 * @author Ramesh
	 * @since 2018-08-15
	 */
	public Permission getPermissionObject(String permission) {
		Permission perm = null;
		if (!StringUtils.isEmpty(permission)) {
			if ("WRITE".equalsIgnoreCase(permission)) {
				perm = BasePermission.WRITE;
			} else if ("CREATE".equalsIgnoreCase(permission)) {
				perm = BasePermission.CREATE;
			} else if ("DELETE".equalsIgnoreCase(permission)) {
				perm = BasePermission.DELETE;
			} else if ("READ".equalsIgnoreCase(permission)) {
				perm = BasePermission.READ;
			} else if ("ADMINISTRATION".equalsIgnoreCase(permission)) {
				perm = BasePermission.ADMINISTRATION;
			}
		}
		return perm;
	}
}
