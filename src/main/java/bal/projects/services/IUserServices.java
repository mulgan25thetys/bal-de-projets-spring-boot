package bal.projects.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import bal.projects.entities.User;

public interface IUserServices {

	User editUser(User user);
	
	User editProfile(MultipartFile profile,Long idUser);
	
	String getProfile(String filename);
	
	User getUser(Long id);
	
	User changePassword(Long id,String newPassword);
	
	User affectTeacherAsJury(Long id);
	
	User addUser(User user);
	
	List<User> affectStudentsToGroup(); //plus tard avec le module gestion de groupe & projets

	List<User> findAll();
}
