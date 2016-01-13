
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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SuppressWarnings("javadoc")
public class UploadTest {

	static String user = "edoweb-admin";
	static String password = "admin";
	static URL url;
	static String basicAuth;
	static HttpURLConnection httpCon;

	@BeforeClass
	public static void initConfigs() throws IOException {
		try {
			url = new URL("http://localhost:9000/resource/frl:6376984");
			httpCon = (HttpURLConnection) url.openConnection();
			String userpass = user + ":" + password;
			basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
			httpCon.setRequestProperty("Authorization", basicAuth);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void uploader_test() {

		Uploader up = new Uploader("/home/raul/test/frl%3A6376984");

	}

	@Test
	public void http_delete_Test() {
		try {
			// url = new URL(url.toString() + pid);
			System.out.println(url);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestProperty("Authorization", basicAuth);
			httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("DELETE");
			httpCon.getInputStream();
			System.out.println(httpCon.getResponseCode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void test_with_javaLib() {
		httpCon.disconnect();
		try {
			// readStream(httpCon.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		httpCon.setRequestProperty("Content-Type", "application/json");
		httpCon.setRequestProperty("Accept", "application/json");
		httpCon.setDoOutput(true);
		try {
			httpCon.setRequestMethod("PUT");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		try {
			httpCon.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String content = "{\"contentType\":\"monograph\",\"accessScheme\":\"public\",\"publishScheme\":\"public\"}";
		System.out.println(content);
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(httpCon.getOutputStream());
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			System.out.println(httpCon.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void upload_metas_test() {
		httpCon.setRequestProperty("content-type", "text/plain; charset=utf-8");
		httpCon.setDoOutput(true);
		try {
			httpCon.setRequestMethod("PUT");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		try {
			httpCon.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStream out = null;
		try {
			out = httpCon.getOutputStream();
			File uploadFile = new File("/home/raul/test/frl%3A6376984/6376984_metadata.nt");
			fileToOutputStream(uploadFile, out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpCon.disconnect();

		try {
			System.out.println(httpCon.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static void readStream(InputStream in) {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println("Tag " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

	@Test
	public void test_upload_data() {
		try {

			try {
				url = new URL("https://api.localhost/resource/frl:6376979/data");
				httpCon = (HttpsURLConnection) url.openConnection();
				String userpass = user + ":" + password;
				basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
				httpCon.setRequestProperty("Authorization", basicAuth);
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<String> response = new ArrayList<String>();
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("Accept", "application/json");
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			httpCon.setReadTimeout(5000);
			String content = "{\"contentType\":\"monograph\",\"accessScheme\":\"public\",\"publishScheme\":\"public\"}";
			try (OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream())) {
				out.write(content);
			}
			System.out.println("HELLO");
			// System.out.println(httpCon.getResponseCode());

			httpCon.getInputStream();
			System.out.println("HELLO");
			int status = httpCon.getResponseCode();
			System.out.println("HELLO");
			if (status == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					response.add(line);
				}
				reader.close();
				httpCon.disconnect();
			} else {
				throw new IOException("Server returned non-OK status: " + status);
			}

			// httpCon.disconnect();
			System.out.println("Status: " + status + "\nResponse: " + response.toString());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void put_data_test() {
		String pid = "frl:6376979";
		try {
			System.out.println(url);
			String charset = "UTF-8";
			File file = new File("/home/raul/test/frl%3A6376984/6376986.pdf");
			FileInputStream fi = new FileInputStream(file);

			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setRequestMethod("PUT");
			httpCon.setRequestProperty("Connection", "Keep-Alive");
			httpCon.setRequestProperty("Content-Type", "application/pdf");
			httpCon.setRequestProperty("type", "multipart/form-data");
			httpCon.setRequestProperty("Accept", "application/data");
			httpCon.setRequestProperty("uploaded_file", file.getPath());
			System.out.println(file.getPath());
			DataOutputStream out = new DataOutputStream(httpCon.getOutputStream());
			System.out.println("130");
			Files.copy(file.toPath(), out);
			int bytesRead;
			byte[] dataBuffer = new byte[1024];
			while ((bytesRead = fi.read(dataBuffer)) != -1) {
				out.write(dataBuffer, 0, bytesRead);
			}
			System.out.println("137");
			out.flush();
			out.close();
			System.out.println("140");
			System.out.println("142");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void form_test() {
		try {
			url = new URL("http://localhost:9000/resource/frl:6376982/data");
			httpCon = (HttpURLConnection) url.openConnection();
			String userpass = user + ":" + password;
			basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
			httpCon.setRequestProperty("Authorization", basicAuth);
			String fieldName = "data";
			File uploadFile = new File("/home/raul/test/frl%3A6376982/6376990.pdf");
			String boundary = "" + System.currentTimeMillis() + "";

			httpCon.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			httpCon.setRequestProperty("file", "6376986.pdf");

			httpCon.setUseCaches(false);
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);

			httpCon.setRequestMethod("PUT");

			OutputStream outputStream = null;
			try {
				outputStream = (httpCon.getOutputStream());
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
			String fileName = uploadFile.getName();
			writer.append("--" + boundary).append(LINE_FEED);
			System.out.println("--" + boundary + (LINE_FEED));
			writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
					.append(LINE_FEED);
			System.out.println("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName
					+ "\"" + (LINE_FEED));
			writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
			System.out.println("Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + (LINE_FEED));
			writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
			System.out.println("Content-Transfer-Encoding: binary" + (LINE_FEED));
			writer.append(LINE_FEED);

			writer.flush();

			fileToOutputStream(uploadFile, outputStream);

			// httpCon.getInputStream();
			try {
				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			writer.append(LINE_FEED);
			writer.flush();
			writer.close();
			httpCon.disconnect();

			try {
				System.out.println(httpCon.getResponseCode());
				System.out.println(httpCon.getResponseMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
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

	public String receiveResponse(HttpURLConnection connection) throws IOException {
		String result = null;
		StringBuffer sb = new StringBuffer();
		InputStream is = null;

		try {
			is = new BufferedInputStream(connection.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			result = sb.toString();
		} catch (Exception e) {
			result = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public byte[] convertToByteArray(String filePath) {
		File f = new File(filePath);
		byte[] retVal = new byte[(int) f.length()];
		try {
			FileInputStream fis = new FileInputStream(f);
			fis.read(retVal);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex2) {
			ex2.printStackTrace();
		}
		return retVal;
	}

	@Test
	public void appache_test() throws ClientProtocolException, IOException {

		String user = "edoweb-admin";
		String password = "admin";
		String encoding = Base64.encodeBase64String((user + ":" + password).getBytes());
		System.out.println(encoding);
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
		provider.setCredentials(AuthScope.ANY, credentials);
		URL myurl = new URL("http://localhost:9000/resource/frl:6376984/data");

		// ----------------------------------------------------------------------------

		String boundary = "==" + System.currentTimeMillis() + "===";
		String filePath = "/home/raul/test/frl%3A6376982/123.txt";
		File file = new File(filePath);
		String fileLength = Long.toString(file.getTotalSpace());
		HttpEntity entity = MultipartEntityBuilder.create().addTextBody("data", boundary)
				.addBinaryBody("data", file, ContentType.create("application/pdf"), "6376986.pdf").build();
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

		HttpPut put = new HttpPut(myurl.toString());
		// put.setHeader("Authorization", "Basic " + encoding);
		// put.setHeader("Accept", "*/*");
		// put.setHeader("Content-Length", fileLength);
		put.setEntity(entity);
		HttpResponse response = client.execute(put);
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(statusCode);
		Assert.assertEquals(statusCode, HttpStatus.SC_OK);
	}

	@Test
	public void java_lib_put() {
		try {
			URL myurl;
			HttpURLConnection conn;
			String port = "9000";
			String user = "edoweb-admin";
			String password = "admin";
			String encoding = Base64.encodeBase64String((user + ":" + password).getBytes());
			String boundary = "" + System.currentTimeMillis() + "";
			String crlf = "\r\n";
			String twoHyphens = "--";
			String attachmentName = "data";
			File uploadFile = new File("/home/raul/test/frl%3A6376984/123.txt");
			String attachmentFileName = uploadFile.getName();

			DataOutputStream request;

			myurl = new URL("http://localhost:9000/resource/frl:6376984/data");
			System.out.println(myurl.toString());
			conn = (HttpURLConnection) myurl.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Authorization", " Basic " + encoding);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			conn.setRequestProperty("Content-Length", String.valueOf(uploadFile.length()));
			conn.setRequestProperty("Accept", "*/*");
			conn.connect();

			FileInputStream fis = new FileInputStream(uploadFile);
			// BufferedInputStream buf = new BufferedInputStream(fis);
			request = new DataOutputStream(conn.getOutputStream());
			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\""
					+ attachmentFileName + "\"" + crlf);
			request.writeBytes("Content-Type: application/pdf");

			request.writeBytes(crlf);

			byte[] streamFileBytes = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = fis.read(streamFileBytes)) != -1) {
				request.write(streamFileBytes, 0, bytesRead);
			}

			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + crlf);
			request.close();
			conn.disconnect();
			System.out.println(conn.getResponseCode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void put_parent_test() {
		try {
			URL url = new URL("http://localhost:9000/resource/frl:6376983");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("PUT");
			con.setRequestProperty("Authorization", basicAuth);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.connect();

			OutputStream outputStream = con.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
			writer.append(
					"{\"contentType\":\"file\",\"parentPid\":\"frl:6376984\",\"accessScheme\":\"public\",\"publishScheme\":\"public\"}");
			writer.flush();
			outputStream.close();
			writer.close();
			con.disconnect();
			System.out.println(con.getResponseCode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void jackson_test() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new File("/home/raul/test/frl%3A6376984/6376984.json"));

			JsonNode curValue = root.at("/hasPart");
			System.out.println("curValue: " + curValue);
			if (curValue.isArray()) {
				ArrayNode hasPartArray = (ArrayNode) curValue;
				for (int i = 0; i < hasPartArray.size(); i++) {
					JsonNode curPart = getFirst((ObjectNode) hasPartArray.get(i));
					System.out.println("curPart: " + curPart);
					System.out.println("Found " + curPart);
				}

			} else {
				throw new RuntimeException(
						"Unexpected type " + curValue.getNodeType() + " found: " + curValue.asText());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JsonNode getFirst(JsonNode c) {
		String key = c.findValue("@id").asText();
		JsonNode curPart = c.get(key);
		return curPart;
	}

	@Test
	public void loop_json() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new File("/home/raul/test/frl%3A6376984/6376984.json"));
			JsonNode jon1 = null, jon2 = null, jon3 = null, jon4 = null;

			if (root.has("hasPart")) {

				jon1 = root.at("/hasPart");
				jon1 = getFirst(jon1.get(0));
			}
			System.out.println(jon1);

			if (jon1.has("hasPart")) {
				jon2 = jon1.at("/hasPart");
				jon2 = getFirst(jon2.get(0));
			}
			System.out.println(jon2);

			if (jon2.has("hasPart")) {
				jon3 = jon2.at("/hasPart");
				jon3 = getFirst(jon3.get(0));
			}
			System.out.println(jon3);

			if (jon3.has("hasPart")) {
				jon4 = jon3.at("/hasPart");
				jon4 = getFirst(jon4.get(0));
			} else {
				System.out.println("HAS JUST !hasPart!");
			}
			System.out.println(jon4);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void loopArray(JsonNode jon) {
		JsonNode curValue = jon.at("/hasPart");
		for (int i = 0; i < curValue.size(); i++) {
			jon = getFirst(jon.get(i));
			System.out.println("Found:" + jon);
		}

	}

	@Test
	public void test_1() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper
					.readTree(new File("/home/raul/workspace/Regal-Upload/target/test-classes/jsonobj.json"));
			// System.out.println(root);
			processObject(root);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void processObject(JsonNode jon) {
		getMainObject(jon);
		jon = jon.at("/hasPart");
		for (int i = 0; i < jon.size(); i++) {
			createObject(getFirst(jon.get(i)));
			createChildren(getFirst(jon.get(i)));

		}
	}

	private void getMainObject(JsonNode jon) {
		System.out.println("Main " + jon.get("@id"));
		System.out.println(jon.get("contentType"));
		System.out.println(jon.get("accessScheme"));
		System.out.println(jon.get("publishScheme"));
		System.out.println(jon.get("parentPid"));
	}

	private void createObject(JsonNode jon) {
		System.out.println("+++++createObject:+++++++");
		if (jon.has("hasData")) {
			System.out.println("Data: " + jon.get("hasData").get("@id"));
		}
		System.out.println("create: " + jon.get("@id"));
		System.out.println(getContentType(jon));
		System.out.println("parentPid: " + jon.get("parentPid"));
		System.out.println("----- ----- -----");
	}

	private String getContentType(JsonNode jon) {
		String content = jon.get("contentType").toString();
		return "\"contentType\" : " + content;
	}

	private void createChildren(JsonNode jon) {

		if (jon.has("hasPart")) {
			jon = jon.at("/hasPart");
			for (int i = 0; i < jon.size(); i++) {

				createObject(getFirst(jon.get(i)));
				processObject(getFirst(jon.get(i)));
			}
		}

	}

	@Test
	public void dir_itr_test() {
		File file = new File("/home/raul/test/regal-mirror1");

		findJsonFiles(file);
	}

	private void findJsonFiles(File file) {
		int i;
		File[] files = file.listFiles();
		for (i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				// System.out.println(files[i]);
				System.out.println(i);
				findJsonFiles(files[i]);
			}
			if (files[i].getName().endsWith(".json")) {
				getRelativeDir(files[i]);
			}
		}
	}

	private void getRelativeDir(File file) {
		try {
			System.out.println(file.getParentFile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startParsing(File file) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(file);
			// System.out.println(root);
			processObject(root);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void dir_arr_tst() {
		File file = new File("/home/raul/test/frl%3A6376984");
		File[] files = file.listFiles();
		int n = foundDatadirs(files);
		System.out.println(n);
	}

	private int foundDatadirs(File[] files) {
		int n = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".json")) {
				n++;
				System.out.println(n);
			}
		}
		return n;
	}

}
