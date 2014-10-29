package ee.bmagrupp.aardejaht.server.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ee.bmagrupp.aardejaht.server.core.domain.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

	Player findByEmail(String email);
	
	Player findBySid(String sid);
	
	@Query("from Player p left join p.ownedProvinces op where op.id = ?1")
	Player findOwner(int ownershipId);
}