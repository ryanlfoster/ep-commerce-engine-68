package com.elasticpath.domain.geoip.provider.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.geoip.provider.DomainNameResolver;

/**
*
* Resolve domain first and second level domain names via DNS instead 
* of geoip data provider. It can be used if some geoip provider not provide 
* first and second level domains (for example simple null geo ip provider). 
* Or force resolve using DNS.
*
*/
public class DomainNameResolverImpl implements DomainNameResolver {
	
	private static final Logger LOG = Logger.getLogger(DomainNameResolverImpl.class);
	
	/**
	 * Get The first and second level domain names if it allowed by java security manager, see InetAddress for more details.
	 * 
	 * @param customerIpAddress textual ip address representation.
	 * @return {@link Pair} of first and second level domain names from left to right or null in case of resolving errors.
	 */
	public Pair<String, String> getFirstAndSecondLevelDomainName(final String customerIpAddress) {
		try {
			InetAddress inetAddress = InetAddress.getByName(customerIpAddress);
			String hostName = inetAddress.getCanonicalHostName();
			return getFirstAndSecondLevelDomainNameByHostName(hostName, customerIpAddress);
		} catch (SecurityException e) {
			LOG.error("Security manager exists and checkConnect method doesn't allow to perform the operation for resolve domain name:" 
					+ e.getMessage());
		} catch (UnknownHostException e) {			
			LOG.error("Resolve domain name :" 
					+ e.getMessage());
		}		
		return null;
	}

	/**
	 * 
	 * Get The first and second level domain names from given domain name.
	 * Here need to check if given customerIpAddress equals hostName. 
     * Java security check can prohibit getHostName operation.
	 * See InetAddress.getHostName() returns section for detail
	 *  
	 * @param hostName the host name.
	 * @param customerIpAddress the client ip address 
	 * 
	 * @return {@link Pair} of first and second level domain names from left to right for given host name 
	 * or null in case of resolving errors.
	 */
	Pair<String, String> getFirstAndSecondLevelDomainNameByHostName(
			final String hostName, final String customerIpAddress) {
		
		Pair<String, String> pair = null;
		
		if (StringUtils.isNotBlank(hostName) && !hostName.equals(customerIpAddress)) {
			String [] domainNames = hostName.split("\\.");		
			if (domainNames.length == 1) {
				// "localhost" value or if somebody uses root dns server for browsing.
				pair = new Pair<String, String>(domainNames[domainNames.length - 1], null);
			} else if (domainNames.length >= 2) { 
				pair = new Pair<String, String>(domainNames[domainNames.length - 1], domainNames[domainNames.length - 2]);
			}
			
		}
		
		return pair;
	}

}
