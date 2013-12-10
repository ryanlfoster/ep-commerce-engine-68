package com.elasticpath.web.security;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;

import com.elasticpath.web.security.ParameterConfiguration.Policy;

/**
 * Spring namespace handler for binding configuration.
 */
public class DatabinderNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("binding", new ParameterConfigurationParser());
	}

	/**
	 * {@link ParameterConfiguration} parser for bindings.
	 */
	private static class ParameterConfigurationParser extends AbstractSingleBeanDefinitionParser {
		@Override
		protected Class<?> getBeanClass(final Element element) {
			return ParameterConfiguration.class;
		}

		@Override
		protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
			builder.addConstructorArgValue(element.getAttribute("pattern"));
			builder.addConstructorArgValue(element.getAttribute("rule"));
			builder.addConstructorArgValue(element.getAttribute("maxLength"));
			builder.addConstructorArgValue(element.getAttribute("allowEmpty"));

			String policyStr = element.getAttribute("policy");
			if (policyStr == null) {
				policyStr = StringUtils.EMPTY;
			}
			Policy policy = Enum.valueOf(Policy.class, policyStr.toUpperCase(Locale.US));
			builder.addConstructorArgValue(policy);
		}
	}
}
