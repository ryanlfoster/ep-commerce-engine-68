/**
 *
 */
package com.elasticpath.sfweb.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.tags.TagProcessorsHolder;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.web.security.EsapiServletUtils;

/**
 * EpDispatcherServlet. Dispatch servlet is extended for tags processors support.
 */
public class EpDispatcherServlet extends DispatcherServlet {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090415L;

	private TagProcessorsHolder tagProcessorsHolder;

	@Override
	protected WebApplicationContext initWebApplicationContext() throws BeansException {
		WebApplicationContext context = super.initWebApplicationContext();

		String tagProcesorsHolderParameter = getServletConfig().getInitParameter("tagProcessorsHolder");
		this.tagProcessorsHolder = (TagProcessorsHolder) context.getBean(tagProcesorsHolderParameter);

		return context;
	}

	@Override
	protected void render(final ModelAndView modelAndView, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final HttpServletRequest safeRequest = EsapiServletUtils.secureHttpRequest(request);
		final HttpServletResponse safeResponse = EsapiServletUtils.secureHttpResponse(response);
		
	    final SfRequestHelper requestHelper = (SfRequestHelper) this.getWebApplicationContext().getBean("requestHelper");
		CustomerSession customerSession = requestHelper.getCustomerSession(safeRequest);
		
		final HttpServletFacadeFactory facadeFactory = (HttpServletFacadeFactory) getWebApplicationContext().getBean("httpServletFacadeFactory");
		final HttpServletRequestFacade requestFacade = facadeFactory.createRequestFacade(safeRequest);

		tagProcessorsHolder.fireExecute(customerSession, requestFacade);

		super.render(modelAndView, safeRequest, safeResponse);
	}

	@Override
	public void destroy() {
		tagProcessorsHolder.removeAllListeners();
		tagProcessorsHolder = null;

		super.destroy();
	}

}
