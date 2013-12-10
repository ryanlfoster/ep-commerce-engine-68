package com.elasticpath.sfweb.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.sfweb.util.StoreResolver;

/**
 * Determines which store has been requested and tells the {@link ThreadLocalStorageImpl} which store has been selected for this request/thread.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class StoreSelectionFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(StoreSelectionFilter.class);

	private StoreResolver storeResolver;

	private ThreadLocalStorageImpl storeConfig;

	private List<String> selectionCandidates;

	/**
	 * Filters the request by checking the host and setting the appropriate store code.
	 * 
	 * @param request the request
	 * @param response the servlet response object
	 * @param filterChain the servlet filter chain
	 * @throws IOException on IO error
	 * @throws ServletException on servlet error
	 */
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException,
			ServletException {

		HttpServletRequest httprequest = (HttpServletRequest) request;

		String storeCode = null;
		for (String candidate : getSelectionCandidates()) {
			storeCode = resolveCandidate(httprequest, candidate);
			if (storeCode != null) {
				break;
			}
		}

		if (storeCode == null) {
			LOG.info("Unable to find store code from any selection candidate");
			return;
		}

		storeConfig.setStoreCode(storeCode);
		filterChain.doFilter(request, response);
		// would like to clear the storeconfig here so its not available on thread, but error/404 page needs thread local store code
		// set because we don't go down the filter chain again when forwarded to the error page.
	}

	/**
	 * Resolve the store code from the given selection candidate.
	 * 
	 * @param request the request to process
	 * @param candidate the selection candidate to use
	 * @return a store code or null if none found
	 */
	protected String resolveCandidate(final HttpServletRequest request, final String candidate) {
		if (candidate == null || candidate.length() == 0 || !candidate.contains("=")) {
			return null;
		}

		String[] keyValue = candidate.split("=");
		if ("DomainHeader".equalsIgnoreCase(keyValue[0])) {
			return storeResolver.resolveDomainHeader(request, keyValue[1]);
		}
		if ("StoreCodeSession".equalsIgnoreCase(keyValue[0])) {
			return storeResolver.resolveStoreCodeSession(request, keyValue[1]);
		}
		if ("StoreCodeParam".equalsIgnoreCase(keyValue[0])) {
			return storeResolver.resolveStoreCodeParam(request, keyValue[1]);
		}
		if ("StoreCodeHeader".equalsIgnoreCase(keyValue[0])) {
			return storeResolver.resolveStoreCodeHeader(request, keyValue[1]);
		}
		if ("DomainParam".equalsIgnoreCase(keyValue[0])) {
			return storeResolver.resolveDomainParam(request, keyValue[1]);
		}
		if ("DomainSession".equalsIgnoreCase(keyValue[0])) {
			return storeResolver.resolveDomainSession(request, keyValue[1]);
		}
		return null;
	}

	/**
	 * Sets the store resolver instance.
	 * 
	 * @param storeResolver the store resolver
	 */
	public void setStoreResolver(final StoreResolver storeResolver) {
		this.storeResolver = storeResolver;
	}

	/**
	 * Sets the store config instance.
	 * 
	 * @param storeConfig the store config
	 */
	public void setStoreConfig(final ThreadLocalStorageImpl storeConfig) {
		this.storeConfig = storeConfig;
	}

	/**
	 * Initializes the filter.
	 * 
	 * @param filterConfig the filter config
	 * @throws ServletException on servlet error
	 */
	public void init(final FilterConfig filterConfig) throws ServletException {
		// Nothing to do
	}

	/**
	 * Called when destroy event is triggered by the container.
	 */
	public void destroy() {
		// Nothing to do
	}

	/**
	 * Get the list of store selection candidates.
	 * 
	 * @return the list of candidates
	 */
	protected List<String> getSelectionCandidates() {
		if (selectionCandidates == null) {
			return new ArrayList<String>();
		}
		return this.selectionCandidates;
	}

	/**
	 * Set the selection candidates to use for store resolution. Typically these will be injected via spring and look something like
	 * StoreCodeParam=storeCode DomainHeader=HOST
	 * 
	 * @param candidates a list of key/value pairs
	 */
	public void setSelectionCandidates(final List<String> candidates) {
		this.selectionCandidates = candidates;
	}

}
