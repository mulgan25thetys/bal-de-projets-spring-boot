package bal.projects.services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bal.projects.entities.Module;
import bal.projects.entities.Option;
import bal.projects.repositories.ModuleRepository;
import bal.projects.repositories.OptionRepository;

@Service
public class ModuleServicesImpl implements IModuleServices{

	@Autowired
	OptionRepository optionRepository;
	
	@Autowired
	ModuleRepository moduleRepository;
	
	@Override
	public List<Module> findAllModules() {
		return moduleRepository.findAll();
	}

	@Transactional
	public Module addModule(Module mod) {
		List<Option> options = mod.getOptions();
		
		if(!options.isEmpty()) {
			Module moduleresp = moduleRepository.save(mod);
			for (Option option : options) {
				option.setDateCreation(new Date());
				option.getModules().add(mod);
				optionRepository.save(option);
			}
			return  moduleresp;
		}
		return null;
	}
}
