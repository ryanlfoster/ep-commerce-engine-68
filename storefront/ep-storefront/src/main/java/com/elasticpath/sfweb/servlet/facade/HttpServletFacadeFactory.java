package com.elasticpath.sfweb.servlet.facade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <code>HttpServletFacadeFactory</code> generates <code>HttpServletRequestFacade</code>s and <code>HttpServletResponseFacade</code>s.
 */
public interface HttpServletFacadeFactory {
	
	/**
	 * Creates a wrapper that contains, but limits access to the underlying <code>HttpServletRequest</code>.
	 *
	 * @param request HttpServletRequest to wrap.
	 * @return a new HttpServletRequestFacade.
	 */
	HttpServletRequestFacade createRequestFacade(HttpServletRequest request);
	
	/**
	 * Creates a wrapper that contains, but limits access to the underlying <code>HttpServletRequest</code> and
	 * <code>HttpServletResponse</code>.
	 *
	 * @param request HttpServletRequest to wrap.
	 * @param response HttpServletResponse to wrap.
	 * @return a new HttpServletRequestResponseFacade.
	 */
	HttpServletRequestResponseFacade createRequestResponseFacade(HttpServletRequest request, HttpServletResponse response);
}
