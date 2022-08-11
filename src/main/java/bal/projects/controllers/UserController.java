package bal.projects.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import bal.projects.entities.User;
import bal.projects.payload.request.LoginRequest;
import bal.projects.payload.response.MessageResponse;
import bal.projects.repositories.UserRepository;
import bal.projects.services.IUserServices;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	IUserServices userService;

	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION')")
	@GetMapping("/list-all")
	@ResponseBody
	public List<User> findAll() {
		return userService.findAll();
	}
	
	@SuppressWarnings("all")
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@PutMapping("/edit-user")
	@ResponseBody
	public ResponseEntity<Object> editUser(@Valid @RequestBody User usr)
	{
		
		if (Boolean.TRUE.equals(userRepository.existsByUsername(usr.getUsername()))) {
			User u = userRepository.findByUsernameOrEmail(usr.getUsername());
			if(!u.getId().equals(usr.getId()))
			{
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: Username is already taken!"));
			}
		}
		
		if(Boolean.TRUE.equals(userRepository.existsByEmail(usr.getEmail()))) {
			User u = userRepository.findByUsernameOrEmail(usr.getEmail());
			if (!u.getId().equals(usr.getId())) {
				return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Error: Email is already in use!"));
			}
		}
		return ResponseEntity.ok().body(userService.editUser(usr));
	}
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@PutMapping("/edit-profile/{id}")
	@ResponseBody
	public User editProfile(@RequestParam("profile") MultipartFile profile,@PathVariable("id") Long id)
	{
		return userService.editProfile(profile,id);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@GetMapping("/get-user/{id}")
	@ResponseBody
	public User getUser(@PathVariable("id") Long id) {
		return userService.getUser(id);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@GetMapping("/get-profile/{filename}")
	@ResponseBody
	public MessageResponse getProfile(@PathVariable("filename") String filename) {
		return new MessageResponse(userService.getProfile(filename));
	}
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@PutMapping("/change-password/{id}")
	@ResponseBody
	public User changePassword(@PathVariable("id") Long id,@RequestBody LoginRequest req)
	{
		return userService.changePassword(id, req.getPassword());
	}
	
	@PreAuthorize("hasAnyRole('ROLE_COMITE_ORGANISATION')")
	@PutMapping("/affect-jury/{id}")
	@ResponseBody
	public User affecterTeacherAsJury(@PathVariable("id") Long id)
	{
		return userService.affectTeacherAsJury(id);
	}
	
	@SuppressWarnings("all")
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Object> addUser(@RequestBody User u)
	{
		if (Boolean.TRUE.equals(userRepository.existsByUsername(u.getUsername()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}
		if (Boolean.TRUE.equals(userRepository.existsByEmail(u.getEmail()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}
		if (userRepository.existsByIdentifiant(u.getIdentifiant())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Identifiant is already taken!"));
		}
		return ResponseEntity.ok().body(userService.addUser(u));
	}
}
