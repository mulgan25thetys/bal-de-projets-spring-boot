package bal.projects.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileStorageServicesImplTest {

	@InjectMocks
	FileStorageServicesImpl fileService = Mockito.mock(FileStorageServicesImpl.class);
	
	
	@Test
	@Order(1)
	public void testLoad() {
		assertNull(fileService.load("default-profile.jpg"));
	}
	
	@Test
	@Order(2)
	public void testLoadProfiles() {
		assertNull(fileService.loadProfiles("default-profile.jpg"));
	}
	
	@Test
	@Order(3)
	public void testLoadAll() {
		assertNotNull(fileService.loadAll());
	}
}
