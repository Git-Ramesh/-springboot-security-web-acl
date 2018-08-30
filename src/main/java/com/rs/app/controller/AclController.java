package com.rs.app.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rs.app.acl.AclServiceImpl;
import com.rs.app.constant.AclObjectType;
import com.rs.app.model.AclCreateInfo;
import com.rs.app.model.Topic;
import com.rs.app.repository.TopicRepository;

@RestController
public class AclController {
	@Autowired
	private AclServiceImpl aclService;
	@Autowired
	private TopicRepository topicRepository;
	@PostMapping("/create")
	public String createAclAndGrantAccess(@RequestBody AclCreateInfo createInfo) {
		System.out.println(createInfo);
		/*List<String> rawPermissions = createInfo.getPermissions();
		final List<Permission> permissions = new ArrayList<>();
		rawPermissions.forEach(perm -> {
			if (perm.equalsIgnoreCase("READ")) {
				permissions.add(BasePermission.READ);
			} else if (perm.equalsIgnoreCase("WRITE")) {
				permissions.add(BasePermission.WRITE);
			} else if (perm.equalsIgnoreCase("ADMIN") || perm.equalsIgnoreCase("ADMINISTRATION")) {
				permissions.add(BasePermission.ADMINISTRATION);
			} else if (perm.equalsIgnoreCase("CREATE")) {
				permissions.add(BasePermission.CREATE);
			} else if (perm.equalsIgnoreCase("DELETE")) {
				permissions.add(BasePermission.DELETE);
			}
		});*/
		//Class<?> objectT = AclObjectType.getObjectType(createInfo.getObjectType());
		Topic t = topicRepository.getTopicById(createInfo.getObjectid());
		aclService.createAclAndGrantAccess(t, createInfo.getRecipient(), createInfo.isPrincipal(), createInfo.getMasks(), createInfo.isSetOwner());
		return "Success";
	}
	@GetMapping("/all")
	public Map<Long, Map<String,String>> getPermissionsFromAclEntry() {
		return aclService.getPermissionsFromAclEntry(Topic.class);
	}
	@GetMapping("/delete/{clazz}/{oid}/{sid}")
	public String deleteAclPermission(@PathVariable("clazz") String clz, @PathVariable("sid") String sid, @PathVariable("oid")Long oid) {
		Class<?> objectT = AclObjectType.getObjectType(clz);
		System.out.println(objectT);
		//aclService.deleteAclPermissionToSid(objectT, oid, sid);
		return "ok";
	}
	@GetMapping("/update/{id}/{masks}")
	public String updatePermission(@PathVariable("id") int id, @PathVariable("masks") int[] masks) {
		System.out.println("Id: " +id);
		System.out.println("Masks: " + Arrays.toString(masks));
		Topic t = topicRepository.getTopicById(id);
		//aclService.updateAclPermission(objectT, oid, BasePermission.WRITE);
		aclService.updateAclPermission(t, masks);
		return "ok";
	}
	@GetMapping("/do/{id}")
	public void doCreate(@PathVariable("id") int id) {
		/*System.out.println(createInfo);
		Topic t = topicRepository.getTopicById(createInfo.getObjectid());
		aclService.doCreate(t, createInfo.getRecipient(), true);*/
		Topic t = topicRepository.getTopicById(id);
		aclService.listAces(t);
	}
}
