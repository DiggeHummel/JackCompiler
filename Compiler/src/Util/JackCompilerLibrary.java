package Util;

public class JackCompilerLibrary {

	private String[] keyword = { "class", "consturctor", "function", "method", "field", "static", "var", "int", "char",
			"boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return" };
	private char[] symbol = { '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=',
			'~' };

	public JackCompilerLibrary() {

	}

	public boolean isKeyword(String suspect) {
		for (String s : keyword) {
			if (suspect.equals(s))
				return true;
		}
		return false;
	}

	public boolean isSymbol(String suspect) {
		for (char c : symbol) {
			if (suspect.equals(c + ""))
				return true;
		}
		return false;
	}

	public boolean isIntegerConstant(String suspect) {
		return suspect.matches("[0-9]+");
	}

	public boolean isStringConstant(String suspect) {
		if (suspect.startsWith("\"") && suspect.endsWith("\""))
			return true;
		return false;
	}

	public boolean isIdentifier(String suspect) {
		return suspect.matches("(([a-z]*[A-Z]*[0-9]*)+)(([a-z]*[A-Z]*[0-9]*[_]*)*)");
	}
}
