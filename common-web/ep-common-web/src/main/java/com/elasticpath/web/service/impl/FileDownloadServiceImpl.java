package com.elasticpath.web.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.web.service.FileDownloadService;


/**
 * Provides the ability to send file contents to a browser.
 */
public class FileDownloadServiceImpl implements FileDownloadService {

	private static final String MSPOWERPOINT = "application/mspowerpoint";

	private static final String DIGITAL_ASSET = "Digital Asset: ";

	private static final int FILE_READ_BUFFER_SIZE = 1024;

	private static final Map<String, String> FILE_EXT_TO_RESPONSE = new HashMap<String, String>();

	static {
		FILE_EXT_TO_RESPONSE.put("mp3", "audio/mpeg");
		FILE_EXT_TO_RESPONSE.put("exe", "application/octet-stream");
		FILE_EXT_TO_RESPONSE.put("doc", "application/msword");
		FILE_EXT_TO_RESPONSE.put("pot", MSPOWERPOINT);
		FILE_EXT_TO_RESPONSE.put("pps", MSPOWERPOINT);
		FILE_EXT_TO_RESPONSE.put("ppt", MSPOWERPOINT);
		FILE_EXT_TO_RESPONSE.put("ppz", MSPOWERPOINT);
		FILE_EXT_TO_RESPONSE.put("xls", "application/x-excel");
		FILE_EXT_TO_RESPONSE.put("pdf", "application/pdf");
		FILE_EXT_TO_RESPONSE.put("mpg", "video/mpeg");
		FILE_EXT_TO_RESPONSE.put("avi", "video/x-msvideo");
		FILE_EXT_TO_RESPONSE.put("jpg", "image/jpeg");
		FILE_EXT_TO_RESPONSE.put("jpeg", "image/jpeg");
		FILE_EXT_TO_RESPONSE.put("gif", "image/gif");
		FILE_EXT_TO_RESPONSE.put("zip", "application/zip");
		FILE_EXT_TO_RESPONSE.put("htm", "text/html");
		FILE_EXT_TO_RESPONSE.put("html", "text/html");
		FILE_EXT_TO_RESPONSE.put("xml", "application/xml");
	}
	/**
	 * Read the input file content and output the stream to client (Browser).
	 *
	 * @param request the download request
	 * @param response the response
	 * @param fullFilePath the full file path on server
	 * @throws EpSystemException - if any error
	 */
	@SuppressWarnings("PMD.DoNotThrowExceptionInFinally")
	public void download(final HttpServletRequest request, final HttpServletResponse response, final String fullFilePath) throws EpSystemException {

		File dataFile = new File(fullFilePath);
		int fileNameStart = fullFilePath.lastIndexOf(File.separator);
		String fileName = fullFilePath.substring(fileNameStart + 1);

		if (dataFile.isFile() && dataFile.canRead()) {

			// Set the response content typeR
			response.addHeader("content-disposition", "attachment; filename=\"" + fileName + "\"");

			int start = fullFilePath.lastIndexOf('.');
			String extension = fullFilePath.substring(start + 1, fullFilePath.length()).toLowerCase();

			if (FILE_EXT_TO_RESPONSE.get(extension) == null) {
				response.setContentType("application/unknown");
			} else {
				response.setContentType(FILE_EXT_TO_RESPONSE.get(extension));
			}

			// Read file and write output stream
			FileInputStream read = null;
			OutputStream write = null;
			try {
				read = new FileInputStream(dataFile);
				write = response.getOutputStream();
				byte[] chars = new byte[FILE_READ_BUFFER_SIZE];
				int byteCount;
				while ((byteCount = read.read(chars)) != -1) {
					write.write(chars, 0, byteCount);
				}
			} catch (FileNotFoundException e) {
				throw new EpSystemException(DIGITAL_ASSET + fileName + " could not be found.", e);
			} catch (IOException e) {
				throw new EpSystemException(DIGITAL_ASSET + fileName + " could not be accessed.", e);
			} finally {
				try {
					if (read != null) {
						read.close();
					}
					if (write != null) {
						write.close();
					}
				} catch (IOException e) {
					throw new EpSystemException(DIGITAL_ASSET + fileName + " could not be accessed.", e);
				}
			}

		} else {
			throw new EpSystemException(DIGITAL_ASSET + fileName + " could not be accessed: " + dataFile.getAbsolutePath());
		}

	}

}
