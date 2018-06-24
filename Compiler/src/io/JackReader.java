package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JackReader {

	private BufferedReader br;

	public JackReader(File f) {
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (IOException ioe) {
			System.out.println("ERR: Buffered Reader could not initialized");
			ioe.printStackTrace();
		}
	}

	public String readAll() {
		String out = "", line;
		try {
			boolean isComment = false;
			while((line = br.readLine()) != null) {
				line = line.replaceAll("//.*", "").trim();
				if(line.startsWith("/*")) {
					isComment = true;
				}				
				if(!(line.startsWith("//") || line.equals("") || isComment)) {
					out += line.trim() + " ";
				}				
				if(line.endsWith("*/")) {
					isComment = false;
				}
			}
			br.close();
		}catch(IOException ioe) {
			System.out.println("ERR: Buffered Reader could not read");
			ioe.printStackTrace();
		}
		return out;
	}

}
