package com.elasticpath.domain.geoip.location.impl;

import com.elasticpath.domain.geoip.location.GeoIpLocation;

/**
 * 
 * Class holds full information about location status. 
 */
public class GeoIpLocationImpl implements GeoIpLocation {

	private String asnNumber;
	private String carrierName;
	private String city;
	private String connectionSpeed;
	private String connectionType;
	private String continent;
	private String countryCode;
	private String ipAddress;
	private String ipRoutingType;
	private String latitude;
	private String longitude;
	private String phoneNumber;
	private String region;
	private String registeredOrganization;
	private String secondLevelDomain;
	private String state;
	private Float timeZone;
	private String topLevelDomain;
	private String zipCode;
	
	@Override
	public String getAsnNumber() {		
		return asnNumber;
	}

	@Override
	public String getCarrierName() {
		return carrierName;
	}

	@Override
	public String getCity() {		
		return city;
	}

	@Override
	public String getConnectionSpeed() {
		return connectionSpeed;
	}

	@Override
	public String getConnectionType() {
		return connectionType;
	}

	@Override
	public String getContinent() {
		return continent;
	}

	@Override
	public String getCountryCode() {
		return countryCode;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public String getIpRoutingType() {
		return ipRoutingType;
	}

	@Override
	public String getLatitude() {
		return latitude;
	}

	@Override
	public String getLongitude() {
		return longitude;
	}

	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String getRegion() {
		return region;
	}

	@Override
	public String getRegisteredOrganization() {
		return registeredOrganization;
	}

	@Override
	public String getSecondLevelDomain() {
		return secondLevelDomain;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public Float getGmtTimeZone() {
		return timeZone;
	}

	@Override
	public String getTopLevelDomain() {
		return topLevelDomain;
	}

	@Override
	public String getZipCode() {
		return zipCode;
	}

	@Override
	public void setAsnNumber(final String asnNumber) {
		this.asnNumber = asnNumber;
	}

	@Override
	public void setCarrierName(final String carrierName) {
		this.carrierName = carrierName;
	}

	@Override
	public void setCity(final String city) {
		this.city = city;
	}

	@Override
	public void setConnectionSpeed(final String connectionSpeed) {
		this.connectionSpeed = connectionSpeed;
	}

	@Override
	public void setConnectionType(final String connectionType) {
		this.connectionType = connectionType;
	}

	@Override
	public void setContinent(final String continent) {
		this.continent = continent;
	}

	@Override
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public void setIpRoutingType(final String ipRoutingType) {
		this.ipRoutingType = ipRoutingType;
	}

	@Override
	public void setLatitude(final String latitude) {
		this.latitude = latitude;

	}

	@Override
	public void setLongitude(final String longitude) {
		this.longitude = longitude;
	}

	@Override
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public void setRegion(final String region) {
		this.region = region;
	}

	@Override
	public void setRegisteredOrganization(final String registeredOrganization) {
		this.registeredOrganization = registeredOrganization;
	}

	@Override
	public void setSecondLevelDomain(final String secondLevelDomain) {
		this.secondLevelDomain = secondLevelDomain;
	}

	@Override
	public void setState(final String state) {
		this.state = state;
	}

	@Override
	public void setGmtTimeZone(final Float timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public void setTopLevelDomain(final String topLevelDomain) {
		this.topLevelDomain = topLevelDomain;
	}

	@Override
	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

}
