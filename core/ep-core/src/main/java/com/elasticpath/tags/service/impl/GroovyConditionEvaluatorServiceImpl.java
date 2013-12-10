package com.elasticpath.tags.service.impl;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.ConditionEvaluatorService;
import com.elasticpath.tags.util.GroovyScriptTimeoutCache;

/**
 * ConditionalExpression evaluator with a Groovy script engine.
 * Evaluates conditions strings need to be expressed in tag framework DSL format. 
 */
public class GroovyConditionEvaluatorServiceImpl implements ConditionEvaluatorService {
	private static final String EVAL_METHOD = "runOnMap";
	
	private static final Logger LOG = Logger.getLogger(GroovyConditionEvaluatorServiceImpl.class);
	private final GroovyExpressionBuilder expressionBuilder = new GroovyExpressionBuilder();
	private static final long DEFAULT_CACHE_TIMEOUT = 60000;
	private final SimpleTimeoutCache<String, FutureTask<Script>> scriptCache
		= new GroovyScriptTimeoutCache(DEFAULT_CACHE_TIMEOUT);

	/**
     * Initialize the groovy environment by loading the groovy 
     * initialization script to make modifications to MetaClasses.
     * 
     * Getting class from classpath to avoid dependency on groovy classes from java code.
     */
    public void initialize() {
    	//Nothing to do
    }
    
    @Override
	public boolean evaluateConditionOnTags(final TagSet tags, final ConditionalExpression condition) {
		return evaluateConditionOnMap(tags.getTags(), condition);
	}
	
	/**
	 * Evaluation on a map.
	 * @param map of string tag key to string tag values.
	 * @param condition the condition to evaluate
	 * @return true if condition script evaluates true
	 */
	boolean evaluateConditionOnMap(final Map <String, Tag> map, final ConditionalExpression condition) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Evaluating on map \n" + map + "\ncondition \n" + condition);
		}
		try {
			Script script = preprocess(condition);
			return (Boolean) script.invokeMethod(EVAL_METHOD, new Object [] {map});
		} catch (Exception e) {
			throw new EpServiceException("Exception evaluating condition \n"
					+ condition.getConditionString()
					+ "\n\tOn\n" + map, e);
		}
	}
	
	/**
	 * Apply any preprocessing and caching necessary for condition scripts.
	 * A groovy shell instance is used to parse the script.
	 * 
	 * @param condition the condition to process
	 * @return groovy script object for the condition
	 * @throws Exception - throws etopxception if future task fails
	 */
	Script preprocess(final ConditionalExpression condition) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing expression " + condition.getConditionString());
		}
		FutureTask<Script> compilationTask = getScriptCompilationTaskAtomic(condition);	
		return compilationTask.get();
	}

	private FutureTask<Script> getScriptCompilationTaskAtomic(final ConditionalExpression condition) throws Exception {
		boolean newlyCreated = false;
		FutureTask<Script> compilationTask;
		synchronized (scriptCache) {
			compilationTask = scriptCache.get(condition.getConditionString());

			if (compilationTask == null) {
				newlyCreated = true;
				compilationTask = new FutureTask<Script>(new Callable<Script>() {
					public Script call() {
						return new GroovyShell().parse(expressionBuilder.buildExpression(condition.getConditionString()));
					}
				});
				scriptCache.put(condition.getConditionString(), compilationTask);
			}
		}
		if (newlyCreated) {
			compilationTask.run();
		}
		return compilationTask;
	}
	
	/**
	 * Set the timeout value of the condition script cache.
	 * 
	 * @param timeoutMillis time in milliseconds
	 */
	public void setConditionCacheTimeout(final long timeoutMillis) {
		this.scriptCache.setTimeout(timeoutMillis);
	}
	
	/**
	 * Expression builder class for constructing a groovy script from DSL string.
	 * Can be factored out of this class if eventually shared.
	 */
	class GroovyExpressionBuilder {
		/* Example completed script:
			def runOnMap(map) {
			   def out =           
				{AND
					{ location.contains "us" }
					{ memberType.contains "poor" }
	            }
			  }
			  new MapRunner(map).run(out)
			}
		 */
		/**
		 * Defines script prefix that provides scaffold code for evaluating conditions.
         * All DSL strings are first routed to LogicalOperator class to handle AND/OR/NOT.
		 */
		static final String DEF =
			"import com.elasticpath.tags.engine.*\n"
			+ "def methodMissing(String name, args) {\n"
			+ "  new LogicalOperator().invokeMethod(name, args)\n"
			+ "}\n"
			+ "def " + EVAL_METHOD + "(map) {\n"
			+ "   def out =\n";
		/**
		 * Defines script suffix that provides scaffold code for evaluating conditions.
		 */
		static final String CLOSE = "\n"
				+ "new MapRunner(map).run(out)\n}";
		
		/**
		 * Build a groovy method script from a conditional string.
		 * Returns a default of "true" if no conditions are given.
		 * @param script conditional
		 * @return runnable script
		 */
		public String buildExpression(final String script) {
			if (StringUtils.isEmpty(script)) {
				return DEF + "{ true }" + CLOSE;
			}
			return DEF + script + CLOSE;
		}
	}
}
