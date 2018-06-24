package main;

import java.util.ArrayList;

import Util.Token;
import Util.TokenType;
import Util.Tree;
import Util.TreeNode;
import err.ErrorCatch;

public class Parser {

	private Tree output;
	private ArrayList<Token> tokens;
	private int index;
	private boolean parsingError;

	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
		this.index = 0;
	}

	public void run() {
		this.parsingError = false;
		this.output = compClass();
		if (tokens.size() != index) {
			ErrorCatch.addError(ErrorCatch.SyntaxAnalyzer, false, "Class " + tokens.get(1).getName()
					+ " has more Tokens than allowed\nLast Token: " + getNextTokenName());
			this.parsingError = true;
		}
	}

	public Tree getOutput() {
		return this.output;
	}

	public boolean hasParsingError() {
		return this.parsingError;
	}

	// compile helper methods
	// #
	private Tree terminalSymbol(String... symbol) {
		if (equalsNextToken(symbol)) {
			Tree out = new Tree((new TreeNode(getNextTokenType().toString(), getNextTokenName(), true)));
			index++;
			return out;
		}
		return new Tree();
	}

	// ##
	private boolean equalsNextToken(String... name) {
		if (index >= tokens.size())
			ErrorCatch.addError(ErrorCatch.SyntaxAnalyzer, true,
					"Programm will be run into a NullPointerException\nParser Range Check for tokens and index is fatal");
		String compareString = getNextTokenName();
		for (String s : name) {
			if (compareString.equals(s))
				return true;
		}
		return false;
	}

	// ###
	private String getNextTokenName() {
		return tokens.get(index).getName();
	}

	// ####
	private TokenType getNextTokenType() {
		return tokens.get(index).getType();
	}

	/*
	 * ### Programm structure ###
	 */

	/*
	 * 'class' className '{' classVarDec* subroutineDec* '}'
	 */
	private Tree compClass() {
		Tree t = new Tree(new TreeNode("class"));
		t.merge(terminalSymbol("class"));
		t.merge(compIdentifier());
		t.merge(terminalSymbol("{"));
		while (getNextTokenName().equals("static") || getNextTokenName().equals("field")) {
			t.merge(compClassVarDec());
		}
		while (getNextTokenName().equals("constructor") || getNextTokenName().equals("function")
				|| getNextTokenName().equals("method")) {
			t.merge(compSubroutineDec());
		}
		t.merge(terminalSymbol("}"));
		return t;
	}

	/*
	 * ('static'|'field') type varName (',' varName)* ';'
	 */
	private Tree compClassVarDec() {
		Tree t = new Tree();
		while (equalsNextToken("static", "field")) {
			t.merge(terminalSymbol("static", "field"));
			t.merge(compType());
			t.merge(compIdentifier());
			while (equalsNextToken(",")) {
				t.merge(terminalSymbol(","));
				t.merge(compIdentifier());
			}
			t.merge(terminalSymbol(";"));
		}
		return t;
	}

	/*
	 * 'int'|'char'|'boolean'|className
	 */
	private Tree compType() {
		Tree t = new Tree();
		if (equalsNextToken("int", "char", "boolean")) {
			t.merge(terminalSymbol("int", "char", "boolean"));
		} else if (getNextTokenType() == TokenType.IDENTIFIER) {
			t.merge(compIdentifier());
		}
		return t;
	}

	/*
	 * ('constructor'|'function'|'method') ('void'|type) subroutineName '('
	 * parameterList ')' subroutineBody
	 */
	private Tree compSubroutineDec() {
		Tree t = new Tree(new TreeNode("subroutineDec"));
		t.merge(terminalSymbol("constructor", "function", "method"));
		if (equalsNextToken("void")) {
			t.merge(terminalSymbol("void"));
		} else if (getNextTokenType() == TokenType.IDENTIFIER) {
			t.merge(compIdentifier());
		}
		t.merge(compIdentifier());
		t.merge(terminalSymbol("("));
		t.merge(compParameterList());
		t.merge(terminalSymbol(")"));
		t.merge(compSubroutineBody());
		return t;
	}

	/*
	 * ((type varName) (',' type varName)*)?
	 */
	private Tree compParameterList() {
		Tree t = new Tree(new TreeNode("parameterList"));
		t.merge(compType());
		if (t.hasTerminalSymbol()) {
			t.merge(compIdentifier());
			while (equalsNextToken(",")) {
				t.merge(terminalSymbol(","));
				t.merge(compType());
				t.merge(compIdentifier());
			}
		} else
			return new Tree(new TreeNode("parameterList"));
		return t;
	}

	/*
	 * '{' varDec* statements '}'
	 */
	private Tree compSubroutineBody() {
		Tree t = new Tree(new TreeNode("subroutineBody"));
		t.merge(terminalSymbol("{"));
		while (equalsNextToken("var")) {
			t.merge(compVarDec());
		}
		t.merge(compStatements());
		t.merge(terminalSymbol("}"));
		return t;
	}

	/*
	 * 'var' type varName (',' varName)* ';'
	 */
	private Tree compVarDec() {
		Tree t = new Tree(new TreeNode("varDec"));
		t.merge(terminalSymbol("var"));
		t.merge(compType());
		t.merge(compIdentifier());
		while (equalsNextToken(",")) {
			t.merge(terminalSymbol(","));
			t.merge(compIdentifier());
		}
		t.merge(terminalSymbol(";"));
		return t;
	}

	/*
	 * 
	 */
	private Tree compIdentifier() {
		if (getNextTokenType() == TokenType.IDENTIFIER) {
			Tree t = new Tree(new TreeNode(getNextTokenType().toString(), getNextTokenName(), true));
			index++;
			return t;
		}
		return new Tree();
	}

	/*
	 * Statements
	 */

	/*
	 * statements*
	 */
	private Tree compStatements() {
		Tree t = new Tree(new TreeNode("statements")), tmp;
		while (!(tmp = compStatement()).isEmpty()) {
			t.merge(tmp);
		}
		return t;
	}

	/*
	 * letStatement|ifStatement|whileStatement|doStatement|returnStatement
	 */
	private Tree compStatement() {
		if (equalsNextToken("let")) {
			return compLetStatement();
		} else if (equalsNextToken("if")) {
			return compIfStatement();
		} else if (equalsNextToken("while")) {
			return compWhileStatement();
		} else if (equalsNextToken("do")) {
			return compDoStatement();
		} else if (equalsNextToken("return")) {
			return compReturnStatement();
		}
		return new Tree();
	}

	/*
	 * 'let' varName ('[' expression ']')? '=' expression ';'
	 */
	private Tree compLetStatement() {
		Tree t = new Tree(new TreeNode("letStatement"));
		t.merge(terminalSymbol("let"));
		t.merge(compIdentifier());
		if (equalsNextToken("[")) {
			t.merge(terminalSymbol("["));
			t.merge(compExpression());
			t.merge(terminalSymbol("]"));
		}
		t.merge(terminalSymbol("="));
		t.merge(compExpression());// err
		t.merge(terminalSymbol(";"));
		return t;
	}

	/*
	 * 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?
	 */
	private Tree compIfStatement() {
		Tree t = new Tree(new TreeNode("ifStatement"));
		t.merge(terminalSymbol("if"));
		t.merge(terminalSymbol("("));
		t.merge(compExpression());
		t.merge(terminalSymbol(")"));
		t.merge(terminalSymbol("{"));
		t.merge(compStatements());
		t.merge(terminalSymbol("}"));
		if (equalsNextToken("else")) {
			t.merge(terminalSymbol("else"));
			t.merge(terminalSymbol("{"));
			t.merge(compStatements());
			t.merge(terminalSymbol("}"));
		}
		return t;
	}

	/*
	 * 'while' '(' expression ')' '{' statements '}'
	 */
	private Tree compWhileStatement() {
		Tree t = new Tree(new TreeNode("whileStatement"));
		t.merge(terminalSymbol("while"));
		t.merge(terminalSymbol("("));
		t.merge(compExpression());
		t.merge(terminalSymbol(")"));
		t.merge(terminalSymbol("{"));
		t.merge(compStatements());
		t.merge(terminalSymbol("}"));
		return t;
	}

	/*
	 * 'do' subroutineCall ';'
	 */
	private Tree compDoStatement() {
		Tree t = new Tree(new TreeNode("doStatement"));
		t.merge(terminalSymbol("do"));
		t = compSubroutineCall(t);
		t.merge(terminalSymbol(";"));
		return t;
	}

	/*
	 * 'return' expression? ';'
	 */
	private Tree compReturnStatement() {
		Tree t = new Tree(new TreeNode("returnStatement"));
		t.merge(terminalSymbol("return"));
		Tree tmp = compExpression();
		if (!tmp.isEmpty()) {
			t.merge(tmp);
		}
		t.merge(terminalSymbol(";"));
		return t;
	}

	/*
	 * Expressions
	 */

	/*
	 * term (operation term)*
	 */
	private Tree compExpression() {
		Tree t = new Tree(new TreeNode("expression"));
		Tree tmp = new Tree();
		t.merge(compTerm());
		// t.merge(compIdentifier());
		while ((tmp = compOperation()).hasTerminalSymbol()) {
			t.merge(tmp);
			t.merge(compTerm());
		}
		return t;

	}

	/*
	 * integerConstant|stringConstant|keywordConstant|varName('[' expression
	 * ']')?|subroutineCall|'(' expression ')'|unaryOperation term
	 */
	private Tree compTerm() {
		Tree t = new Tree(new TreeNode("term"));
		Tree tmp = new Tree();
		TokenType tt = getNextTokenType();
		if (tt == TokenType.INT_CONST || tt == TokenType.STRING_CONST) {
			t.merge(new Tree(new TreeNode(tt.toString(), getNextTokenName(), true)));
			index++;
		} else if (getNextTokenType() == TokenType.IDENTIFIER && tokens.get(index + 1).getName().equals("[")) {
			t.merge(compIdentifier());
			t.merge(terminalSymbol("["));
			t.merge(compExpression());
			t.merge(terminalSymbol("]"));
		} else if ((tmp = compSubroutineCall(t)).hasTerminalSymbol()) {
			t = tmp;
			return t;
		} else if (getNextTokenType() == TokenType.IDENTIFIER) {
			t.merge(compIdentifier());
			return t;
		} else if ((tmp = compKeywordConstant()).hasTerminalSymbol()) {
			t.merge(tmp);
			return t;
		} else if (equalsNextToken("(")) {
			t.merge(terminalSymbol("("));
			t.merge(compExpression());
			t.merge(terminalSymbol(")"));
		} else if (!(t = compUnaryOperation()).isEmpty()) {
			t.merge(compTerm());
		}
		return t;
	}

	/*
	 * subroutineName '(' expressionList ')'|(className|varName) '.' subroutineName
	 * '(' expressionList ')'
	 */
	private Tree compSubroutineCall(Tree t) {
		if (getNextTokenType() == TokenType.IDENTIFIER && tokens.get(index + 1).getName().equals("(")) {// change
			t.merge(compIdentifier());
			t.merge(terminalSymbol("("));
			t.merge(compExpressionList());
			t.merge(terminalSymbol(")"));
		} else if (getNextTokenType() == TokenType.IDENTIFIER && tokens.get(index + 1).getName().equals(".")) {
			t.merge(compIdentifier());
			t.merge(terminalSymbol("."));
			t.merge(compIdentifier());
			t.merge(terminalSymbol("("));
			t.merge(compExpressionList());
			t.merge(terminalSymbol(")"));
		}
		return t;
	}

	/*
	 * (expression (',' expression)*)?
	 */
	private Tree compExpressionList() {
		Tree t = new Tree(new TreeNode("expressionList"));
		Tree tmp = new Tree();
		if ((tmp = compExpression()).hasTerminalSymbol()) {
			t.merge(tmp);
			Tree tmp2 = new Tree();
			while (!(tmp2 = terminalSymbol(",")).isEmpty()) {
				tmp2.merge(compExpression());
				t.merge(tmp2);
			}
		}
		return t;
	}

	private Tree compOperation() {
		return terminalSymbol("+", "-", "*", "/", "&", "|", "<", ">", "=");
	}

	private Tree compUnaryOperation() {
		return terminalSymbol("-", "~");
	}

	private Tree compKeywordConstant() {
		return terminalSymbol("true", "false", "null", "this");
	}

}
