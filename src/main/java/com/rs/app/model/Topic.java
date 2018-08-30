package com.rs.app.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="TOPIC")
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Topic extends AbstractSecureObject implements Serializable {
	private static final long serialVersionUID = 4365115071415037704L;
	/*@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", length = 10)
	@Type(type = "int")*/
	//private Integer id;
	public Topic(long id) {
		super.setId(id);
	}
	@Column(name = "TITLE", length = 25)
	@Type(type = "string")
	private String title;
	@Column(name = "CATEGORY", length = 25)
	@Type(type = "string")
	private String category;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", length = 10)
	@Type(type = "long")
	public long getId() {
		return super.getId();
	}
	
}
