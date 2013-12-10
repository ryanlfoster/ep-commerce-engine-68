package com.elasticpath.test.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Utility methods to ease persistent test case setup. Some code is taken from http://www.javaalmanac.com.
 */
public class Utils {
	
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	/** Sequence number to ensure uniqueness of {@link #uniqueCode(String)}. */
	private static int seq;
	
	/** default email domain. */
	public static final String EMAIL_DOMAIN = "@qa.local";

	/**
	 * Copies src file to dst file. If the dst file does not exist, it is created
	 * 
	 * @param src source file.
	 * @param dst destination file.
	 * @throws IOException in case of any problem with files.
	 */
	public static void copy(final File src, final File dst) throws IOException {
		final InputStream in = new FileInputStream(src);
		final OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Copies all files under srcDir to dstDir. If dstDir does not exist, it will be created.
	 * 
	 * @param srcDir source directory.
	 * @param dstDir destination directory.
	 * @throws IOException in case of any problem with files.
	 */
	public static void copyDirectory(final File srcDir, final File dstDir) throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdir();
			}

			String[] children = srcDir.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
			}
		} else {
			copy(srcDir, dstDir);
		}
	}

	/**
	 * Create a unique string code.
	 * 
	 * !!!! DO NOT USE THIS !!!!
	 * Please spend some time and think of some predictable code to give your objects, since
	 * QA write fit test and try to match these unique values!!!!
	 * 
	 * @param prefix the prefix for the unique code.
	 * @return a unique code prefixed with <code>prefix</code>
	 */
	public static String uniqueCode(final String prefix) {
		return prefix + System.currentTimeMillis() + ++seq;
	}

	/**
	 * Create a unique email address.
	 * 
	 * @param prefix the prefix for the unique code.
	 * @return a unique email address prefixed with <code>prefix</code> with domain of 
	 * 		<code>EMAIL_DOMAIN</code>
	 */
	public static String uniqueEmailAddress(final String prefix) {
		return uniqueCode(prefix) + EMAIL_DOMAIN;
	}
	
	/**
	 * Converts <code>String</code> to <code>Date</code>.
	 * 
	 * @param timestamp string date format.
	 * @return date.
	 * @throws ParseException
	 */
	public static Date parseDate(final String timestamp) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
		try {
			return sdf1.parse(timestamp);
		} catch (ParseException e) {
		}

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		try {
			return sdf2.parse(timestamp);
		} catch (ParseException e) {
		}
		return null;
	}

	/**
	 * Converts <code>Date</code> to <code>String</code>.
	 * 
	 * @param timestamp string date format.
	 * @return string representation of date.
	 * @throws ParseException
	 */
	public static String parseString(final Date timestamp) {
		if (timestamp == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);

		return sdf.format(timestamp);
	}

	/**
	 * Sets scale of 2 to the specified big decimal. Some DB implementations return 1 instead of 1.00 for integer types. FIT expects a value of scale
	 * 2.
	 * 
	 * @param bigDecimal to convert
	 * @return big decimal of the scale 2.
	 */
	public static BigDecimal convert(final BigDecimal bigDecimal) {
		return bigDecimal.setScale(2);
	}

	/**
	 * Converts xml document to a string.
	 * 
	 * @param document document that represents XML.
	 * @return string representation of org.w3c.dom.Document
	 */
	public static String displayXML(final Document document) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer;

			transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source, result);
			return new String(outputStream.toByteArray());
		} catch (TransformerConfigurationException e) {

		} catch (TransformerException e) {

		}
		return null;
	}
}
