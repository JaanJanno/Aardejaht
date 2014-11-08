package ee.bmagrupp.aardejaht.server.service;

import static org.junit.Assert.*;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import ee.bmagrupp.aardejaht.server.Application;
import ee.bmagrupp.aardejaht.server.core.domain.Player;
import ee.bmagrupp.aardejaht.server.core.domain.Province;
import ee.bmagrupp.aardejaht.server.core.repository.HomeOwnershipRepository;
import ee.bmagrupp.aardejaht.server.core.repository.PlayerRepository;
import ee.bmagrupp.aardejaht.server.core.repository.ProvinceRepository;
import ee.bmagrupp.aardejaht.server.rest.domain.ProvinceDTO;
import ee.bmagrupp.aardejaht.server.rest.domain.ProvinceType;
import ee.bmagrupp.aardejaht.server.rest.domain.ProvinceViewDTO;
import ee.bmagrupp.aardejaht.server.util.Constants;
import ee.bmagrupp.aardejaht.server.util.GeneratorUtil;

/**
 * The {@link ProvinceServiceGeneratorTest} tests are mostly focused on the
 * generation {@link ProvinceDTO}'s, but these tests are focused on the other
 * methods in {@link ProvinceService}.
 * 
 * @author TKasekamp
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ProvinceServiceTest2 {

	private String sid; // Mr.TK
	private String cookie; // Default cookie value
	private double lat1;
	private double long1;

	private double lat2;
	private double long2;

	@Autowired
	private ProvinceService provServ;

	@Autowired
	private ProvinceRepository provRepo;

	@Autowired
	private PlayerRepository playerRepo;

	@Autowired
	private HomeOwnershipRepository homeRepo;

	@Before
	public void setUp() {
		sid = "BPUYYOU62flwiWJe";
		cookie = "cookie";

		lat1 = 37.8265;
		long1 = -122.423;

		lat2 = -40.4195;
		long2 = 144.961;
	}

	// Tests for getProvince

	@Test
	public void botOwnedProvinceWithSID() {

		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(lat1),
				Double.toString(long1), sid);
		ProvinceViewDTO prov2 = provServ.getProvince(Double.toString(lat1),
				Double.toString(long1), sid);

		assertEquals("Province latitude", lat1, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", long1, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.BOT, prov1.getType());

		assertEquals("Same name", prov1.getProvinceName(),
				prov2.getProvinceName());
		assertEquals("Same unit size", prov1.getUnitSize(), prov2.getUnitSize());
	}

	@Test
	public void botOwnedProvinceWithoutSID() {

		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(lat1),
				Double.toString(long1), cookie);
		ProvinceViewDTO prov2 = provServ.getProvince(Double.toString(lat1),
				Double.toString(long1), cookie);

		assertEquals("Province latitude", lat1, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", long1, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.BOT, prov1.getType());

		assertEquals("Same name", prov1.getProvinceName(),
				prov2.getProvinceName());
		assertEquals("Same unit size", prov1.getUnitSize(), prov2.getUnitSize());
	}

	@Test
	public void homeProvince() {

		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(lat2),
				Double.toString(long2), sid);

		assertEquals("Province latitude", lat2, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", long2, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.HOME, prov1.getType());
		assertEquals("Province name", "lzpD6mFm44", prov1.getProvinceName());
		assertEquals("Province unit size", 10, prov1.getUnitSize());
		assertEquals("Province owner name", "Mr. TK", prov1.getOwnerName());

	}

	@Test
	public void otherPlayerProvince() {
		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(lat2),
				Double.toString(long2), "UJ86IpW5xK8ZZH7t"); // JohnnyZQ

		assertEquals("Province latitude", lat2, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", long2, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.OTHER_PLAYER,
				prov1.getType());
		assertEquals("Province name", "lzpD6mFm44", prov1.getProvinceName());
		assertEquals("Province unit size", 4, prov1.getUnitSize());
		assertEquals("Province new units", 0, prov1.getNewUnitSize());
		assertEquals("Province owner name", "Mr. TK", prov1.getOwnerName());
		assertEquals("Attackable", true, prov1.isAttackable());
		assertEquals("Attackable", false, prov1.isUnderAttack());
	}

	@Test
	public void ownedProvince() {
		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(-40.4225),
				Double.toString(144.963), sid);

		assertEquals("Province latitude", -40.4225, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", 144.963, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.PLAYER, prov1.getType());
		assertEquals("Province name", "Kvukx9SCOB", prov1.getProvinceName());
		assertEquals("Province unit size", 9, prov1.getUnitSize());
		// assertEquals("Province new units", 0, prov1.getNewUnitSize());
		assertEquals("Province owner name", "Mr. TK", prov1.getOwnerName());
		assertEquals("Attackable", false, prov1.isAttackable());
		assertEquals("Under attack", false, prov1.isUnderAttack());
	}

	@Test
	public void ownedProvinceWithoutSid() {
		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(-40.4225),
				Double.toString(144.963), cookie);

		assertEquals("Province latitude", -40.4225, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", 144.963, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.OTHER_PLAYER,
				prov1.getType());
		assertEquals("Province name", "Kvukx9SCOB", prov1.getProvinceName());
		assertEquals("Province unit size", 9, prov1.getUnitSize());
		// assertEquals("Province new units", 0, prov1.getNewUnitSize());
		assertEquals("Province owner name", "Mr. TK", prov1.getOwnerName());
		assertEquals("Attackable", false, prov1.isAttackable());
		assertEquals("Under attack", false, prov1.isUnderAttack());
	}

	@Test
	public void otherPlayerHome() {
		double lat3 = 26.123;
		double long3 = 58.123;
		Province home = new Province(lat3, long3);
		Player player = new Player("Smaug", GeneratorUtil.generateString(16),
				home);

		assertNull(playerRepo.findByUserName("Smaug"));
		provRepo.save(home);
		homeRepo.save(player.getHome());
		playerRepo.save(player);

		ProvinceViewDTO prov1 = provServ.getProvince(Double.toString(lat3),
				Double.toString(long3), sid);

		assertEquals("Province latitude", lat3, prov1.getLatitude(), 0.0001);
		assertEquals("Province longitude", long3, prov1.getLongitude(), 0.001);
		assertEquals("Province type", ProvinceType.BOT, prov1.getType());

		assertEquals("Province new units", 0, prov1.getNewUnitSize());
		assertEquals("Province owner name", Constants.BOT_NAME,
				prov1.getOwnerName());
		assertEquals("Attackable", true, prov1.isAttackable());
		assertEquals("Under attack", false, prov1.isUnderAttack());
	}

	@Test(expected = NumberFormatException.class)
	public void badParameterTest() {
		provServ.getProvince("dfdf", "sdsd", sid);

	}

}
