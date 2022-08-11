package bal.projects.services;

import java.util.List;

import bal.projects.entities.Module;

public interface IModuleServices {

	Module addModule(Module mod);
	
	List<Module> findAllModules();
}
