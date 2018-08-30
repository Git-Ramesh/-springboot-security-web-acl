package com.rs.app;

import static org.mockito.Mockito.mock;

import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rs.app.acl.AclServiceImpl;
import com.rs.app.model.Topic;
import com.rs.app.repository.TopicRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class DeleteTest {
	@Autowired
	private AclServiceImpl aclService;
	@Autowired
	private TopicRepository topicRepository;
	Authentication auth = new TestingAuthenticationToken("ramesh", "1234", "ROLE_ADMIN");
	AclAuthorizationStrategy authzStrategy;
	PermissionGrantingStrategy pgs;
	AuditLogger mockAuditLogger;

	@Before
	public void setUp() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(auth);
		authzStrategy = mock(AclAuthorizationStrategy.class);
		mockAuditLogger = mock(AuditLogger.class);
//		pgs = new DefaultPermissionGrantingStrategy(mockAuditLogger);
		auth.setAuthenticated(true);
	}

	@After
	public void tearDown() throws Exception {
		SecurityContextHolder.clearContext();
	}

	//@Test
	//@Ignore
	public void testDeleteAces() {
		/*List<String> permissions = new ArrayList<>();
		System.out.println("================================================");
		System.out.println("Is Authen: "+ SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
		aclService.updatePermission1(1, "Topic", permissions);
		System.out.println("================================================");
		System.out.println("Is Authen: "+ SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
		System.out.println("=================");
		System.out.println("DONE");
		System.out.println("=================");*/
		Sid sid = new PrincipalSid("jaga");
		aclService.deletePermission(new Topic(2), sid, BasePermission.READ, Topic.class);
	}
	@Test
	@Ignore
	public void testsGetPermissionsTest() {
		System.out.println(aclService.permissionsOnNode(1, "Topic"));
	}
	@Test
	//@Ignore
	public void testInsertACE() {
		//Topic topic = topicRepository.getTopicById(4);
		//aclService.addCummulativePermission(topic, "ramesh", false, Arrays.asList("WRITE","ADMINISTRATION"));
		//aclService.addCummulativePermission(topic, recipient, principal, permissions, granting, setParent, setOwner, inherit);
		//aclService.addCummulativePermission("Topic",4, "ROLE_USER", false, Arrays.asList("READ","WRITE"), true, false, true, false);
		aclService.addCummulativePermission("Topic",2, "ramesh", true, Arrays.asList("READ","WRITE","ADMINISTRATION"), true, false, true, false);
	}
	@Test
	@Ignore
	public void testDeleteACE() {
		Topic topic = topicRepository.getTopicById(4);
		//aclService.addCummulativePermission(topic, "ramesh", false, Arrays.asList("WRITE","ADMINISTRATION"));
		//aclService.addCummulativePermission(topic, recipient, principal, permissions, granting, setParent, setOwner, inherit);
//		aclService.deletePermissions(securedObjectType, securedObjectId, recipient, principal, permissions);
		aclService.deletePermissions("Topic", 4, "ROLE_USER", false, Arrays.asList("READ"));
	}
	@Test
	@Ignore
	public void testUpdateACE() {
		Topic topic = topicRepository.getTopicById(4);
		//aclService.addCummulativePermission(topic, "ramesh", false, Arrays.asList("WRITE","ADMINISTRATION"));
		//aclService.addCummulativePermission(topic, recipient, principal, permissions, granting, setParent, setOwner, inherit);
//		aclService.deletePermissions(securedObjectType, securedObjectId, recipient, principal, permissions);
		//aclService.deletePermissions("Topic", 4, "ROLE_USER", false, Arrays.asList("READ"));
		aclService.updatePermissions("Topic", 12, "ROLE_USER", false, Arrays.asList("READ"),true);
	}
	@Test
	@Ignore
	public void testPermissionsOnACL() {
		Topic topic = topicRepository.getTopicById(4);
		//aclService.addCummulativePermission(topic, "ramesh", false, Arrays.asList("WRITE","ADMINISTRATION"));
		//aclService.addCummulativePermission(topic, recipient, principal, permissions, granting, setParent, setOwner, inherit);
//		aclService.deletePermissions(securedObjectType, securedObjectId, recipient, principal, permissions);
		//aclService.deletePermissions("Topic", 4, "ROLE_USER", false, Arrays.asList("READ"));
		//aclService.updatePermissions("Topic", 4, "ROLE_USER", false, Arrays.asList("READ", "WRITE", "DELETE","ADMINISTRATION"),true);
		System.out.println(aclService.permissionsOnEntity(4, "Topic"));
	}
}
