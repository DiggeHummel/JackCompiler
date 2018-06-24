package main;

import java.util.ArrayList;

import Util.JackCompilerLibrary;
import Util.Token;
import Util.TokenType;
import err.ErrorCatch;

public class Tokenizer {

	private final ArrayList<Token> tokens;
	private JackCompilerLibrary lib;

	public Tokenizer() {
		this.tokens = new ArrayList<Token>();
		this.lib = new JackCompilerLibrary();
	}

	public void run(String src) {
		String tmp = "";
		boolean isString = false;
		char c;
		for (int i = 0; i < src.length(); i++) {
			c = src.charAt(i);
			if (c == '"') {
				if (isString) {
					tmp += c;
					addToken(tmp);
					tmp = "";
				}
				isString = !isString;
			}
			if (isString) {
				tmp += c;
			} else {
				switch (c) {
				case ' ':
					addToken(tmp);
					tmp = "";
					break;
				default:
					if (c == '(' || c == ')' || c == ';' || c == ',' || c == '.' || c == '[' || c == ']' || c == '-' || c == '~') {
						addToken(tmp);
						tmp = Character.toString(c);
						addToken(tmp);
						tmp = "";
					} else if (c != '"') {
						tmp += c;
					}
					break;

				}
			}
		}
	}

	private void addToken(String name) {
		name = name.trim();
		if (name.equals(""))
			return;
		TokenType tt = getTokenType(name);
		if (!ErrorCatch.wasAnErrorThrown()) {
			tokens.add(new Token(name, tt));
		}
	}

	private TokenType getTokenType(String tmp) {
		if (lib.isKeyword(tmp))
			return TokenType.KEYWORD;
		if (lib.isSymbol(tmp))
			return TokenType.SYMBOL;
		if (lib.isIntegerConstant(tmp))
			return TokenType.INT_CONST;
		if (lib.isStringConstant(tmp))
			return TokenType.STRING_CONST;
		if (lib.isIdentifier(tmp))
			return TokenType.IDENTIFIER;
		ErrorCatch.addError(ErrorCatch.SyntaxAnalyzer, false, "String:[" + tmp + "] has no TokenType");
		return null;
	}

	public ArrayList<Token> getTokens() {
		return this.tokens;
	}

}
