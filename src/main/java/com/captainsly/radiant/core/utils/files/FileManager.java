package com.captainsly.radiant.core.utils.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {

	private FileManager() {
	}

	public String getFileContents(String filePath) {
		StringBuilder sb = new StringBuilder();
		try {

			BufferedReader r = new BufferedReader(new FileReader(new File(filePath)));

			String line = "";
			while ((line = r.readLine()) != null) {
				sb.append(line).append(System.lineSeparator());
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static FileManager getInstance() {
		return new FileManager();
	}

}
