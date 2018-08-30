package com.rs.app.repository;

import com.rs.app.model.User;

public interface UserRepository {
	User getActiveUser(String username);
}
