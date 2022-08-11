package bal.projects.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bal.projects.entities.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long>{

	@Query(value = "SELECT *FROM Options WHERE responsable_id =:id",nativeQuery = true)
	public Option getOptionForThisResponsable(@Param("id") Long idResponsable);
	
	Boolean existsByName(String name);
	
	Optional<Option> findByName(String name);
}
