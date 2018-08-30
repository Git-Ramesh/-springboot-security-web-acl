package com.rs.app.acl;

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
@ApiModel(description = "Input data to add permission")
public class AddPermissionInput extends PermissionInput {
	private static final long serialVersionUID = -4419628642040492617L;
	@ApiModelProperty(name = "setParent", dataType="boolean", allowableValues="true or false", required = false)
	private boolean setParent;
	@ApiModelProperty(name = "setOwner", dataType="boolean", allowableValues="true or false", required=false)
	private boolean setOwner;
	@ApiModelProperty(name = "inherit", dataType="boolean", allowableValues="true or false", required=false)
	private boolean inherit;
}
