package com.rs.app.acl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.util.Assert;

/**
 * {@link DefaultPermissionGrantingStrategy} doesn't have support to work with
 * {@link CumulativePermission}, it can only works with {@link BasePermission}
 * available, so to over come that we bring this
 * {@link RadiantSagePermissionGratingStrategry} in to an action, so you can
 * work with {@link CumulativePermission}
 *
 * @author Ramesh
 * @version 1.0
 * @since 2018-08-16
 */
public class RadiantSagePermissionGratingStrategry implements PermissionGrantingStrategy {
	private final transient AuditLogger auditLogger;

	public RadiantSagePermissionGratingStrategry(AuditLogger auditLogger) {
		Assert.notNull(auditLogger, "auditLogger cannot be null");
		this.auditLogger = auditLogger;
	}

	@Override
	public boolean isGranted(Acl acl, List<Permission> permission, List<Sid> sids, boolean administrativeMode) {
		final List<AccessControlEntry> aces = acl.getEntries();

		AccessControlEntry firstRejection = null;
		System.out.println("******************");
		System.out.println("Permission: "+ permission);
		System.out.println("sids: " + sids);
		System.out.println("administrativeMode: "+ administrativeMode);
		System.out.println("**********************");
		for (Permission p : permission) {
			for (Sid sid : sids) {
				System.out.println("Checking SID: " + sid);
				// Attempt to find exact match for this permission mask and SID
				boolean scanNextSid = true;

				for (AccessControlEntry ace : aces) {

					// Bit-wise comparison
					if (containsPermission(ace.getPermission().getMask(), p.getMask()) && ace.getSid().equals(sid)) {
						// Found a matching ACE, so its authorization decision will prevail
						if (ace.isGranting()) {
							// Success
							if (!administrativeMode) {
								auditLogger.logIfNeeded(true, ace);
							}

							return true;
						}

						// Failure for this permission, so stop search
						// We will see if they have a different permission
						// (this permission is 100% rejected for this SID)
						if (firstRejection == null) {
							// Store first rejection for auditing reasons
							firstRejection = ace;
						}

						scanNextSid = false; // helps break the loop

						break; // exit aces loop
					}
				}

				if (!scanNextSid) {
					break; // exit SID for loop (now try next permission)
				}
			}
		}

		if (firstRejection != null) {
			// We found an ACE to reject the request at this point, as no
			// other ACEs were found that granted a different permission
			if (!administrativeMode) {
				auditLogger.logIfNeeded(false, firstRejection);
			}

			return false;
		}

		// No matches have been found so far
		if (acl.isEntriesInheriting() && (acl.getParentAcl() != null)) {
			System.out.println("Entries inheriting..");
			// We have a parent, so let them try to find a matching ACE
			return acl.getParentAcl().isGranted(permission, sids, false);
		} else {
			// We either have no parent, or we're the uppermost parent
			throw new NotFoundException("Unable to locate a matching ACE for passed permissions and SIDs");
		}
	}

	private boolean containsPermission(int mask, int permission) {
		System.out.println("mask: " + mask);
		System.out.println("permission: " + permission);
		System.out.println("mask&permission " + (mask&permission));
		return (mask & permission) == permission;
	}
}
