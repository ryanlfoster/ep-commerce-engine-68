package com.elasticpath.domain.rules.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Level;

import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.AppliedRule.Visitor;

/**
 * This class implements a visit which can be called to log the rulecode, and checksum of the rulecode.
 */
public final class ChecksumAndLogVisitor implements Visitor {

	/**
	 * This method checksums a log friendly version of the rulecode and logs it.
	 *
	 * @param rule appliedRule to get rulecode from
	 */
	public void visit(final AppliedRule rule) {
		String logFriendlyRuleCode = makeLogFriendly(rule.getRuleCode());
		String ruleCodeChecksum = checksumRuleCode(logFriendlyRuleCode);		
		rule.setRuleCode(ruleCodeChecksum);
		logRuleCode(ruleCodeChecksum, logFriendlyRuleCode);
	}
	
	/**
	 * Takes in an unformatted rule code and returns a string whose newlines characters are replaced by a literal '\n'.
	 * '\n' was chosen for easy replacing in the log files.
	 *
	 * @param ruleCode the ruleCode to format
	 * @return
	 */
	private String makeLogFriendly(final String ruleCode) {
		return ruleCode.replaceAll("\\r\\n|\\r|\\n", "\\\\n");
	}
	/**
	 * Gives a checksum for the specified plaintext rulecode.
	 * @param plainTextRuleCode rulecode in plain text
	 */
	private String checksumRuleCode(final String plainTextRuleCode) {
		return DigestUtils.md5Hex(plainTextRuleCode);
	}
	/**
	 * Logs the given checksum and ruleCode using the PromotionRulecodeLogSupport.
	 *
	 * @param checksum the checksum of the ruleCode to log
	 * @param ruleCode the ruleCode to log
	 */
	private void logRuleCode(final String checksum, final String ruleCode) {
		PromotionLogSupport.RULECODE.log(Level.INFO, "CHECKSUM: " + checksum + " RULECODE: " + ruleCode);
	}
}