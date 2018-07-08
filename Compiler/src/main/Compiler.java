package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

import Util.FileModifier;
import err.ErrorCatch;
import io.JackReader;

public class Compiler {


	public Compiler() {
	}

	public void run() {
		File dir = chooseDirectory();
		if (!ErrorCatch.wasAnErrorThrown() && dir != null) {
			System.out.println(dir.toString());
			File[] jackFiles = extractJackFilesFromDirectory(dir);
			for (File file : jackFiles) {
				System.out.println("Start Compile File: " + file.getAbsolutePath().toString());
				compile(loadJackFile(file), file);
			}
		}
		ErrorCatch.printErrors();
	}

	private void compile(String jackCode, File output) {
		SyntaxAnalyzer analyzer = new SyntaxAnalyzer(jackCode);
		analyzer.run();
		analyzer.extractXMLFiles(FileModifier.changeExtension(output, "xml"));
		CodeGenerator generator = new CodeGenerator(analyzer.getParsingTree());
		generator.run();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FileModifier.changeExtension(output, "vm")))) {
			System.out.println("Write vm File: " + output.getAbsolutePath());
			for (String line : generator.getResult()) {				
				bw.write(line);
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String loadJackFile(File file) {
		JackReader jr = new JackReader(file);
		return jr.readAll();
	}

	private File[] extractJackFilesFromDirectory(File dir) {
		File[] list = dir.listFiles();
		int length = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i].isFile()) {
				if (list[i].getName().endsWith(".jack")) {
					System.out.println("File found: " + list[i].getAbsolutePath().toString());
					length++;
				}
			}
		}
		if (length > 0) {
			File[] out = new File[length];
			for (int i = 0; i < list.length; i++) {
				if (list[i].isFile()) {
					if (list[i].getName().endsWith(".jack")) {
						length--;
						System.out.println("File added: " + list[i].getAbsolutePath().toString());
						out[length] = list[i];
					}
				}
			}
			return out;
		} else {
			ErrorCatch.addError(ErrorCatch.FileChooser, false, "No jack Files at selected directory");
			return null;
		}
	}

	private File chooseDirectory() {
		JFileChooser chooser = new JFileChooser();
		if (System.getProperty("os.name").startsWith("Mac OS X")) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		} else {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		int retValue = chooser.showOpenDialog(null);
		switch (retValue) {
		case JFileChooser.CANCEL_OPTION:
			ErrorCatch.addError(ErrorCatch.FileChooser, false, "canceled JFileChooser");
			return null;
		case JFileChooser.APPROVE_OPTION:
			return chooser.getSelectedFile();
		case JFileChooser.ERROR_OPTION:
			ErrorCatch.addError(ErrorCatch.FileChooser, false, "canceled JFileChooser");
			return null;
		default:
			return null;
		}
	}

	public static void main(String[] args) {
		Compiler c = new Compiler();
		c.run();
	}

}
