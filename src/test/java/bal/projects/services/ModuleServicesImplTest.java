package bal.projects.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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

import bal.projects.entities.Level;
import bal.projects.entities.Module;
import bal.projects.entities.Option;
import bal.projects.repositories.ModuleRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModuleServicesImplTest {
	
  private final static Logger log = Logger.getLogger(UserServicesImplTest.class);

  @InjectMocks
  ModuleServicesImpl moduleServices = Mockito.mock(ModuleServicesImpl.class);
  
  @Mock
  ModuleRepository moduleRepository = Mockito.mock(ModuleRepository.class);
  
  Option opt = Option.builder().id((long)1).dateModification(new Date()).name("INFINI").dateCreation(new Date()).build();
  
   @SuppressWarnings("serial")
Module mod = Module.builder().ects(12).hoursNbr(21).level(Level.ANNEE_4).name("Spring boot").ue("ue").up("up")
		   .options(new ArrayList<Option>() {{add(opt);}}).build();
	
	@SuppressWarnings("serial")
	List<Module> listModules = new ArrayList<Module>() {
		{
			add(mod);
			add(Module.builder().ects(12).hoursNbr(21).level(Level.ANNEE_4).name("Spring boot22").ue("ue").up("up").build());
		}
	};
	
	@Test
	@Order(0)
	public void testAddModule() {
		Mockito.when(moduleRepository.save(Mockito.any(Module.class))).then(invocation -> {
			Module model = invocation.getArgument(0, Module.class);
			model.setId((long)1);
			return model;
		});
		log.info("Avant  :"+mod.toString());
		Module modu = moduleServices.addModule(mod);
		assertNotSame(modu, mod);
		log.info("Apres  :"+mod.toString());
	}
	
	@Test
	@Order(1)
	public void testFindAll() {
		Mockito.when(moduleRepository.findAll()).thenReturn(listModules);
		List<Module> modules = moduleServices.findAllModules();
		assertNotNull(modules);
		for (Module module : modules) {
			log.info("Module  :"+module.toString());
		}
	}
}
