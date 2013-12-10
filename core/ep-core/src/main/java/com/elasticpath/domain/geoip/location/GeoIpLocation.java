package com.elasticpath.domain.geoip.location;

/**
 * Interface of the class that suppose to hold
 * information about subject location.
 */
public interface GeoIpLocation {

	/**
	 * @return Returns the ip.
	 */
	String getIpAddress();
	
	/**
	 * Setter of the property <tt>ip</tt>.
	 * 
	 * @param ipAddress The ip address to set.
	 */
	void setIpAddress(String ipAddress);

	/**
	 * @return Returns the state.
	 */
	String getState();

	/**
	 * Setter of the property <tt>state</tt>.
	 * 
	 * @param state The state to set.
	 */
	 void setState(String state);

	/**
	 * @return Returns the city.
	 */
	String getCity();

	/**
	 * Setter of the property <tt>city</tt>.
	 * 
	 * @param city The city to set.
	 */
	void setCity(String city);

	/**
	 * @return Returns the region.
	 */
	String getRegion();

	/**
	 * Setter of the property <tt>region</tt>.
	 * 
	 * @param region The region to set.
	 */
	 void setRegion(String region);

	/**
	 * @return Returns the continent.
	 */
	String getContinent();

	/**
	 * Setter of the property <tt>continent</tt>.
	 * 
	 * @param continent The continent to set.
	 */
	void setContinent(String continent);

	/**
	 * @return Returns the latitude.
	 */
	String getLatitude();

	/**
	 * Setter of the property <tt>latitude</tt>.
	 * 
	 * @param latitude The latitude to set.
	 */
	void setLatitude(String latitude);

	/**
	 * @return Returns the longitude.
	 */
	String getLongitude();

	/**
	 * Setter of the property <tt>longitude</tt>.
	 * 
	 * @param longitude The longitude to set.
	 */
	void setLongitude(String longitude);

	/**
	 * @return Returns the asnNumber.
	 * @uml.property name="asnNumber"
	 */
	String getAsnNumber();

	/**
	 * Setter of the property <tt>asnNumber</tt>.
	 * 
	 * @param asnNumber The asnNumber to set.
	 * @uml.property name="asnNumber"
	 */
	void setAsnNumber(String asnNumber);

	/**
	 * @return Returns the zipCode.
	 * @uml.property name="zipCode"
	 */
	String getZipCode();

	/**
	 * Setter of the property <tt>zipCode</tt>.
	 * 
	 * @param zipCode The zipCode to set.
	 * @uml.property name="zipCode"
	 */
	void setZipCode(String zipCode);

	/**
	 * @return Returns the phoneNumber.
	 * @uml.property name="PhoneNumber"
	 */
	String getPhoneNumber();

	/**
	 * Setter of the property <tt>PhoneNumber</tt>.
	 * 
	 * @param phoneNumber The phoneNumber to set.
	 * @uml.property name="PhoneNumber"
	 */
	void setPhoneNumber(String phoneNumber);

	/**
	 * @return Returns the country.
	 * @uml.property name="countryCode"
	 */
	String getCountryCode();

	/**
	 * Setter of the property <tt>country</tt>.
	 * 
	 * @param countryCode The country code to set.
	 * @uml.property name="countryCode"
	 */
	void setCountryCode(String countryCode);

	/**
	 * @return Returns the ipRoutingType.
	 * @uml.property name="ipRoutingType"
	 */
	String getIpRoutingType();

	/**
	 * Setter of the property <tt>ipRoutingType</tt>.
	 * 
	 * @param ipRoutingType The ipRoutingType to set.
	 * @uml.property name="ipRoutingType"
	 */
	void setIpRoutingType(String ipRoutingType);

	/**
	 * @return Returns the connectionType.
	 * @uml.property name="connectionType"
	 */
	String getConnectionType();

	/**
	 * Setter of the property <tt>connectionType</tt>.
	 * 
	 * @param connectionType The connectionType to set.
	 * @uml.property name="connectionType"
	 */
	void setConnectionType(String connectionType);

	/**
	 * @return Returns the connectionSpeed.
	 * @uml.property name="connectionSpeed"
	 */
	String getConnectionSpeed();

	/**
	 * Setter of the property <tt>connectionSpeed</tt>.
	 * 
	 * @param connectionSpeed The connectionSpeed to set.
	 * @uml.property name="connectionSpeed"
	 */
	void setConnectionSpeed(String connectionSpeed);

	/**
	 * @return Returns the topLevelDomain.
	 * @uml.property name="topLevelDomain"
	 */
	String getTopLevelDomain();

	/**
	 * Setter of the property <tt>topLevelDomain</tt>.
	 * 
	 * @param topLevelDomain The topLevelDomain to set.
	 * @uml.property name="topLevelDomain"
	 */
	void setTopLevelDomain(String topLevelDomain);

	/**
	 * @return Returns the secondLevelDomain.
	 * @uml.property name="secondLevelDomain"
	 */
	String getSecondLevelDomain();

	/**
	 * Setter of the property <tt>secondLevelDomain</tt>.
	 * 
	 * @param secondLevelDomain The secondLevelDomain to set.
	 * @uml.property name="secondLevelDomain"
	 */
	void setSecondLevelDomain(String secondLevelDomain);

	/**
	 * @return Returns the carrierName.
	 * @uml.property name="carrierName"
	 */
	String getCarrierName();

	/**
	 * Setter of the property <tt>carrierName</tt>.
	 * 
	 * @param carrierName The carrierName to set.
	 * @uml.property name="carrierName"
	 */
	void setCarrierName(String carrierName);

	/**
	 * @return Returns the registeredOrganization.
	 * @uml.property name="registeredOrganization"
	 */
	String getRegisteredOrganization();

	/**
	 * Setter of the property <tt>registeredOrganization</tt>.
	 * 
	 * @param registeredOrganization The registeredOrganization to set.
	 * @uml.property name="registeredOrganization"
	 */
	void setRegisteredOrganization(String registeredOrganization);

	/**
	 * Get the gmt time zone offset.
	 * @return Returns the timeZone.
	 * @uml.property name="timeZone"
	 */
	Float getGmtTimeZone();

	/**
	 * Setter of the property gmt <tt>timeZone</tt>.
	 * 
	 * @param timeZone The timeZone to set.
	 * @uml.property name="timeZone"
	 */
	void setGmtTimeZone(Float timeZone);

}
