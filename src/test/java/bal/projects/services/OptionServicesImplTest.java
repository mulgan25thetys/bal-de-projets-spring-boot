package bal.projects.services;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

import bal.projects.entities.ERole;
import bal.projects.entities.Option;
import bal.projects.entities.Role;
import bal.projects.entities.User;
import bal.projects.repositories.OptionRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OptionServicesImplTest {

	private final static Logger log = Logger.getLogger(UserServicesImplTest.class);
	
	@InjectMocks
	OptionServicesImpl optionServices = Mockito.mock(OptionServicesImpl.class);
	
	@Mock
	OptionRepository optionRepository = Mockito.mock(OptionRepository.class);
	
	@SuppressWarnings("serial")
	User responsable = User.builder().className(null).dateModification(new Date()).email("theyysresponsablemayhh@gmail.com").username("name2")
			.firstname("mlflfl").lastname("gfjhfhf").identifiant("resjhfjhfkj").isJury(true).level(null)
			.option(null).meanPI(0).speciality(null).roles(new HashSet<Role>() {
				{
					add(Role.builder().id(3).name(ERole.ROLE_RESPONSABLE_OPTION).build());
				}
			}).build();
	
	Option opt = Option.builder().dateModification(new Date()).name("INFINI").dateCreation(new Date()).build();
	
	@SuppressWarnings("serial")
	List<Option> listOptions = new ArrayList<Option>() {
		{
			add(opt);
			add(Option.builder().name("TWIN").dateCreation(new Date()).dateModification(new Date()).build());
		}
	};
	
	int optionSize = listOptions.size();
	
	@Test
	public void testAddOption() {
		Mockito.when(optionRepository.save(Mockito.any(Option.class))).then(invocation -> {
			Option model = invocation.getArgument(0, Option.class);
			model.setId((long)1);
			return model;
		});
		log.info("Avant ==> " + opt.toString());
        Option option = optionServices.addOption(opt,responsable.getId());
        assertNotSame(option, opt);
        log.info("Après ==> " + opt.toString());
	}
	
	@Test
	public void testEditOption() {
		Mockito.when(optionRepository.save(Mockito.any(Option.class))).then(invocation -> {
			Option model = invocation.getArgument(0, Option.class);
			model.setId((long)1);
			model.setResponsable(responsable);
			model.setName("SEA");
			return model;
		});
		log.info("Modification Avant ==> " + opt.toString());
        Option option = optionServices.editOption(opt);
        assertNotSame(option, opt);
        log.info("Modification Après ==> " + opt.toString());
		
	}
	
	@Test
	public void testDeleteOption() {
		Mockito.when(optionRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(opt));
		optionServices.deleteOption((long)1);
		Option model = optionServices.getOption((long)1);
		assertNull(model);
	}
	
	@Test
	public void testGetOption() {
		Mockito.when(optionRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(opt));
		Option optn = optionServices.getOption((long)2);
		assertNull(optn);
//		log.info("Get Option :"+optn.toString());
	}
	
	@Test
	public void testFindAll() {
		Mockito.when(optionRepository.findAll()).thenReturn(listOptions);
		List<Option> options = optionServices.findAll();
		assertNotNull(options);
		for (Option option : options) {
			log.info("Get option :"+option.toString());
		}
	}
}
