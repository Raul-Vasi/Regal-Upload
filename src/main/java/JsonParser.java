
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

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Raul Vasi
 *
 */
public class JsonParser {
	File dir;
	HttpClient1 client;
	final static Logger logger = LoggerFactory.getLogger(JsonParser.class);

	/**
	 * Constructor to start parsing the jons file
	 * 
	 * @param dir
	 *            the directory that contains the json file
	 */
	public JsonParser(File dir) {
		this.dir = dir;
		logger.info("Data Directory: " + this.dir);
		parseObject(findJsonFile());
		endMasg();
	}

	private void parseObject(File jsonFile) {
		logger.info("Parsing json file:" + jsonFile.getAbsolutePath());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(jsonFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		createMainObject(root);
		addMetadataFile(root);
		processObject(root);

	}

	private void addMetadataFile(JsonNode jon) {
		int i;
		File[] file = dir.listFiles();
		for (i = 0; i < file.length; i++) {
			if (file[i].getName().endsWith(".nt")) {
				client = new HttpClient1(jon.get("@id").asText() + File.separator + "metadata");
				client.addMetadata(file[i]);
				break;
			}
		}
		if (!file[i].getName().endsWith(".nt")) {
			logger.info("No Metadata file was found");
		}

	}

	private void processObject(JsonNode jon) {

		jon = jon.at("/hasPart");
		for (int i = 0; i < jon.size(); i++) {
			createObject(getFirst(jon.get(i)));
			createChildren(getFirst(jon.get(i)));

		}

	}

	private void createMainObject(JsonNode jon) {
		logger.info("Create main Object /" + jon.get("@id").asText());
		client = new HttpClient1(jon.get("@id").asText());
		client.newObject(getContentType(jon), getAccessScheme(jon), getPublishScheme(jon), null);
	}

	private void createObject(JsonNode jon) {
		logger.info("create object ID: " + jon.get("@id") + " with " + getParentPid(jon));
		client = new HttpClient1(jon.get("@id").asText());
		client.newObject(getContentType(jon), getAccessScheme(jon), getPublishScheme(jon), getParentPid(jon));
		if (jon.has("hasData")) {
			client = new HttpClient1(jon.get("hasData").get("@id").asText());
			client.putData(getFilename(jon));
		}
	}

	private String getParentPid(JsonNode jon) {
		String content = jon.get("parentPid").toString();
		return "\"parentPid\" : " + content;
	}

	private String getPublishScheme(JsonNode jon) {
		String content = jon.get("publishScheme").toString();
		return "\"publishScheme\" : " + content;
	}

	private String getAccessScheme(JsonNode jon) {
		String content = jon.get("accessScheme").toString();
		return "\"accessScheme\" : " + content;
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

	private JsonNode getFirst(JsonNode c) {
		String key = c.findValue("@id").asText();
		JsonNode curPart = c.get(key);
		return curPart;
	}

	private File getFilename(JsonNode jon) {
		String filename = jon.get("hasData").get("@id").asText();
		filename = filename.split(":")[1];
		filename = filename.split("/")[0] + ".pdf";
		File file = new File(dir + File.separator + filename);
		return file;
	}

	private File findJsonFile() {
		int i;
		File[] files = dir.listFiles();
		for (i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".json")) {
				break;
			}
		}
		if (files[i].getName().endsWith(".json")) {
			return files[i];
		} else {
			System.out.println("Path" + dir + "No Json File was found!");
			return null;
		}
	}

	private void endMasg() {
		logger.info("|===== Job from sorce " + dir + "/ is done! =====|");
	}
}
