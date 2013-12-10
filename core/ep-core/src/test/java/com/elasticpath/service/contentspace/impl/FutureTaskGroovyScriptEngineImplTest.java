package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import groovy.lang.Script;

import org.junit.Test;

import com.elasticpath.service.contentspace.impl.AbstractGroovyScriptEngineImpl.RunnableScript;
import com.elasticpath.service.contentspace.impl.FutureTaskGroovyScriptEngineImpl.SynchronizedRunnableScript;

/**
 * Test for the caching in the FutureTaskGroovyScriptEngineImpl.
 */
public class FutureTaskGroovyScriptEngineImplTest {

	private static final String GROOVY_SCRIPT = "a = 1";


	/** Test that the first call causes compilation. */
	@Test
	public void testFirstCallCausesCompilation() {
		FutureTaskGroovyScriptEngineImpl scriptEngine = new FutureTaskGroovyScriptEngineImpl();
		
		RunnableScript script = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		assertNotNull("A null script means it didn't successfully compile", script);
	}
	
	/** Test that consecutive calls use the cached script. */
	@Test
	public void testSecondCallUsesCachedScript() {
		FutureTaskGroovyScriptEngineImpl scriptEngine = new FutureTaskGroovyScriptEngineImpl();
		
		RunnableScript script1 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		Script firstScript = ((SynchronizedRunnableScript) script1).getScriptForTestingOnly();
		
		RunnableScript script2 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		Script secondScript = ((SynchronizedRunnableScript) script2).getScriptForTestingOnly();

		assertNotSame("We should get different script wrapper.", script1, script2);
		assertSame("The same underlying script should be returned from the cache.", firstScript, secondScript);
	}

	/** Test multi-threaded calls use the cached script (only one is used). */
	@Test
	public void testSubsequentMultiThreadedCallUsesCachedScript() {
		final FutureTaskGroovyScriptEngineImpl scriptEngine = new FutureTaskGroovyScriptEngineImpl();
		final RunnableScript script1 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		final Script firstScript = ((SynchronizedRunnableScript) script1).getScriptForTestingOnly();
		
		Runnable runnable = new Runnable() {
			public void run() {
				RunnableScript script2 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
				Script secondScript = ((SynchronizedRunnableScript) script2).getScriptForTestingOnly();
				assertNotSame("We should get different script wrapper.", script1, script2);
				assertSame("The same underlying script should be returned from the cache.", firstScript, secondScript);
			}
		};
		
		final int max = 20;
		threadAndWait(runnable, max);
	}

	
	private void threadAndWait(final Runnable runnable, final int max) {
		Thread [] threads = new Thread[max];
		for (int x = 0; x < max; x++) {
			threads[x] = new Thread(runnable);
			threads[x].start();
		}
		for (int x = 0; x < max; x++) {
			try {
				threads[x].join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
	
}
