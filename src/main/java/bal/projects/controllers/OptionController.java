package bal.projects.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bal.projects.entities.Module;
import bal.projects.entities.Option;
import bal.projects.payload.response.MessageResponse;
import bal.projects.repositories.ModuleRepository;
import bal.projects.repositories.OptionRepository;
import bal.projects.services.IModuleServices;
import bal.projects.services.IOptionServices;

@RestController
@RequestMapping("/options")
public class OptionController {

	@Autowired
	IOptionServices optionServices;
	
	@Autowired
	IModuleServices moduleServices;
	
	@Autowired
	OptionRepository optionRepository;
	
	@Autowired
	ModuleRepository moduleRepository;
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@GetMapping("/list-all")
	@ResponseBody
	public List<Option> findAll(){
		return optionServices.findAll();
	}
	
	@PreAuthorize("hasAnyRole('ROLE_RESPONSABLE_OPTION','ROLE_COMITE_ORGANISATION','ROLE_ETUDIANT','ROLE_ENSEIGNANT')")
	@GetMapping("/modules/list-all")
	@ResponseBody
	public List<Module> findAllModules(){
		return moduleServices.findAllModules();
	}
	
	@SuppressWarnings("all")
	@PreAuthorize("hasAnyRole('ROLE_COMITE_ORGANISATION','ROLE_RESPONSABLE_OPTION','ROLE_ENSEIGNANT')")
	@PostMapping("/add-option/{idresp}")
	@ResponseBody
	public ResponseEntity<Object> addOption(@Valid @RequestBody Option option,@PathVariable("idresp") Long idResp){
		
		if(Boolean.TRUE.equals(optionRepository.existsByName(option.getName()))) {
			return ResponseEntity.badRequest().body(new MessageResponse("This option is already exist!"));
		}
		return ResponseEntity.ok().body(optionServices.addOption(option, idResp));
	}
	
	@PreAuthorize("hasAnyRole('ROLE_COMITE_ORGANISATION','ROLE_RESPONSABLE_OPTION','ROLE_ENSEIGNANT')")
	@GetMapping("/get-option/{id}")
	@ResponseBody
	public ResponseEntity<Object> getOption(@PathVariable("id") Long id){

		return ResponseEntity.ok().body(optionServices.getOption(id));
	}
	
	@SuppressWarnings("all")
	@PreAuthorize("hasAnyRole('ROLE_COMITE_ORGANISATION','ROLE_RESPONSABLE_OPTION','ROLE_ENSEIGNANT')")
	@PostMapping("/edit-option")
	@ResponseBody
	public ResponseEntity<Object> editOption(@Valid @RequestBody Option option){
		
		if(Boolean.TRUE.equals(optionRepository.existsByName(option.getName()))) {
			
			Optional<Option> existOpt = optionRepository.findAll().stream().min(Comparator.comparing(Option::getName));
			
				if(existOpt.isPresent() && !option.getId().equals(existOpt.get().getId()))
				{
					return ResponseEntity.badRequest().body(new MessageResponse("This option is already exist!"));
				}
		}
		return ResponseEntity.ok().body(optionServices.editOption(option));
	}
	
	@SuppressWarnings("all")
	@PreAuthorize("hasAnyRole('ROLE_COMITE_ORGANISATION','ROLE_RESPONSABLE_OPTION','ROLE_ENSEIGNANT')")
	@PostMapping("/modules/add")
	@ResponseBody
	public ResponseEntity<Object> addlModule(@Valid @RequestBody Module mod){
		
		if(Boolean.TRUE.equals(moduleRepository.existsByName(mod.getName()))) {
			return ResponseEntity.badRequest().body(new MessageResponse("This module is already exist!"));
		}
		return ResponseEntity.ok().body(moduleServices.addModule(mod));
	}
	
	@PreAuthorize("hasAnyRole('ROLE_COMITE_ORGANISATION','ROLE_RESPONSABLE_OPTION','ROLE_ENSEIGNANT')")
	@DeleteMapping("/delete-option/{id}")
	@ResponseBody ResponseEntity<Object> deleteOption(@Valid @PathVariable("id") Long id){
		
		if(optionRepository.findById(id).orElse(null) == null) {
			return ResponseEntity.badRequest().body(new MessageResponse("This option does not exist!"));
		}
		return ResponseEntity.ok().body(optionServices.deleteOption(id));
	}
}
