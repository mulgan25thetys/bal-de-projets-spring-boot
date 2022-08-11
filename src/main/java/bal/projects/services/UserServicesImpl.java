package bal.projects.services;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import bal.projects.controllers.FileController;
import bal.projects.entities.ERole;
import bal.projects.entities.Option;
import bal.projects.entities.Role;
import bal.projects.entities.User;
import bal.projects.repositories.OptionRepository;
import bal.projects.repositories.RoleRepository;
import bal.projects.repositories.UserRepository;

@Service
public class UserServicesImpl implements IUserServices{

	private final Path root = Paths.get("src/main/resources/uploads/profiles");
	
	private static final Logger log = Logger.getLogger(UserServicesImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private OptionRepository optionRepository;

	@Transactional
	public User addUser(User user) {
		user.setProfile("default-profile.jpg");
		user.setStatus(false);
		user.setDateCreation(new Date());
		user.setPassword(new BCryptPasswordEncoder().encode(user.getIdentifiant()));
		
		if(user.getRoles().isEmpty()) {
			Role role = roleRepository.findByName(ERole.ROLE_ETUDIANT).orElse(null);
			user.getRoles().add(role);
		}
		
		User addedUser = userRepository.save(user);
		
		if(user.getOption() !=null) {
			Option opt = user.getOption();
			opt.setDateModification(new Date());
			opt.setResponsable(addedUser);
			optionRepository.save(opt);
		}
		return addedUser;
	}
	
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	@Transactional
	public User editUser(User user) {
		if(user.getOption() != null) {//dans le cas d'option
			Option currentOption = optionRepository.getOptionForThisResponsable(user.getId());
			if(currentOption != null && currentOption != user.getOption()) {
				currentOption.setResponsable(null);
				optionRepository.save(currentOption);
			}
			Option option = user.getOption();
			option.setResponsable(user);
			optionRepository.save(option);
		}
		
		user.setDateModification(new Date());
		
		return userRepository.save(user);
	}

	@Override
	public User editProfile(MultipartFile profile,Long idUser){
		User user = userRepository.findById(idUser).orElse(null);
		String filename = "";
		
		if(user !=null && profile !=null) {
			Optional<String> extension = Optional.ofNullable(profile.getOriginalFilename()).filter(f -> f.contains("."))
				      .map(f -> f.substring(profile.getOriginalFilename().lastIndexOf('.') + 1));
			if(extension.isPresent()) {
				filename = user.getUsername()+new Date().getTime()+"."+extension.get();
			}
			
			try {
			   Files.copy(profile.getInputStream(), this.root.resolve(filename));
			} catch (Exception e) {
				log.info("Could not store the file. Error: " + e.getMessage());
			    
			}
			user.setProfile(filename);
		}
		
		if(user != null) {
			user.setDateModification(new Date());
			return userRepository.save(user);
		}
		
		return user;
	}
	
	@Override
	public String getProfile(String filename) {
		String profileUrl = "";
		if(filename != null) {
			Resource profile = this.load(filename);
			try {
				if(profile != null) {
					profileUrl = MvcUriComponentsBuilder
					          .fromMethodName(FileController.class, "getFileForProfile", profile.getFilename()).build().toString();
				}
			} catch (Exception e) {
				log.debug(e);
			}
		}
		
		return profileUrl;
	}
	
	@Override
	public User getUser(Long id) {
		return userRepository.findById(id).orElse(null);
		
	}

	@Override
	public User changePassword(Long id, String newPassword) {
		User user = userRepository.findById(id).orElse(null);
		if(user !=null) {
			user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
			user.setDateModification(new Date());
			return userRepository.save(user);
		}
		return null;
	}

	@Override
	public User affectTeacherAsJury(Long id) {
		User user = userRepository.findById(id).orElse(null);
		
		if(user != null) {
			Optional<Role> role = roleRepository.findByName(ERole.ROLE_ENSEIGNANT);
			
				if(role.isPresent() && user.getRoles().contains(role.get())) {
					user.setIsJury(this.changeUserJuryStatus(user.getIsJury()));
					user.setDateModification(new Date());
					user = userRepository.save(user);
					return user;
				}
		}
		return user;
	}
	
	private Boolean changeUserJuryStatus(Boolean juryStatus) {
		return juryStatus == null || !juryStatus;
	}

	private Resource load(String filename) {
	    try {
	      Path file = root.resolve(filename);
	      Resource resource = new UrlResource(file.toUri());
	      if (resource.exists() || resource.isReadable()) {
	        return resource;
	      } else {
	    	  log.info("could not read file");
	    	  return null;
	      }
	    } catch (MalformedURLException e) {
	      log.debug(e);
	      return null;
	    }	    
	  }
	
	@Override
	public List<User> affectStudentsToGroup() {
		return userRepository.findAll();
	}

}
