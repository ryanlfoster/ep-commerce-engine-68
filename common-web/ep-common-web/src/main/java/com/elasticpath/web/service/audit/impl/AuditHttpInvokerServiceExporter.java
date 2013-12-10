/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.web.service.audit.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;

import com.elasticpath.commons.ThreadLocalMap;

/**
 * Extension to the Spring HttpInvokerServiceExporter that tracks info about the caller. 
 */
public class AuditHttpInvokerServiceExporter extends HttpInvokerServiceExporter implements BeanFactoryAware {

	private BeanFactory beanFactory;

	private ThreadLocalMap<String, Object> metadataMap;
	
	/**
	 * Set the bean factory used for getting beans.
	 * 
	 * @param beanFactory the <code>BeanFactory</code> to use
	 * @throws BeansException in case of error
	 */
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * When a read is called on the remote invocation, intercept it and read off the
	 * attribute we require.
	 * 
	 * @param request the request involved
	 * @return the <code>RemoteInvocation</code>
	 * @throws IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown during deserialization
	 */
	@Override
	protected RemoteInvocation readRemoteInvocation(final HttpServletRequest request) throws IOException, ClassNotFoundException {
		RemoteInvocation remoteInvocation = super.readRemoteInvocation(request);
		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) remoteInvocation.getAttribute("auditMetadata");
		processMetadata(metadata);
		return remoteInvocation;
	}

	/**
	 * Process the metadata - register it on the audit service.
	 * 
	 * @param metadata the metadata to register.
	 */
	protected void processMetadata(final Map<String, String> metadata) {
		if (metadata != null) {
			getMetadataMap().putAll(metadata);
		}
	}

	/**
	 * Get the map of metadata for this listener.
	 * 
	 * @return the threadLocalMap
	 */
	@SuppressWarnings("unchecked")
	protected ThreadLocalMap<String, Object> getMetadataMap() {
		if (metadataMap == null) {
			metadataMap = (ThreadLocalMap<String, Object>) beanFactory.getBean("persistenceListenerMetadataMap");
		}
		return metadataMap;
	}
	
}
