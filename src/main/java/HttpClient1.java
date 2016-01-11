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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

public class HttpClient1 {
	Properties prop = new Properties();
	URL url;
	HttpURLConnection httpConn;
	String basicAuth;
	String userpass;

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
			System.out.println(url);
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
		System.out.println(userpass);
		httpConn.setRequestProperty("Authorization", basicAuth);
	}

	public void newObject(String contentType, String accessScheme, String publishScheme, String parentPid) {
		httpConn.disconnect();
		String content;
		if (parentPid != null) {
			content = "{" + contentType + "," + accessScheme + "," + publishScheme + "," + parentPid + "}";
		} else {
			content = "{" + contentType + "," + accessScheme + "," + publishScheme + "}";
		}

		System.out.println(content);

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
			System.out.println(httpConn.getResponseCode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		httpConn.disconnect();

	}

	public void putData(File uploadFile) {
		String fileName = uploadFile.getName();
		String fieldName = "data";
		String boundary = "" + System.currentTimeMillis() + "";

		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		httpConn.setRequestProperty("file", fileName);

		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

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

		writer.append("--" + boundary).append(LINE_FEED);
		System.out.println("--" + boundary + (LINE_FEED));
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
				.append(LINE_FEED);
		System.out.println("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\""
				+ (LINE_FEED));
		writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
		System.out.println("Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + (LINE_FEED));
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		System.out.println("Content-Transfer-Encoding: binary" + (LINE_FEED));
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
			System.out.println(httpConn.getResponseCode());
			System.out.println(httpConn.getResponseMessage());
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

	public void addMetadata(File file) {
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
			System.out.println(httpConn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
