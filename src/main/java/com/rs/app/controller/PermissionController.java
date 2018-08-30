package com.rs.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rs.app.acl.AclServiceImpl;
import com.rs.app.acl.AddPermissionInput;
import com.rs.app.acl.PermissionInput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@Api(value = "PermissionController", description = "Handles the permissions related operations")
public class PermissionController {
	@Autowired
	private AclServiceImpl aclService;
	
	@PostMapping("/addPermission")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@ApiOperation(value = "Adds the ACL permission to principal or authority om entity", produces = "application/json", consumes ="application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "NotFound"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
	public ResponseEntity<String> addPermission(@RequestBody AddPermissionInput input) {
		aclService.addCummulativePermission(input.getObjectType(), input.getSecuredObjectId(), input.getRecipient(), input.isPrincipal(), input.getPermissions(), input.isGranting(), input.isSetParent(), input.isSetOwner(), input.isInherit());
		return ResponseEntity.ok().body("success");
	}
	@PostMapping("/updatePermission")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@ApiOperation(value = "Updates the ACL permission of give principal or authority on secured entity object", produces = "application/json", consumes ="application/json")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "NotFound"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
	public ResponseEntity<String> updatePermission(@RequestBody PermissionInput input) {
		aclService.updatePermissions(input.getObjectType(), input.getSecuredObjectId(), input.getRecipient(), input.isPrincipal(), input.getPermissions(), input.isGranting());
		return ResponseEntity.ok().body("success");
	}
}
