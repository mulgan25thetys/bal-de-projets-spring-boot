package bal.projects.services;

import java.util.List;

import javax.validation.Valid;

import bal.projects.entities.Option;

public interface IOptionServices {

	List<Option> findAll();
	
	Option addOption(Option opt,Long idResponsable);
	
	Option editOption(Option opt);
	
	Boolean deleteOption(@Valid Long id);
	
	Option getOption(Long id);
}
