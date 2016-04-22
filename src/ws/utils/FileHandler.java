package ws.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FileHandler {
	
	public static HashMap<String, String> loadCommands(String path) {
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			HashMap<String, String> commands = new HashMap<>();
		    String line = br.readLine();

		    while (line != null) {
		    	String[] lineContent = line.split("-");
		        commands.put(lineContent[0], lineContent[1]);
		        line = br.readLine();
		    }
		    return commands;
		    
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("File read error");
		}
		return null;
	}
}
