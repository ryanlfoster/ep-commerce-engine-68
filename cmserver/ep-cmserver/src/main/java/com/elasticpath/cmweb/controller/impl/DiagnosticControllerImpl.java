/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmweb.controller.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple controller which indicates that web application started properly.
 */
@Controller
public class DiagnosticControllerImpl {

	/**
	 * Return diagnostic response.
	 * @param response http servlet response
	 * @throws IOException in case of an IO exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void constructDiagnosticResponse(final HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		response.getOutputStream().print("I am alive!");
	}
}