
/*Copyright (c) 2015 "hbz"

This file is part of ebooky.

ebooky is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raul Vasi
 *
 */
public class HttpClient1 {
	Properties prop = new Properties();
	URL url;
	HttpURLConnection httpConn;
	String basicAuth;
	String userpass;
	final static Logger logger = LoggerFactory.getLogger(HttpClient1.class);

	/**
	 * Constructor to initialize the connection
	 * 
	 * @param action
	 *            includin a String value of Namespace, ID, /Data etc..
	 */
	public HttpClient1(String action) {
		loadProperties();
		setURL(action);
		setAuthorization();
	}

	private void loadProperties() {
		try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(
				new File(Thread.currentThread().getContextClassLoader().getResource("config.properties").getPath())))) {
			prop.load(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setURL(String action) {
		try {
			url = new URL(prop.getProperty("Upload.URL") + File.separator + action);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setAuthorization() {
		try {
			httpConn = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		userpass = prop.getProperty("user") + ":" + prop.getProperty("password");
		basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
		httpConn.setRequestProperty("Authorization", basicAuth);
	}

	/**
	 * Methode to creating and uploading a new json Object
	 * 
	 * @param contentType
	 * @param accessScheme
	 * @param publishScheme
	 * @param parentPid
	 */
	public void newObject(String contentType, String accessScheme, String publishScheme, String parentPid) {

		httpConn.disconnect();
		String content;
		if (parentPid != null) {
			content = "{" + contentType + "," + accessScheme + "," + publishScheme + "," + parentPid + "}";
		} else {
			content = "{" + contentType + "," + accessScheme + "," + publishScheme + "}";
		}
		logger.info("PUT object on " + url + " as: " + prop.getProperty("user") + " with content: " + content);

		httpConn.setRequestProperty("Content-Type", "application/json");
		httpConn.setRequestProperty("Accept", "application/json");
		httpConn.setDoOutput(true);

		try {
			httpConn.setRequestMethod("PUT");
			httpConn.connect();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try (OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream())) {
			out.write(content);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			logger.info("Server respond: " + httpConn.getResponseCode() + " , " + httpConn.getResponseMessage() + "\n");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		httpConn.disconnect();

	}

	/**
	 * Methode to upload a data file.
	 * 
	 * @param uploadFile
	 */
	public void putData(File uploadFile) {
		String fileName = uploadFile.getName();
		String fieldName = "data";
		String boundary = "" + System.currentTimeMillis() + "";

		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		httpConn.setRequestProperty("file", fileName);

		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		logger.info("PUT Data file on " + url);
		logger.info("Uploading Data file: " + uploadFile);
		logger.info("Writing header:" + httpConn.getRequestProperties());
		try {
			httpConn.setRequestMethod("PUT");
		} catch (ProtocolException e1) {
			e1.printStackTrace();
		}

		OutputStream outputStream = null;
		try {
			outputStream = (httpConn.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String LINE_FEED = "\r\n";
		logger.debug("Writing body");
		writer.append("--" + boundary).append(LINE_FEED);
		logger.debug("Boundary: " + boundary);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
				.append(LINE_FEED);
		logger.debug("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"");
		writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
		logger.debug("Content-Type: " + URLConnection.guessContentTypeFromName(fileName));
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		logger.debug("Content-Transfer-Encoding: binary");
		writer.append(LINE_FEED);

		writer.flush();

		fileToOutputStream(uploadFile, outputStream);
		try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		writer.append(LINE_FEED);
		writer.flush();
		writer.close();
		httpConn.disconnect();

		try {
			logger.info("Server respond: " + httpConn.getResponseCode() + " , " + httpConn.getResponseMessage() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void fileToOutputStream(File uploadFile, OutputStream outputStream) {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(uploadFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		try {
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Methode to upload a Metadata file
	 * 
	 * @param file
	 */
	public void addMetadata(File file) {
		logger.info("PUT Metadata on " + url + " as: " + prop.getProperty("user"));
		logger.info("Uploading Metadata file: " + file);
		httpConn.setRequestProperty("content-type", "text/plain; charset=utf-8");
		httpConn.setDoOutput(true);
		try {
			httpConn.setRequestMethod("PUT");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		try {
			httpConn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream out = null;
		try {
			out = httpConn.getOutputStream();
			fileToOutputStream(file, out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpConn.disconnect();

		try {
			logger.info("Server respond: " + httpConn.getResponseCode() + " , " + httpConn.getResponseMessage() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
