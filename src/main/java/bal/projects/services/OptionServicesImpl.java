package bal.projects.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bal.projects.entities.ERole;
import bal.projects.entities.Option;
import bal.projects.entities.Role;
import bal.projects.entities.User;
import bal.projects.repositories.ModuleRepository;
import bal.projects.repositories.OptionRepository;
import bal.projects.repositories.UserRepository;
import bal.projects.repositories.RoleRepository;

@Service
public class OptionServicesImpl implements IOptionServices{

	@Autowired
	OptionRepository optionRepository;
	
	@Autowired
	ModuleRepository moduleRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	
	@Override
	public List<Option> findAll() {
		return optionRepository.findAll();
	}

	@Transactional
	public Option addOption(Option opt,Long idResponsable) {
		User responsable = userRepository.findById(idResponsable).orElse(null);
		if(responsable != null) {
			this.saveData(responsable, opt);
		}
		
		return null;
	}

	@Transactional
	public Boolean deleteOption(Long id) {
		Option opt = optionRepository.findById(id).orElse(null);
		
		if(opt !=null) {
			List<User> students = userRepository.getStudentsByOptionId(opt.getId());
			for (User student : students) {
				student.setMyOption(null);
				student.setDateModification(new Date());
					userRepository.save(student);
			}
			optionRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Option getOption(Long id) {
		return  optionRepository.findById(id).orElse(null);
	}

	@Transactional
	public Option editOption(Option opt) {
		opt.setDateModification(new Date());
		return this.saveData(opt.getResponsable(),opt);
	}
	
	private Option saveData(User responsable,Option opt) {

			for (Role role : responsable.getRoles()) {
				if(!role.getName().equals(ERole.ROLE_ETUDIANT)) {
					Option verifOption = optionRepository.getOptionForThisResponsable(responsable.getId());
					if(verifOption !=null) {
						verifOption.setResponsable(null);
						verifOption.setDateCreation(new Date());
						optionRepository.save(verifOption);
					}
					Optional<Role> newRole = roleRepository.findByName(ERole.ROLE_RESPONSABLE_OPTION);
					if(role.getName().equals(ERole.ROLE_ENSEIGNANT) && newRole.isPresent()) {
					
						responsable.getRoles().add(newRole.get());
					}
					opt.setResponsable(responsable);
					responsable.setOption(opt);
					responsable.setDateModification(new Date());
					userRepository.save(responsable);
					opt.setDateCreation(new Date());
					return optionRepository.save(opt);
				}
			}
		return opt;
	}

}
