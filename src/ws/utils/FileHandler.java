package ws.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandler {

	/**
	 * Takes a string containing a path to a file to be read. Then reads each
	 * line from the file into an ArrayList of Strings.
	 * 
	 * @param path
	 *            A String containing the path to the file to be read.
	 * @return an ArrayList of strings containing every line in the read file.
	 * 
	 * @exception FileNotExceptionFound
	 *                if no file is found at the specified path
	 * @exception IOException
	 *                can occur for several reasons related to I/O operations.
	 */
	public static ArrayList<String> readFile(String path) {

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			ArrayList<String> content = new ArrayList<String>();
			String line = br.readLine();

			while (line != null) {
				content.add(line);
				line = br.readLine();
			}
			return content;
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("File read error");
		}
		return null;
	}

	/**
	 * Takes a String containing a path and a String containing the content to
	 * save. Then writes the content to a file specified by the path. A new file
	 * is created if no file exists on the specified path.
	 * 
	 * @param path
	 *            The file path
	 * @param content
	 *            The content to write to file
	 * @return true if successfully saved, false otherwise
	 * 
	 * @exception FileNotFoundException
	 *                when the specified path is not available, for example when
	 *                attempting to write to a read-only directory.
	 * @exception UnsupportedEncodingException
	 *                when the specified encoding is not supported.
	 * 
	 */
	public static boolean saveFile(String path, String content) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(path, "UTF-8");
			pw.println(content.trim());
			pw.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
}