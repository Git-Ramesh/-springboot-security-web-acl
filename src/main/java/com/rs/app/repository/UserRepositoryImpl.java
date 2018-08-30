package com.rs.app.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.rs.app.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public User getActiveUser(String username) {
		User user = null;
		final short ENABLED = 1;
		final String HQL_QUERY = "SELECT u FROM User u WHERE u.username =?1 AND u.enabled =?2";
		List<?> list = entityManager.createQuery(HQL_QUERY)
			.setParameter(1, username)
			.setParameter(2, 1)
			.getResultList();
		if(!list.isEmpty()) {
			user = (User) list.get(0);
		}
		return user;
	}
}
