package com.rs.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.acls.domain.PermissionFactory;

import com.rs.app.acl.AclServiceImpl;
import com.rs.app.repository.AclRepo;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan(basePackages = "com.rs.app.model")
@EnableSwagger2
public class SpringbootSecurityWebAclApplication implements CommandLineRunner {
	@Autowired
	private AclRepo aclRepo;
	@Autowired
	private PermissionFactory permissionFactory;
	@Autowired
	private AclServiceImpl aclService;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootSecurityWebAclApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*Map<Long, Map<String, Set<String>>> map = new HashMap<>();
		List<BigInteger> securedObjectIds = aclRepo.getDistinctObjdentities();
		for(BigInteger oid : securedObjectIds) {
			long id = oid.longValue();
			List<AclEntry> entries = aclRepo.getACEList(id);
			Map<String, Set<String>> inMap = new HashMap<>();
			for(AclEntry ace : entries) {
				String sid = ace.getSid().getSid();
				if(!inMap.containsKey(sid)) {
					inMap.put(sid, new HashSet<>());
					inMap.get(sid).add(getPermissionFromMask(ace.getMask()));
				} else {
					inMap.get(sid).add(getPermissionFromMask(ace.getMask()));
				}
				System.out.println(inMap);
				map.put(id, inMap);
				System.out.println("===========");
			}
		}
		System.out.println(map);*/
		/*System.out.println("===========================");
		System.out.println(aclService.permissionsOnNode(2, "topic"));
		System.out.println("===========================");*/
		/*Authentication auth = new TestingAuthenticationToken("joe", "ignored",
				"ROLE_ADMINISTRATOR");
		List<String> permissions = new ArrayList<>();
		aclService.updatePermission1(2, "Topic", permissions);
		System.out.println("=================");
		System.out.println("DONE");
		System.out.println("=================");*/
	}

	public String getPermissionFromMask(int mask) {
		String permission = "";
		System.out.println("Mask: " + mask);
		switch (mask) {
		case 1:
			permission = "READ";
			break;
		case 2:
			permission = "WRITE";
			break;
		case 4:
			permission = "CREATE";
			break;
		case 8:
			permission = "DELETE";
			break;
		case 16:
			permission = "ADMINISTRATION";
			break;
		}
		return permission;
	}
	/*@Bean
	public InternalResourceViewResolver viewResolver() {
	    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
	    viewResolver.setViewClass(JstlView.class);
	    viewResolver.setPrefix("/WEB-INF/views/");
	    viewResolver.setSuffix(".jsp");
	    return viewResolver;
	}*/
}
