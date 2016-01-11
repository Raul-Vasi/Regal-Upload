import java.io.File;
import java.io.IOException;

import org.omg.Messaging.SyncScopeHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
	File dir;
	HttpClient1 client;

	public JsonParser(File dir) {
		this.dir = dir;
		System.out.println(dir);
		parseObject(findJsonFile());
	}

	private void parseObject(File jsonFile) {
		System.out.println(jsonFile.getAbsolutePath());

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
				break;
			}
		}
		client = new HttpClient1(jon.get("@id").asText() + File.separator + "metadata");
		client.addMetadata(file[i]);
	}

	private void processObject(JsonNode jon) {

		jon = jon.at("/hasPart");
		for (int i = 0; i < jon.size(); i++) {
			createObject(getFirst(jon.get(i)));
			createChildren(getFirst(jon.get(i)));

		}

	}

	private void createMainObject(JsonNode jon) {
		client = new HttpClient1(jon.get("@id").asText());
		client.newObject(getContentType(jon), getAccessScheme(jon), getPublishScheme(jon), null);
	}

	private void createObject(JsonNode jon) {
		System.out.println("+++++createObject:+++++++");

		System.out.println("create: " + jon.get("@id"));
		client = new HttpClient1(jon.get("@id").asText());
		client.newObject(getContentType(jon), getAccessScheme(jon), getPublishScheme(jon), getParentPid(jon));
		if (jon.has("hasData")) {
			System.out.println("Data: " + jon.get("hasData").get("@id"));
			client = new HttpClient1(jon.get("hasData").get("@id").asText());
			client.putData(getFilename(jon));
		}

		System.out.println("parentPid: " + jon.get("parentPid"));
		System.out.println("----- ----- -----");
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
		System.out.println("Filename: " + file.getName());
		return file;
	}

	private File findJsonFile() {
		int i;
		File[] files = dir.listFiles();
		for (i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith("json")) {
				break;
			}
		}
		if (files[i].getName().endsWith("json")) {
			return files[i];
		} else {
			System.out.println("Path" + dir + "No Json File was found!");
			return null;
		}
	}
}
