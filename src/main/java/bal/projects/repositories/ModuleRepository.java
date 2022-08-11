package bal.projects.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bal.projects.entities.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long>{

	Boolean existsByName(String name);
}
