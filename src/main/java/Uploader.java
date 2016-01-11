import java.io.File;

public class Uploader {

	public Uploader() {

	}

	public Uploader(String rootDir) {
		File file = new File(rootDir);
		findJsonFile(file);
	}

	private void findJsonFile(File file) {
		int i;
		File[] files = file.listFiles();
		for (i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				findJsonFile(files[i]);
			}
			if (files[i].getName().endsWith(".json")) {
				uploadAll(files[i].getParentFile());
			}
		}
	}

	private void uploadAll(File file) {
		new JsonParser(file);
	}

}
