package com.elasticpath.web.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test class for {@link AbstractSecurityWizardFormController}.
 */
public class AbstractSecurityWizardFormControllerTest {
	/**
	 * If no configuration has been passed to the binder, then fields should not be bound.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testBindNoConfig() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", null);
		request.setSession(new MockHttpSession());

		final String value = "value";
		request.setParameter("field", value);

		AbstractSecurityWizardFormControllerTester controller = new AbstractSecurityWizardFormControllerTester();
		controller.setCommandClass(TargetObject.class);
		controller.setPages(new String[] { "page" });

		// call twice: once to populate session, once for actual call
		controller.handleRequest(request, new MockHttpServletResponse());
		ModelAndView view = controller.handleRequest(request, new MockHttpServletResponse());
		Object command = view.getModel().get(controller.getCommandName());

		assertNull("Value should not be set due to no configuration", ((TargetObject) command).field);
	}

	/**
	 * When there is configuration, it should setup the binder correctly.
	 *
	 * @throws Exception in case of errors
	 */
	@Test
	public void testBindConfig() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", null);
		request.setSession(new MockHttpSession());

		final String value = "value";
		request.setParameter("field", value);

		ParameterConfiguration config = new ParameterConfiguration("field");

		AbstractSecurityWizardFormControllerTester controller = new AbstractSecurityWizardFormControllerTester();
		controller.setCommandClass(TargetObject.class);
		controller.setPages(new String[] { "page" });
		controller.setBindingConfig(Collections.singletonList(config));

		// call twice: once to populate session, once for actual call
		controller.handleRequest(request, new MockHttpServletResponse());
		ModelAndView view = controller.handleRequest(request, new MockHttpServletResponse());
		Object command = view.getModel().get(controller.getCommandName());

		assertEquals("Value is configured, but not bound?", value, ((TargetObject) command).field);
	}

	/** Test class for {@link AbstractSecurityWizardFormController}. */
	private static class AbstractSecurityWizardFormControllerTester extends AbstractSecurityWizardFormController {
		@Override
		protected ModelAndView processFinish(final HttpServletRequest request, final HttpServletResponse response, final Object command,
				final BindException errors) throws Exception {
			return null;
		}
	}

	/** Bindable command for tests. */
	@SuppressWarnings("unused") // for tests
	private static final class TargetObject {
		private String field;

		public String getField() {
			return field;
		}

		public void setField(final String field) {
			this.field = field;
		}
	}
}
