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