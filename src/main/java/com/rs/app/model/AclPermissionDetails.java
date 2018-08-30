package com.rs.app.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AclPermissionDetails {
	private Integer objectId;
	private String target;
	@NotNull
	private List<String> permissions;
	private Boolean princiapl;
	private Boolean inheritPermissions;
	private String authority;
	private Boolean granting;
}
