package bal.projects.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bal.projects.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	
	@Query(value = "SELECT * FROM users WHERE email =:value OR username=:value",nativeQuery = true)
	User findByUsernameOrEmail(@Param("value") String value);
	
	@Query(value = "SELECT * FROM users WHERE mon_option_id =:optionid",nativeQuery = true)
	List<User> getStudentsByOptionId(@Param("optionid") Long id);
	
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);

	boolean existsByIdentifiant(String identifiant);
}
