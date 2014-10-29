package ee.bmagrupp.aardejaht.server.service.imlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ee.bmagrupp.aardejaht.server.core.domain.Ownership;
import ee.bmagrupp.aardejaht.server.core.domain.Player;
import ee.bmagrupp.aardejaht.server.core.domain.Unit;
import ee.bmagrupp.aardejaht.server.core.domain.UnitState;
import ee.bmagrupp.aardejaht.server.core.repository.HomeOwnershipRepository;
import ee.bmagrupp.aardejaht.server.core.repository.OwnershipRepository;
import ee.bmagrupp.aardejaht.server.core.repository.PlayerRepository;
import ee.bmagrupp.aardejaht.server.core.repository.ProvinceRepository;
import ee.bmagrupp.aardejaht.server.core.repository.UnitRepository;
import ee.bmagrupp.aardejaht.server.rest.domain.CameraFOV;
import ee.bmagrupp.aardejaht.server.rest.domain.Province;
import ee.bmagrupp.aardejaht.server.service.ProvinceService;
import static ee.bmagrupp.aardejaht.server.util.Constants.*;
import ee.bmagrupp.aardejaht.server.util.NameGenerator;

@Service
public class ProvinceServiceImpl implements ProvinceService {

	private static Logger LOG = LoggerFactory
			.getLogger(ProvinceServiceImpl.class);

	@Autowired
	ProvinceRepository provRepo;

	@Autowired
	PlayerRepository playerRepo;

	@Autowired
	OwnershipRepository ownerRepo;

	@Autowired
	HomeOwnershipRepository homeRepo;

	@Autowired
	UnitRepository unitRepo;

	/**
	 * @author Sander
	 * @param cookie
	 * @return returns the overall strength of a player
	 */

	private int findPlayerStrength(String cookie) {
		Player curPlayer = playerRepo.findBySid(cookie);
		Set<Ownership> provinces = curPlayer.getOwnedProvinces();
		int overall = 0;
		for (Ownership b : provinces) {
			Set<Unit> units = b.getUnits();
			for (Unit unit : units) {
				if (unit.getState() == UnitState.CLAIMED) {
					overall += unit.getSize();
				}
			}
		}
		return overall;
	}

	/**
	 * @author Sander
	 * @param lat2
	 *            /long2
	 * @param lat1
	 *            /long1
	 * @return The amount of columns/rows needed to be calculated
	 */

	private int calculateRowsNr(double lat1, double lat2) {
		lat1 = Math.floor(lat1 * 1000) / 1000;
		lat2 = Math.ceil(lat2 * 1000) / 1000;
		return (int) ((lat2 - lat1) / PROVINCE_HEIGHT);
	}

	private int calculateColumnNr(double long1, double long2) {
		long1 = Math.floor(long1 * 1000) / 1000;
		long2 = Math.ceil(long2 * 1000) / 1000;
		return (int) ((long2 - long1) / PROVINCE_WIDTH);
	}

	/**
	 * @author Sander
	 * @param fov
	 *            - camera object
	 * @param playerId
	 *            - The player who sent the request
	 * @return returns an array with Province objects with info for the mobile
	 *         app
	 */

	@Override
	public List<Province> getProvinces(CameraFOV fov, String cookie) {
		ArrayList<Province> rtrn = new ArrayList<Province>();
		// maybe obsolete , Tõnis says no
		double lat1 = Math.round(fov.getSWlatitude() * 1000) / 1000;
		double lat2 = Math.round(fov.getNElatitude() * 1000) / 1000;
		double long1 = Math.round(fov.getSWlongitude() * 1000) / 1000;
		double long2 = Math.round(fov.getNElongitude() * 1000) / 1000;

		LOG.info("findBetween arguments in order");
		LOG.info(Double.toString(long1));
		LOG.info(Double.toString(lat1));
		LOG.info(Double.toString(long2));
		LOG.info(Double.toString(lat2));
		// ---
		int columns = calculateColumnNr(fov.getSWlongitude(),
				fov.getNElongitude());
		int rows = calculateRowsNr(fov.getSWlatitude(), fov.getNElatitude());
		int playerStrength = findPlayerStrength(cookie);

		LOG.info("Player strength " + Integer.toString(playerStrength));
		double baseLat = Math.floor(lat1 * 1000) / 1000;
		double baseLong = Math.floor(long1 * 1000) / 1000;

		List<Ownership> lst = ownerRepo.findBetween(long1, lat1, long2, lat2);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				boolean found = false;
				for (Ownership a : lst) {
					double x = a.getProvince().getLongitude();
					double y = a.getProvince().getLatitude();
					// Flow control for determining whether the ownership is
					// inside
					// The marked area or not
					if (x > (baseLat + (i * PROVINCE_HEIGHT))
							&& x < (baseLat + ((i + 1) * PROVINCE_HEIGHT))
							&& y > (baseLong + (j * PROVINCE_WIDTH))
							&& y < (baseLong + ((j + 1) * PROVINCE_WIDTH))) {
						// -----
						ee.bmagrupp.aardejaht.server.core.domain.Province temp = a
								.getProvince();
						Set<Unit> units = a.getUnits();
						int overall = 0;
						for (Unit unit : units) {
							if (unit.getState() == UnitState.CLAIMED) {
								overall += unit.getSize();
							}
						}
						int playerId = playerRepo.findOwner(a.getId()).getId();
						rtrn.add(new Province(temp.getId(), temp.getLatitude(),
								temp.getLongitude(), overall, playerId, temp
										.getName()));
						found = true;
						break;
					}
				}
				if (!found) {
					// generateProvince(lat2, long2, playerStrength);
				}

			}
		}
		return rtrn;
	}

	private Province generateProvince(double latitude, double longitude,
			int playerStrength) {
		// Write something smarter here
		int unitCount = 2;
		if (playerStrength > 10) {
			unitCount = 11;
		}

		// has to be random, some big number will do
		int provinceID = 10001;

		String name = NameGenerator.generate(6);
		return new Province(provinceID, latitude, longitude, unitCount, BOT_ID,
				name);
	}

}