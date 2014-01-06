package data_preprocessing.userinput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class delete_output_folder {
	public static void main(String[] args) throws IOException {
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String filepath = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				filepath = line_c.split("n:")[1].trim();
			}
		}
		config.close();
		File f = new File(filepath);
		deleteFolder(f);
		System.out.println("COMPLETED!!!");
	}

	private static void deleteFolder(File f) throws IOException {

		File[] lev1 = f.listFiles();
		for (File file : lev1) {
			if (file.isDirectory() && file.getName().equals("output")) {
				delete(file);
			} else {
				if (file.isDirectory())
					deleteFolder(file);
			}
		}

	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
			} else {
				// list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}
				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}
		} else {
			// if file, then delete it
			file.delete();
		}
	}
}
