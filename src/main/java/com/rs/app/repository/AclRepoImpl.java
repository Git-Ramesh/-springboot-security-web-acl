package com.rs.app.repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rs.app.model.AclClass;
import com.rs.app.model.AclEntry;
import com.rs.app.model.AclObjectIdentity;
import com.rs.app.model.AclSid;


@Repository
public class AclRepoImpl implements AclRepo {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<ObjectIdentity> findChildren(Serializable identifier, String type) {
		Query query = entityManager.createQuery(
				"select aoi from AclObjectIdentity aoi, AclObjectIdentity parent, AclClass aclClass where aoi.parentObject = parent and aoi.objIdClass = aclClass and parent.objIdIdentity = :objIdIdentity and parent.objIdClass = (select acl FROM AclClass acl where acl.clazz = :clazz)");
		query.setParameter("objIdIdentity", identifier);
		query.setParameter("clazz", type);

		return (List<ObjectIdentity>) query.getResultList();
	}

	@Override
	public AclObjectIdentity getObjectIdentity(String type, Serializable identifier) {
		Query query = entityManager.createQuery(
				"select aoi from AclObjectIdentity aoi, AclClass aclClass where  aoi.objIdIdentity = :objIdIdentity and aoi.objIdClass = aclClass and aclClass.clazz = :clazz)");
		query.setParameter("objIdIdentity", identifier);
		query.setParameter("clazz", type);

		return (AclObjectIdentity) query.getSingleResult();
	}

	@Override
	@Transactional
	public void createObjectIdentity(AclObjectIdentity identity) {
		entityManager.persist(identity);
	}

	@Override
	public List<AclSid> findAclSidList(Boolean principal, String sidName) {
		Query query = entityManager
				.createQuery("select sid from AclSid sid where sid.principal=:principal and sid.sid=:sid");
		query.setParameter("principal", principal);
		query.setParameter("sid", sidName);
		return query.getResultList();
	}

	@Override
	@Transactional
	public AclSid createAclSid(AclSid sid) {
		entityManager.persist(sid);
		return sid;
	}

	@Override
	public List<AclClass> findAclClassList(String type) {
		Query query = entityManager.createQuery("select clazz from AclClass clazz where clazz.clazz=:clazz");

		query.setParameter("clazz", type);
		return query.getResultList();
	}

	@Override
	@Transactional
	public AclClass createAclClass(AclClass clazz) {
		entityManager.persist(clazz);
		return clazz;
	}

	@Override
	@Transactional
	public void deleteEntries(AclObjectIdentity objectIdentity) {
		objectIdentity = entityManager.find(AclObjectIdentity.class, objectIdentity.getId());
		if (objectIdentity.getEntries() != null) {
			for (AclEntry entry : objectIdentity.getEntries()) {
				entityManager.remove((AclEntry) entry);
			}
		}

	}

	@Override
	public void deleteObjectIdentity(AclObjectIdentity oidPrimaryKey) {
		entityManager.remove(oidPrimaryKey);
	}

	@Override
	@Transactional
	public void createEntries(List<AclEntry> entries) {
		for (AclEntry entry : entries) {
			entityManager.persist(entry);
		}

	}

	@Override
	public boolean updateObjectIdentity(AclObjectIdentity aclObject) {
		entityManager.merge(aclObject);
		return true;
	}

	@Override
	public AclSid findAclSid(String principal) {
		Query query = entityManager.createQuery("select sid from AclSid sid where sid.sid=:sid");

		query.setParameter("sid", principal);
		List<AclSid> results = query.getResultList();
		if (results.size() > 0) {
			return results.get(0);
		}
		
		return null;
	}

	@Override
	public List<Long> getObjectSecuredIdentities(Class<?> clazz) {
		String query = "select ae.id from AclEntry ae where ae.aclObjectIdentity in ( select aoi.id from AclObjectIdentity aoi \n" + 
				"where aoi.objIdClass  = (select ac.id from AclClass ac where ac.clazz=:qualifiedClassName))";
//		String query = "select id from acl_entry where acl_object_identity in ( select id from acl_object_identity \n" + 
//				"where object_id_class  = (select id from acl_class where class=:qualifiedClassName));";
		return entityManager.createQuery(query).setParameter("qualifiedClassName", clazz.getCanonicalName()).getResultList();
	}
	public void updateMask(int mask, long identifier) {
		Query UPDATE_MASK_QURY = entityManager.createQuery("UPDATE AclEntry AS ae SET ae.mask=:mask WHERE ae.id=:id");
		UPDATE_MASK_QURY.setParameter("mask", mask);
		UPDATE_MASK_QURY.setParameter("id", identifier);
	}
	public List<BigInteger> getDistinctObjdentities() {
		String GET_DISTINCT_ACL_OBJECT_IDENTITY = "SELECT DISTINCT(acl_object_identity) FROM acl_entry";
		return entityManager.createNativeQuery(GET_DISTINCT_ACL_OBJECT_IDENTITY).getResultList();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AclEntry> getACEList(long securedObjectIdentity) {
		System.out.println("securedObjectIdentity: " + securedObjectIdentity);
		String JPQL_GET_ALL_ACEs_OF_SECURED_OBJECT_IDENTITY = "FROM AclEntry ae WHERE ae.aclObjectIdentity.objIdIdentity=:securedObjectIdentity";
		return entityManager.createQuery(JPQL_GET_ALL_ACEs_OF_SECURED_OBJECT_IDENTITY, AclEntry.class)
			.setParameter("securedObjectIdentity", securedObjectIdentity)
			.getResultList();
	}

}