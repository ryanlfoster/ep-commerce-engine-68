package com.elasticpath.ql.asserts;
import org.junit.Assert;

import com.elasticpath.ql.parser.EpQLParseException;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.EpQueryParser;

/**
 * Contains asserts for query parsing verification. 
 */
public final class ParseAssert {

	private ParseAssert() { };
	
	/**
	 * Asserts that the query is parsed successfully.
	 * 
	 * @param query query to parse
	 * @param parser parser
	 * @return parsed query
	 */
	public static EpQuery assertParseSuccessfull(final String query, final EpQueryParser parser) {
		try {
			return parser.parse(query);
		} catch (EpQLParseException e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Asserts that query is not parsed successfully.
	 * 
	 * @param query query to parse
	 * @param message error message
	 * @param parser parser
	 */
	public static void assertParseInvalid(final String query, final String message, final EpQueryParser parser) {
		try {
			parser.parse(query);
			Assert.fail(message);
		} catch (EpQLParseException expected) {
			Assert.assertNotNull(expected);
		}
	}
}
