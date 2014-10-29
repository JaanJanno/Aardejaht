package ee.bmagrupp.aardejaht.server.service;

import java.util.List;

import ee.bmagrupp.aardejaht.server.rest.domain.PlayerProfile;

public interface ProfileService {

	void createSampleData();

	PlayerProfile getPlayer(int id);
	
	List<PlayerProfile> findAll();
}