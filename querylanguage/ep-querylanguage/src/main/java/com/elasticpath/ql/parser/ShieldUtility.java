package com.elasticpath.ql.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class shielding characters with escape symbols.
 */
public final class ShieldUtility {

	private static Map<Character, String> escapeMap = new HashMap<Character, String>();
	
	private ShieldUtility() {
	}

	static {
		escapeMap.put('\\', "\\\\");
		escapeMap.put('+', "\\+");
		escapeMap.put('-', "\\-");
		escapeMap.put('&', "\\&");
		escapeMap.put('|', "\\|");
		escapeMap.put('!', "\\!");
		escapeMap.put('(', "\\(");
		escapeMap.put(')', "\\)");
		escapeMap.put('{', "\\{");
		escapeMap.put('}', "\\}");
		escapeMap.put('[', "\\[");
		escapeMap.put(']', "\\]");
		escapeMap.put('^', "\\^");
		escapeMap.put('\"', "\\&quot;");
		escapeMap.put('~', "\\~");
		escapeMap.put('*', "\\*");
		escapeMap.put('?', "\\?");
		escapeMap.put(':', "\\:");
		escapeMap.put(' ', "\\ ");
	}

	/**
	 * Shields special characters with back slash symbol.
	 * 
	 * @param inputString string to shield
	 * @return shielded string
	 */
	public static String shieldString(final String inputString) {
		final StringBuffer buffer = new StringBuffer();
		String inUse;
		for (char c : inputString.toCharArray()) {
			inUse = escapeMap.get(c);
			if (inUse == null) {
				buffer.append(c);
			} else {
				buffer.append(inUse);
			}
		}
		return buffer.toString();
	}
}
