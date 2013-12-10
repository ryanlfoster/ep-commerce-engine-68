package com.elasticpath.epcoretool.logic;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.NotImplementedException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RecompilingRuleEngine;
import com.elasticpath.service.rules.impl.DBCompilingRuleEngineImpl;


/**
 * The Class AbstractRecompileRuleBase.
 */
public abstract class AbstractRecompileRuleBase extends AbstractEpCore {

	/**
	 * Instantiates a new abstract recompile rule base.
	 *
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractRecompileRuleBase(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}
	
	/**
	 * Execute.
	 */
	public void execute() {
		RecompilingRuleEngine ruleEngine = epCore().getRuleEngine();
		((DBCompilingRuleEngineImpl) ruleEngine).setTimeService(new StubTimeService());
		ruleEngine.recompileRuleBase();
	}

	/**
	 * A stub implementation of {@link TimeService} to ensure that the rules are built on every invocation of this command.
	 */
	private class StubTimeService implements TimeService {

		private static final String NOT_IMPLEMENTED_MSG = "Not implemented for stub service";

		@Override
		public void setElasticPath(final ElasticPath elasticpath) {
			throw new NotImplementedException(NOT_IMPLEMENTED_MSG);
		}

		@Override
		public ElasticPath getElasticPath() {
			throw new NotImplementedException(NOT_IMPLEMENTED_MSG);
		}

		@Override
		public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
			throw new NotImplementedException(NOT_IMPLEMENTED_MSG);
		}

		@Override
		public PersistenceEngine getPersistenceEngine() {
			throw new NotImplementedException(NOT_IMPLEMENTED_MSG);
		}

		@Override
		public Object getObject(final long uid, final Collection<String> fieldsToLoad) throws EpServiceException {
			throw new NotImplementedException(NOT_IMPLEMENTED_MSG);
		}

		@Override
		public Object getObject(final long uid) throws EpServiceException {
			throw new NotImplementedException(NOT_IMPLEMENTED_MSG);
		}

		@Override
		public Date getCurrentTime() {
			return new Date(0);
		}
	}
}
