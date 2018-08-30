package com.rs.app.acl;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "Input data to add/update permission")
public class PermissionInput implements Serializable {
	private static final long serialVersionUID = -3285822203547807963L;
	@ApiModelProperty(name = "objectType", dataType="String", required = true, example="Site")
	private String objectType;
	@ApiModelProperty(name = "securedObjectId", dataType="int", required = true, example="1")
	private int securedObjectId;
	@ApiModelProperty(name = "recipient", dataType="string", required = true, example="ramesh/ROLE_USER")
	private String recipient;
	@ApiModelProperty(name = "principal", dataType="boolean", required = true, example="true/false")
	private boolean principal;
	@ApiModelProperty(name = "granting", dataType="boolean", required = true, example="true/false")
	private boolean granting;
	@ApiModelProperty(name = "permissions", dataType="list", required = true, example="READ,WRITE,CREATE,ADMINISTRATION")
	private List<String> permissions;
}
