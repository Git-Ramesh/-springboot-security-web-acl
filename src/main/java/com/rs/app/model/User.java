package com.rs.app.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
	private static final long serialVersionUID = -7714538896502164425L;
	@Id
	@Column(name="USERNAME",length=50,nullable=false)
	private String username;
	@Column(name="PASSWORD",length=100,nullable=false)
	private String password;
	@Column(name="EMAIL",length=38, nullable=false, unique = true)
	private String email;
	@Column(name="ROLE",length=38, nullable=false)
	private String role;
	@Column(name="COUNTRY",length=30) 
	private String country;
	@Column(name="ENABLED") 
	private Integer enabled;
	
}
