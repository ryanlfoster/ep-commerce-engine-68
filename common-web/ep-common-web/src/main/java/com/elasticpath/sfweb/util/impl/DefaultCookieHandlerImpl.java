/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.sfweb.util.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.CookieGenerator;

import com.elasticpath.sfweb.util.CookieHandler;

/**
 * The default implementation of the {@link CookieHandler}. It works with {@link CookieGenerator}s to make sure
 * that different cookies are handled in a unique way.<p>
 * If a new cookie is added to the system a new {@link CookieGenerator} could be added to the list of generators
 * making the properties of the cookie specific.<p>
 * If no generator has been specified the default one will be used (max age unlimited, path '/',  not secured).
 */
public class DefaultCookieHandlerImpl implements CookieHandler {

	private Map<String, CookieGenerator> cookieGenerators = new HashMap<String, CookieGenerator>(0);

	/**
	 * Adds a cookie to the reponse using the cookie generator defined given by the map.
	 * 
	 * @param response the http response
	 * @param name the name of the cookie
	 * @param value the value of the cookie
	 */
	public void addCookie(final HttpServletResponse response, final String name, final String value) {
		getCookieGenerator(name).addCookie(response, value);
	}

	/**
	 * Removes a cookie using the response and a cookie generator instance.
	 * 
	 * @param response the response
	 * @param name the name
	 */
	public void removeCookie(final HttpServletResponse response, final String name) {
		getCookieGenerator(name).removeCookie(response);
	}
	
	/**
	 * Gets an existing or creates a new generator and returns it.
	 * 
	 * @param name the cookie name
	 * @return the cookie generator
	 */
	private CookieGenerator getCookieGenerator(final String name) {
		if (cookieGenerators.containsKey(name)) {
			return cookieGenerators.get(name);
		}
		return getDefaultCookieGenerator(name);
	}

	/**
	 * Gets the default cookie generator. For thread safety a new instance gets created 
	 * on every invocation.
	 * 
	 * @param name the cookie name
	 * @return the cookie generator
	 */
	protected CookieGenerator getDefaultCookieGenerator(final String name) {
		final CookieGenerator cookieGenerator = new CookieGenerator();
		cookieGenerator.setCookieName(name);
		return cookieGenerator;
	}

	/**
	 * Gets the cookie generators map.
	 * 
	 * @return the map of cookiie name -> cookie generator value
	 */
	protected Map<String, CookieGenerator> getCookieGenerators() {
		return cookieGenerators;
	}

	/**
	 * Sets the cookie generators list.
	 * 
	 * @param cookieGenerators the cookie generator list
	 */
	public void setCookieGenerators(final List<CookieGenerator> cookieGenerators) {
		this.cookieGenerators = createGeneratorsMap(cookieGenerators);
	}

	/**
	 * Creates a map of cookieName -> cookieGenerator out of the provided list of generators.
	 * 
	 * @param cookieGenerators the generators
	 * @return the new map
	 */
	private Map<String, CookieGenerator> createGeneratorsMap(final List<CookieGenerator> cookieGenerators) {
		final Map<String, CookieGenerator> result = new HashMap<String, CookieGenerator>();
		for (CookieGenerator generator : cookieGenerators) {
			result.put(generator.getCookieName(), generator);
		}
		return result;
	}

}
