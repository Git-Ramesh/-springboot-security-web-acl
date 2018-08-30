package com.rs.app;

import com.rs.app.service.TopicService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TopicServiceTest {
    AuditLogger mockAuditLogger;
    Authentication auth = new TestingAuthenticationToken("jaga", "1234", "ROLE_USER");
    @Autowired
    private TopicService topicService;

    @Before
    public void setUp() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
//        authzStrategy = mock(AclAuthorizationStrategy.class);
        mockAuditLogger = mock(AuditLogger.class);
//		pgs = new DefaultPermissionGrantingStrategy(mockAuditLogger);
        auth.setAuthenticated(true);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetAllTopics() {
        System.out.println(topicService.getAllTopics());
    }
}

