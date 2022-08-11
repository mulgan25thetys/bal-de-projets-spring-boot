package bal.projects.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bal.projects.entities.Notification;
import bal.projects.repositories.NotificationRepository;

@RestController
@RequestMapping("/notification")
public class NotificationController {

	@Autowired
	NotificationRepository notificationRepository;
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@GetMapping("/list-all")
	@ResponseBody
	public List<Notification> findAll(){
		return notificationRepository.findAll();
	}
}
