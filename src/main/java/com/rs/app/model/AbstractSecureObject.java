package com.rs.app.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSecureObject {
	@Setter
	@Getter
	private long id;
}
