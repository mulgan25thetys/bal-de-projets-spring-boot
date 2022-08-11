package bal.projects.services;



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;

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
import bal.projects.entities.Level;
import bal.projects.entities.Role;
import bal.projects.entities.Speciality;
import bal.projects.entities.User;
import bal.projects.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServicesImplTest {
	
	private final static Logger log = Logger.getLogger(UserServicesImplTest.class);
	
	@InjectMocks
	UserServicesImpl userService = Mockito.mock(UserServicesImpl.class);
	
	@InjectMocks
	NotificationServicesImpl notifService = Mockito.mock(NotificationServicesImpl.class);
	
	@Mock
	UserRepository  userRepository = Mockito.mock(UserRepository.class);
	
	Role role = Role.builder().name(ERole.ROLE_ETUDIANT).id(1).build();
	@SuppressWarnings("serial")
	Set<Role> listRole = new HashSet<Role>() {
		{
			add(role);
		}
	};
	
	User usr = User.builder().className("4INFINI1").dateModification(new Date()).email("mayelemay25@gmail.com").username("name0")
			.firstname("Morgand").lastname("general").identifiant("idede255").isJury(false).level(Level.ANNEE_4)
			.myOption(null).meanPI(0).profile("default-profile.jpg").speciality(Speciality.INFORMATIQUE)
			.roles(listRole).build();
	
	@SuppressWarnings("serial")
	List<User> users = new ArrayList<User>() {
		{
			add(User.builder().id((long)1).className("4INFINI1").dateModification(new Date()).email("theyysmayhh@gmail.com").username("name1")
					.firstname("mlflfl").lastname("gfjhfhf").identifiant("ensjhfjhfkj").isJury(false).level(Level.ANNEE_4)
					.option(null).meanPI(modCount).speciality(Speciality.INFORMATIQUE).roles(listRole).build());
			add(User.builder().id((long)2).className(null).dateModification(new Date()).email("theyysmayhh@gmail.com")
					.firstname("mlflfl").lastname("gfjhfhf").identifiant("jhfjhfkj").isJury(true).level(null)
					.option(null).meanPI(0).speciality(null).roles(new HashSet<Role>() {
						{
							add(Role.builder().id(2).name(ERole.ROLE_ENSEIGNANT).build());
						}
					}).build());
			add(User.builder().id((long)3).className(null).dateModification(new Date()).email("theyysresponsablemayhh@gmail.com").username("name2")
					.firstname("mlflfl").lastname("gfjhfhf").identifiant("resjhfjhfkj").isJury(true).level(null)
					.option(null).meanPI(0).speciality(null).roles(new HashSet<Role>() {
						{
							add(Role.builder().id(3).name(ERole.ROLE_RESPONSABLE_OPTION).build());
						}
					}).build());
			add(User.builder().id((long)4).className(null).dateModification(new Date()).email("theyycomitesmayhh@gmail.com").username("name3")
					.firstname("mlflfl").lastname("gfjhfhf").identifiant("comjhfjhfkj").isJury(true).level(null)
					.option(null).meanPI(0).speciality(null).roles(new HashSet<Role>() {
						{
							add(Role.builder().id(4).name(ERole.ROLE_COMITE_ORGANISATION).build());
						}
					}).build());
		}
	};
	
	@Test
	@Order(1)
//	@RepeatedTest(3) 
	public void testAddUser() {
		Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> {
            User model = invocation.getArgument(0, User.class);
            model.setId((long)1);
            return model;
        });
        log.info("Avant ==> " + usr.toString());
        User user = userService.addUser(usr);
        assertNotSame(user, usr);
        log.info("Après ==> " + usr.toString());
	}
	
	@Test
	@Order(2)
//	@RepeatedTest(5) 
	public void testGetUser() {
		
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usr));
		User usermodel = userService.getUser((long)1);
		assertNull(usermodel);
//		log.info("Get :"+usermodel.toString());
//		verify(userRepository.findById(Mockito.anyLong()).get());
	}
	
	@Test
	@Order(3)
	public void testFindAll() {
		Mockito.when(userRepository.findAll()).thenReturn(users);
		List<User> listUsers = userService.findAll();
		assertNotNull(listUsers);
		for (User user : listUsers) {
			log.info(user.toString());
		}
	}
	
	@Test
	@Order(4)
	public void testAffect_teacher_as_jury() {
		Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> {
			User enseignant = invocation.getArgument(1, User.class);
			enseignant.setId((long)2);
			enseignant.setIsJury(true);
			return enseignant;
		});
		log.info("Affecter enseignant Avant ==> " + usr.toString());
        User user = userService.affectTeacherAsJury(usr.getId());
        assertNotSame(user, usr);
        log.info("Affecter enseignant Après ==> " + usr.toString());
		
	}
	
	@Test
	@Order(5)
	public void testChange_password() {
		Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation -> {
			User model = invocation.getArgument(0, User.class);
			model.setId((long)3);
			return model;
		});
		log.info("Changer mot de passe Avant ==> " + usr.toString());
        User user = userService.changePassword(usr.getId(),"newPassword");
        assertNotSame(user, usr);
        log.info("Changer mot de passe Après ==> " + usr.toString());
	}
	
	@Test
	@Order(6)
	public void testEditUser() {
		
		Mockito.when(userRepository.save(Mockito.any(User.class))).then(invocation ->{
			User model = invocation.getArgument(3, User.class);
			model.setId((long)4);
			model.setEmail("emailcomitte@gmail.com");
			model.setFunction("fonction");
			return model;
		});
		
		log.info("Modifier user Avant ==> " + usr.toString());
        User user = userService.editUser(usr);
        assertNotSame(user, usr);
        log.info("Modifier user Après ==> " + usr.toString());
	}
	
	@Test
	@Order(7)
	public void testGetProfile() {
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usr));
		String profileUrl = userService.getProfile(usr.getProfile());
		assertNull(profileUrl);
	}
	
	@Test
	@Order(8)
	public void testsendMailWithCode() throws MessagingException {
		Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(usr));
		User usermodel = userService.getUser((long)1);
		assertNull(usermodel);
//		notifService.sendMailWithCode(usermodel, true);	
	}
}