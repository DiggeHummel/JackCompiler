package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Element;

import Util.FileModifier;
import Util.Token;
import err.ErrorCatch;
import io.XMLWriter;

public class SyntaxAnalyzer {

	private Tokenizer tokenizer;
	private Parser parser;
	private String src;

	public SyntaxAnalyzer(String src) {
		this.src = src;
	}

	public void run() {
		tokenizer = new Tokenizer();
		tokenizer.run(src);
		if (!ErrorCatch.wasAnErrorThrown()) {
			parser = new Parser(tokenizer.getTokens());
			parser.run();
		} else {
			ErrorCatch.setErrorThrownTrue();
		}
	}

	public void extractXMLFiles(File output) {
		if (!parser.hasParsingError()) {
			System.out.println("Start Extraction");
			extractTokenstream(FileModifier.changeName(output, output.getName() + "T"));
			extractParserOutput(output);
		}
	}

	private void extractTokenstream(File output) {
		ArrayList<Token> list = tokenizer.getTokens();
		Element root = new Element("tokens");
		XMLWriter writer = new XMLWriter(root);
		for (Token t : list) {
			writer.addDocument(new Element(t.getType().toString()).setText(t.getName()), false);
		}
		try {
			System.out.println("tyr to write at File[" + output.getAbsolutePath() + "]");
			writer.write(new BufferedWriter(new FileWriter(output)));
		} catch (IOException e) {
			ErrorCatch.addError(ErrorCatch.XML, false, "File[" + "]\tcould not write");
		}
	}

	private void extractParserOutput(File output) {
		XMLWriter writer = new XMLWriter();
		writer.convertTreeToDocument(parser.getOutput());
		try {
			writer.write(new BufferedWriter(new FileWriter(output)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
