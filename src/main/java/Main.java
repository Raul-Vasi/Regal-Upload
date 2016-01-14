import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @author Raul Vasi
 *
 */

public class Main {

	/**
	 * @param args
	 */

	final static Logger logger = LoggerFactory.getLogger(Uploader.class);

	public static void main(String[] args) {

		if (args.length != 0) {
			Uploader upload = new Uploader(args[0]);
		} else {
			logger.warn("No input Directory was found!\n" + "!You must introduce a valid Directory!");
			System.exit(0);
		}

	}

}
