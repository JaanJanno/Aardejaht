package ee.bmagrupp.georivals.server.rest.domain;

import ee.bmagrupp.georivals.server.game.util.Constants;

/**
 * DTO used for doing everything with provinces. This includes MapView,
 * ProvinceView and my provinces tab.
 * 
 * @author TKasekamp
 *
 */
public class ProvinceDTO {
	private final double latitude;
	private final double longitude;

	private final ProvinceType type;
	private final String provinceName;
	private final String ownerName;

	private final boolean attackable;
	private final boolean underAttack;

	private final int unitSize;
	private final int newUnitSize;

	public ProvinceDTO(double latitude, double longitude, ProvinceType type,
			String provinceName, String ownerName, boolean attackable,
			boolean underAttack, int unitSize, int newUnitSize) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.provinceName = provinceName;
		this.ownerName = ownerName;
		this.attackable = attackable;
		this.underAttack = underAttack;
		this.unitSize = unitSize;
		this.newUnitSize = newUnitSize;
	}

	/**
	 * Constructor used when the province is owned by the BOT.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param provinceName
	 * @param unitSize
	 */
	public ProvinceDTO(double latitude, double longitude, String provinceName,
			int unitSize) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = ProvinceType.BOT;
		this.provinceName = provinceName;
		this.ownerName = Constants.BOT_NAME;
		this.attackable = true;
		this.underAttack = false;
		this.unitSize = unitSize;
		this.newUnitSize = 0;
	}

	/**
	 * Constructor used when the province is the player's home province.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param ownerName
	 * @param provinceName
	 * @param unitSize
	 * @param newUnitSize
	 */
	public ProvinceDTO(double latitude, double longitude, String ownerName,
			String provinceName, int unitSize, int newUnitSize) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = ProvinceType.HOME;
		this.provinceName = provinceName;
		this.ownerName = ownerName;
		this.attackable = false;
		this.underAttack = false;
		this.unitSize = unitSize;
		this.newUnitSize = newUnitSize;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public ProvinceType getType() {
		return type;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public boolean isUnderAttack() {
		return underAttack;
	}

	public int getUnitSize() {
		return unitSize;
	}

	public int getNewUnitSize() {
		return newUnitSize;
	}

}
