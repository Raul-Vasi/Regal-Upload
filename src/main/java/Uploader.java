
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

/**
 * @author Raul Vasi
 *
 */
public class Uploader {

	final static Logger logger = LoggerFactory.getLogger(Uploader.class);

	/**
	 * Constructor to start uploading
	 * 
	 * @param rootDir
	 *            directoy were the data files including json file are stored
	 * 
	 */
	public Uploader(String rootDir) {
		File file = new File(rootDir);
		logger.info("\n************************************************************" + "" + "\nReading root Directory: "
				+ rootDir + "\n************************************************************");
		findJsonFile(file);

	}

	private void findJsonFile(File file) {
		int i;
		int count = 0;
		File[] files = file.listFiles();
		int n = foundDatadirs(files);
		if (n == 0) {
			logger.error("No json file was found in the Directory tree");
			System.exit(0);
		}

		logger.info("Found: " + n + " Data Directories");
		for (i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				findJsonFile(files[i]);
			}
			if (files[i].getName().endsWith(".json")) {
				count++;
				logger.info("Start uploading " + count + " / " + n
						+ "\n------------------------------------------------------------------------------------------------");
				uploadAll(files[i].getParentFile());
			}
		}
	}

	private void uploadAll(File file) {
		new JsonParser(file);
	}

	private int foundDatadirs(File[] files) {
		int n = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".json")) {
				n++;
			}
		}
		return n;
	}

}
