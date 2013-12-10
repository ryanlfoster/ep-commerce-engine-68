package com.elasticpath.domain.rules.impl;


import static org.junit.Assert.assertEquals;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.test.TestLog4jLoggingAppender;


/** Test cases for <code>ChecksumAndLogVisitor</code>. */
public class ChecksumAndLogVisitorTest {
	
	private static final String RULE_CODE = "Rule Code";
	private static final String RULE_CODE_WITH_NEWLINES = "Rule\nCode\rWith\r\nNewlines";
	private final ChecksumAndLogVisitor visitor = new ChecksumAndLogVisitor();

	private final AppliedRule appliedRule = new AppliedRuleImpl();
	private final TestLog4jLoggingAppender appender = new TestLog4jLoggingAppender();
	/**
	 * Add appender to loggers under test.
	 */
	@Before
	public void setUp() {
		Logger log = LogManager.getLogger(PromotionLogSupport.RULECODE.getLoggerName());
		log.setLevel(Level.INFO);
		log.addAppender(appender);		
	}
	
	/**
	 * Test that the visit method logs appropriately.
	 */
	@Test
	public void testBasicLoggingAndChecksumming() {
		appliedRule.setRuleCode(RULE_CODE);
		
		// add expectations
		appender.addMessageToVerify(Level.INFO, "CHECKSUM: c38bddc59cff110525939cc537f06e0f RULECODE: Rule Code");
		

		visitor.visit(appliedRule);
		
		// ensure all the expectations are met
		appender.verify();		
	}

	
	/**
	 * We squish the log messages so they are easier to read (and sed/grep) in the log files.
	 */
	@Test
	public void logMessagesShouldAppearOnOneLine() {
		appliedRule.setRuleCode(RULE_CODE_WITH_NEWLINES);

		// add expectations
		appender.addMessageToVerify(Level.INFO, "CHECKSUM: c588f7f3e6ceeff5848c6eb707ad4975 RULECODE: Rule\\nCode\\nWith\\nNewlines");

		visitor.visit(appliedRule);
		
		appender.verify();
	}
	
	/**
	 * 
	 */
	@Test
	public void appliedRuleShouldHaveRuleCodeReplacedWithChecksumForPersisting() {
		appliedRule.setRuleCode(RULE_CODE);
		
		visitor.visit(appliedRule);
		
		assertEquals("c38bddc59cff110525939cc537f06e0f", appliedRule.getRuleCode());
	}	
}
