package bal.projects.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bal.projects.entities.ERole;
import bal.projects.entities.Role;
import bal.projects.entities.User;
import bal.projects.payload.request.LoginRequest;
import bal.projects.payload.request.SignupRequest;
import bal.projects.payload.request.ValidationRequest;
import bal.projects.payload.response.JwtResponse;
import bal.projects.payload.response.MessageResponse;
import bal.projects.repositories.RoleRepository;
import bal.projects.repositories.UserRepository;
import bal.projects.security.jwt.JwtUtils;
import bal.projects.security.services.UserDetailsImpl;
import bal.projects.services.INotificationServices;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger l = Logger.getLogger(AuthController.class);
	
	private static final String MESSAGE4_ROLE_ERROR = "Error: role not found";
	
	private static final String COOKIE_NAME_4EMAIL = "email";
	
	private static final String COOKIE_NAME_4RECOVERY = "forgot-password";
	
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	INotificationServices notificationService;
	
	@PostMapping("/login")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = new ArrayList<>();
		try {
			roles = userDetails.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
		} catch (Exception e) {
			l.error(e);
		}
		
		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles,
												 userDetails.getProfile(),
												 userDetails.getStatus()));
	}
	
	
	
	@PostMapping("/register")
	public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signUpRequest,HttpServletResponse httpResponse) {
		
		if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}
		if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}
		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getLastname(),signUpRequest.getFirstname());
		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_ETUDIANT)
					.orElseThrow(() -> new RuntimeException(MESSAGE4_ROLE_ERROR));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "comite_organisation":
					Role adminRole = roleRepository.findByName(ERole.ROLE_COMITE_ORGANISATION)
							.orElseThrow(() -> new RuntimeException(MESSAGE4_ROLE_ERROR));
					roles.add(adminRole);
					break;
				case "responsable_option":
					Role responsableRole = roleRepository.findByName(ERole.ROLE_RESPONSABLE_OPTION)
							.orElseThrow(() -> new RuntimeException(MESSAGE4_ROLE_ERROR));
					roles.add(responsableRole);
					break;
				case "enseignant":
					Role enseignantRole = roleRepository.findByName(ERole.ROLE_ENSEIGNANT)
							.orElseThrow(() -> new RuntimeException(MESSAGE4_ROLE_ERROR));
					roles.add(enseignantRole);
					break;
				default:
					Role etudiantRole = roleRepository.findByName(ERole.ROLE_ETUDIANT)
							.orElseThrow(() -> new RuntimeException(MESSAGE4_ROLE_ERROR));
					roles.add(etudiantRole);
				}
			});
		}
		user.setDateCreation(new Date());
		user.setProfile("default-profile.jpg");
		user.setStatus(false);
		user.setRoles(roles);
		
		try {
			notificationService.sendMailWithCode(user,false);
		} catch (MessagingException e) {
			l.debug("Failed to reach this "+user.getEmail()+" email address!" + e.getMessage());
			return ResponseEntity.badRequest().body(new MessageResponse("We failed to reach this email address check and try again!"));
		}
		this.setCookie(httpResponse,COOKIE_NAME_4EMAIL, signUpRequest.getEmail());
		
		userRepository.save(user);
	
		
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	
	
	@PostMapping("/forgot-password")
	public ResponseEntity<Object> forgotPassword(@Valid @RequestBody SignupRequest signUpRequest ,HttpServletResponse httpResponse){
		User user = userRepository.findByUsernameOrEmail(signUpRequest.getEmail());
		if(user != null && user.getStatus()) {
			try {
				notificationService.sendMailWithCode(user,true);
			} catch (MessagingException e) {
				l.info(e);
			}
			this.setCookie(httpResponse,COOKIE_NAME_4RECOVERY, user.getEmail());
	    	return ResponseEntity.ok(new MessageResponse("Email confirmation successfully!"));
	    	
	    }else if(user != null && !user.getStatus()){
	    	try {
				notificationService.sendMailWithCode(user,false);
				this.setCookie(httpResponse,COOKIE_NAME_4EMAIL, signUpRequest.getEmail());
			} catch (MessagingException e) {
				l.info("Failed to reach this "+user.getEmail()+" email address!" + e.getMessage());
			}
	    	return ResponseEntity.badRequest().body(new MessageResponse("Please confirm your Email Address!"));
	    }else {
	    
	    	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is non-existent!"));
	    }
	}
	
	
	
	@PutMapping("/code-validation")
	public ResponseEntity<Object> validCode(@Valid @RequestBody ValidationRequest validation,HttpServletRequest request,HttpServletResponse httpResponse){
		Cookie[] cookies = request.getCookies();
		String cookieName=COOKIE_NAME_4RECOVERY;
		String email = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
	    if (cookies != null && email !=null) {
	        
	        User user = userRepository.findByUsernameOrEmail(jwtUtils.getUserNameFromJwtToken(email));
	        if(user.getCode().equals(validation.getCode())) {
	        	user.setCode(0);
	        	userRepository.save(user);
	        	this.setCookie(httpResponse,"reset-confirmation", "ok");
	        	return ResponseEntity.ok(new MessageResponse("User validation successfully!"));
	        }else {
	        	return ResponseEntity
						.badRequest()
						.body(new MessageResponse("Unauthorized: Code is invalid!"));
	        }
	    }else {
	    	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Unauthorized: operation failed!!"));
	    }  
	}
	
	
	@PutMapping("/reset-password")
	public ResponseEntity<Object> resetPassword(@Valid @RequestBody SignupRequest signUpRequest,HttpServletRequest request,HttpServletResponse httpResponse) {
		Cookie[] cookies = request.getCookies();
		String cookieName=COOKIE_NAME_4RECOVERY;
		String email = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
	    if (cookies != null && email !=null) {
	        
	        User user = userRepository.findByUsernameOrEmail(jwtUtils.getUserNameFromJwtToken(email));
	        
	        
	        	user.setPassword(encoder.encode(signUpRequest.getPassword()));
		        if(user.getStatus() == null || !user.getStatus()) {
		        	user.setStatus(true);
		        }
		        user.setDateModification(new Date());
		        userRepository.save(user);
		        this.deleteCookie(httpResponse, COOKIE_NAME_4RECOVERY);
		        this.deleteCookie(httpResponse, "reset-confirmation");
		    	return ResponseEntity.ok(new MessageResponse("User confirmation successfully!"));
	        
	        
	    }else {
	    	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Unauthorized: check you Email Address!"));
	    }
	}
	
	
	
	@GetMapping("/confirm")
	public ResponseEntity<Object> confirmation(HttpServletRequest request,HttpServletResponse httpResponse){
		Cookie[] cookies = request.getCookies();
		String cookieName=COOKIE_NAME_4EMAIL;
		Boolean confirmation = false; 
	    if (cookies != null) {
	        String email = Arrays.stream(cookies)
	                .filter(c -> c.getName().equals(cookieName))
	                .findFirst()
	                .map(Cookie::getValue)
	                .orElse(null);
	        
	        User user = userRepository.findByUsernameOrEmail(jwtUtils.getUserNameFromJwtToken(email));
	        if(user !=null) {
	        	confirmation = true;
	        	user.setDateModification(new Date());
	        	user.setStatus(true);
	        	userRepository.save(user);
	        }
	    }else {
	    	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: not cookie!"));
	    }
	    
	    if(Boolean.TRUE.equals(confirmation)) {
	    	this.deleteCookie(httpResponse, COOKIE_NAME_4EMAIL);
	    	return ResponseEntity.ok(new MessageResponse("User confirmation successfully!"));
	    	
	    }else {
	    	return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is non-existent!"));
	    }
	}
	
	
	
	public void setCookie(HttpServletResponse response,String cookieName,String cookieValue) {
	
		Cookie cookie = new Cookie(cookieName,jwtUtils.generateTokenForCookie(cookieValue));
		cookie.setMaxAge(1 * 24 *60 *60); // expires in 1 Days
		cookie.setPath("/"); // global cookie accessible every where
		response.addCookie(cookie);
	}
	
	
	
	public void deleteCookie(HttpServletResponse response,String name) {
		
		Cookie cookie = new Cookie(name,null);
		cookie.setMaxAge(0); 
		cookie.setPath("/"); // global cookie accessible every where
		response.addCookie(cookie);
	}
}
