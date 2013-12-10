package com.elasticpath.domain.geoip.provider;

import com.elasticpath.commons.util.Pair;

/**
*
* Resolve domain first and second level domain names via DNS instead 
* of geoip data provider. It can be used if some geoip provider not provide 
* first and second level domains (for example simple null geo ip provider). 
* Or force resolve using DNS.
*
*/
public interface DomainNameResolver {
	/**
	 * Get The first and second level domain names.
	 * @param customerIpAddress textual ip address representation.
	 * @return {@link Pair} of first and second level domain names from left to right.
	 */
	Pair<String, String> getFirstAndSecondLevelDomainName(String customerIpAddress);

}
