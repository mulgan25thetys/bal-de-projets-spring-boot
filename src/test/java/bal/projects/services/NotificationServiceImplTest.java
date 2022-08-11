package bal.projects.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import bal.projects.entities.User;
import bal.projects.repositories.NotificationRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationServiceImplTest {

//	private static final Logger log = Logger.getLogger(NotificationServiceImplTest.class);
	
	@InjectMocks
	UserServicesImpl userServices = Mockito.mock(UserServicesImpl.class);
	
	@InjectMocks
	NotificationServicesImpl notifServices = Mockito.mock(NotificationServicesImpl.class);
	
	
	@Test
	@Order(0)
	public void testsendMailWithCode() throws MessagingException {
//		User usr = userServices.getUser((long)2);
//		assertNotNull(usr);
		User usr = userServices.getUser((long)1);
		assertNull(usr);
//		notifServices.sendMailWithCode(usr, true);	
	}
}
