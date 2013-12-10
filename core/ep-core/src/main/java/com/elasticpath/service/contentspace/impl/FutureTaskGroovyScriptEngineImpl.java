package com.elasticpath.service.contentspace.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.util.GroovyScriptTimeoutCache;

/**
 * This implementation of the GroovyScriptEngine uses a single GroovyShell instance and
 * is biased to perform better where the same script is run very frequently: all text 
 * expressions are pre-compiled and the resulting Script objects cached.
 */
public class FutureTaskGroovyScriptEngineImpl extends AbstractGroovyScriptEngineImpl {

	private static final Logger LOG = Logger.getLogger(FutureTaskGroovyScriptEngineImpl.class);

	private final GroovyShell groovyShell = new GroovyShell();

	private static final long TIMEOUT = 1000 * 60 * 60 * 24;
	
	private static final SimpleTimeoutCache<String, FutureTask<Script>> CACHE = 
		new GroovyScriptTimeoutCache(TIMEOUT);

	/**
	 * Return the compiled script version of the incoming text script.
	 * @param textScript the script to get a compiled version of.
	 * @return the compiled script.
	 */
	public RunnableScript getCompiledScript(final String textScript) {
		boolean newlyCreated = false;

		FutureTask<Script> compiler;
		synchronized (CACHE) {
			compiler = CACHE.get(textScript);
			if (compiler == null) {
				newlyCreated = true;
				compiler = new FutureTask<Script>(new Callable<Script>() {
					public Script call() {
						return groovyShell.parse(textScript);
					}
				});
				CACHE.put(textScript, compiler);
			}
		}

		if (newlyCreated) {
			compiler.run();
		}

		Script script = null;
		try {
			script = compiler.get();
		} catch (Exception e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to compile groovy script", e);
			}
		}
		if (script == null) {
			return null;
		}
		return new SynchronizedRunnableScript(script);
	}



	/**
	 * A script wrapper that makes setting the binding on a script and running the 
	 * script an atomic operation - by synchronizing on the script.
	 */
	static class SynchronizedRunnableScript implements RunnableScript {

		private final Script script;

		/**
		 * @param script the script to run.
		 */
		public SynchronizedRunnableScript(final Script script) {
			this.script = script;

		}

		/**
		 * Runs the script in the context of the binding as an atomic operation.
		 * @param binding the binding to run the script in the context of.
		 * @return the result of the script.
		 */
		public Object run(final Binding binding) {
			synchronized (script) {
				script.setBinding(binding);
				return script.run();
			}
		}
		
		/** @return the currenct script object - for testing only. */
		Script getScriptForTestingOnly() {
			return script;
		}

	}
	
	
	/**
	 * @param cacheTimeoutMillis the milliseconds to cache compiled scripts for.
	 */
	public void setCacheTimeout(final long cacheTimeoutMillis) {
		CACHE.setTimeout(cacheTimeoutMillis);
	}
}
