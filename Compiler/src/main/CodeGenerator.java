package main;

import java.util.ArrayList;

import Util.Information;
import Util.SymbolTable;
import Util.Tree;
import Util.TreeNode;

public class CodeGenerator {

	private final Tree tree;
	private final SymbolTable classScope;
	private SymbolTable methodScope;
	private int classVarIndex = 0, methodVarIndex = 0, labelCount = 0;
	private String className;
	private boolean isRunning = false;

	private ArrayList<String> list;

	public CodeGenerator(Tree t) {
		this.tree = t;
		this.classScope = new SymbolTable();
		this.methodScope = new SymbolTable();
		this.className = tree.getRoot().searchFor("identifier").getData()[1];
		createClassScope();
	}

	public ArrayList<String> getResult() {
		return isRunning ? null : list;
	}

	public void run() {
		isRunning = true;
		System.out.println("CodeGen start...");
		list = new ArrayList<String>();
		for (TreeNode child : tree.getRoot().getChildren()) {
			if (child.getData()[0].equals("subroutineDec")) {
				createMethodScope(child);
				generateFunction(child);
			}
		}
		System.out.println("CodeGen end");
		isRunning = false;
	}

	private void generateFunction(TreeNode root) {
		String functionName = root.searchFor("identifier").getData()[1];
		ArrayList<TreeNode> subroutineBodyList = root.searchFor("subroutineBody").getChildren();
		int varDeclarations = 0;
		for (TreeNode child : subroutineBodyList) {
			if (child.getData()[0].equals("varDec"))
				for (TreeNode subChild : child.getChildren()) {
					if (subChild.getData()[0].equals("symbol"))
						varDeclarations++;
				}
		}
		list.add("function " + className + "." + functionName + " " + varDeclarations);
		TreeNode tmp = root.searchFor("subroutineBody").searchFor("statements");
		generateStatements(tmp);

	}

	private void generateStatements(TreeNode statementsNode) {
		for (TreeNode statementNode : statementsNode.getChildren()) {
			switch (statementNode.getData()[0]) {
			case "letStatement":
				generateLet(statementNode);
				break;
			case "ifStatement":
				generateIf(statementNode);
				break;
			case "whileStatement":
				generateWhile(statementNode);
				break;
			case "doStatement":
				generateDo(statementNode);
				break;
			case "returnStatement":
				generateReturn(statementNode);
				break;
			}
		}
	}

	private void generateReturn(TreeNode statementNode) {
		switch (statementNode.getChildren().size()) {
		case 3:
			generateExpression(statementNode.getChildren().get(1));
			list.add("return");
			break;
		case 2:
			list.add("push constant 0");
			list.add("return");
			break;
		default:
			throw new IllegalArgumentException();
		}

	}

	// do subroutineCall ;
	private void generateDo(TreeNode statementNode) {
		ArrayList<TreeNode> childs = statementNode.getChildren();
		childs.remove(0); // do
		childs.remove(childs.size() - 1); // ;
		generateSubroutineCall(childs);
		list.add("pop temp 0");
	}

	private void generateWhile(TreeNode statementNode) {
		ArrayList<TreeNode> childs = statementNode.getChildren();
		int start = labelCount;
		int end = labelCount + 1;
		labelCount += 2;
		list.add("label LABEL_" + start);
		generateExpression(childs.get(2));
		list.add("not");
		list.add("if-goto LABEL_" + end);
		generateStatements(childs.get(5));
		list.add("goto LABEL_" + start);
		list.add("label LABEL_" + end);
	}

	private void generateIf(TreeNode statementNode) {
		ArrayList<TreeNode> childs = statementNode.getChildren();
		int start = labelCount;
		int end = labelCount + 1;
		labelCount += 2;
		generateExpression(childs.get(2));
		list.add("not");
		list.add("if-goto LABEL_" + start);
		generateStatements(childs.get(5));
		list.add("goto LABEL_" + end);
		list.add("label LABEL_" + start);
		if (childs.size() >= 9)
			generateStatements(childs.get(9));
		list.add("label LABEL_" + end);
	}

	// let destination = source;
	private void generateLet(TreeNode statementNode) {
		boolean isPointer = false;
		ArrayList<TreeNode> childs = statementNode.getChildren();
		TreeNode varName = childs.get(1); // varName
		String appendix = "";
		if (childs.get(2).equalsName("[")) {
			isPointer = true;
			appendix = generateArray(varName, statementNode, ArrayMode.Destination);
		}
		generateExpression(childs.get(isPointer ? 6 : 3));
		if (isPointer) {
			for (String s : appendix.split("\n")) {
				if (!s.equals(""))
					list.add(s);
			}
		} else
			list.add(lookup(varName.getData()[1]).convertToPop());

	}

	private enum ArrayMode {
		Normal, Destination
	};

	private String generateArray(TreeNode varName, TreeNode parent, ArrayMode mode) {
		// search expression
		TreeNode expNode = null;
		for (TreeNode child : parent.getChildren()) {
			if (child.equalsType("expression")) {
				expNode = child;
				break;
			}
		}
		// search for identifier and [ to say next is an array or not
		boolean isNextArray = false;
		parent = expNode;
		expNode = expNode.getChildren().get(0); // expNode = termNode
		if (expNode.getChildren().size() >= 2) {
			if (expNode.getChildren().get(0).equalsType("identifier") && expNode.getChildren().get(1).equalsName("[")) {
				isNextArray = true;
			}
		}
		// begin genrateing code
		Information inf = lookup(varName.getData()[1]);
		list.add(inf.convertToPush());
		if (isNextArray)
			generateArray(expNode.getChildren().get(0), expNode, ArrayMode.Normal);
		else
			generateExpression(parent);
		switch (mode) {
		case Normal:
			list.add("add");
			list.add("pop pointer 1");
			list.add("push that 0");
			return "";
		case Destination:
			list.add("add");
			return "pop temp 0\npop pointer 1\npush temp 0\npop that 0";
		}
		return "";
	}

	private void generateExpression(TreeNode expNode) {
		ArrayList<TreeNode> childs = expNode.getChildren();
		generateTerm(childs.get(0));
		if (((childs.size() - 1) % 2) == 0) {
			for (int i = 1; i < childs.size(); i += 2) {
				generateTerm(childs.get(i + 1));
				generateSymbol(childs.get(i), false);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void generateSymbol(TreeNode symbolNode, boolean partOfOneTerm) {
		switch (symbolNode.getData()[1]) {
		case "+":
			list.add("add");
			break;
		case "-":
			list.add(partOfOneTerm ? "neg" : "sub");
			break;
		case "*":
			list.add("call Math.multiply 2");
			break;
		case "/":
			list.add("call Math.divide 2");
			break;
		case "&":
			list.add("and");
			break;
		case "|":
			list.add("or");
			break;
		case "<":
			list.add("lt");
			break;
		case ">":
			list.add("gt");
			break;
		case "=":
			list.add("eq");
			break;
		case "~":
			list.add("not");
			break;
		}
	}

	// symbol term
	private void generateTerm(TreeNode termNode) {
		ArrayList<TreeNode> childs = termNode.getChildren();
		for (int i = 0; i < childs.size(); i++) {
			TreeNode child = childs.get(i);
			switch (child.getData()[0]) {
			case "integerConstant":
				list.add("push constant " + child.getData()[1]);
				break;
			case "stringConstant":
				String s = child.getData()[1];
				int length = s.length();
				list.add("push constant " + length);
				list.add("call String.new 1");
				for (int j = 0; j < s.length(); j++) {
					list.add("push constant " + (int) s.charAt(j));
					list.add("call String.appendChar 2");
				}
				break;
			case "identifier":
				if (i + 1 < childs.size()) {
					if (childs.get(i + 1).equalsName(".")) {
						// subroutineCall-->Class . method ( expList )
						ArrayList<TreeNode> tmp = new ArrayList<TreeNode>();
						for (int j = i; j < i + 6; j++) {
							tmp.add(childs.get(j));
						}
						generateSubroutineCall(tmp);
						i += 5;
					} else if (childs.get(i + 1).equalsName("(")) {
						// subroutineCall-->method ( expList )
						ArrayList<TreeNode> tmp = new ArrayList<TreeNode>();
						for (int j = i; j < i + 4; i++) {
							tmp.add(childs.get(j));
						}
						generateSubroutineCall(tmp);
						i += 3;
					} else if (childs.get(i + 1).equalsName("[")) {
						generateArray(childs.get(i), termNode, ArrayMode.Normal);
						i += 3;
					}
				} else
					generateIdentifier(child, "push");
			case "symbol":
				switch (child.getData()[1]) {
				case "(":
					generateExpression(childs.get(i + 1));
					break;
				case "-":
					generateTerm(childs.get(i + 1));
					i++;
					generateSymbol(child, true);
					break;
				case "~":
					generateTerm(childs.get(i + 1));
					i++;
					generateSymbol(child, false);
					break;
				}
				break;
			case "keyword":
				switch (child.getData()[1]) {
				case "true":
					list.add("push constant 0");
					list.add("not");
					break;
				case "false":
					list.add("push constant 0");
					break;
				case "null":
					list.add("push constant 0");
					break;
				}
				break;
			default:
				// nothing
			}
		}
	}

	private void generateSubroutineCall(ArrayList<TreeNode> nodes) {
		switch (nodes.get(1).getData()[1]) {
		case "(":// subroutineCall-->Class . method ( expList )
			int numberOfParameter = 0;
			for (TreeNode subChild : nodes.get(2).getChildren())
				if (subChild.equalsType("expression"))
					numberOfParameter++;
			generateExpressionList(nodes.get(2));
			list.add("call " + className + "." + nodes.get(0).getData()[1] + " " + numberOfParameter);
			break;
		case ".":// subroutineCall-->method ( expList )
			int numberOfParameter2 = 0;
			for (TreeNode subChild : nodes.get(4).getChildren())
				if (subChild.equalsType("expression"))
					numberOfParameter2++;
			generateExpressionList(nodes.get(4));
			list.add("call " + nodes.get(0).getData()[1] + "." + nodes.get(2).getData()[1] + " " + numberOfParameter2);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void generateExpressionList(TreeNode expListNode) {
		ArrayList<TreeNode> childs = expListNode.getChildren();
		if (childs.isEmpty())
			return;
		generateExpression(childs.get(0));
		for (int i = 1; i < childs.size(); i += 2) {
			if (childs.get(i).equalsName(",")) {
				generateExpression(childs.get(i + 1));
			} else
				throw new IllegalArgumentException();
		}
	}

	private void generateIdentifier(TreeNode child, String command) {
		Information inf = lookup(child.getData()[1]);
		if (inf != null)
			list.add(command + " " + inf.getConvertedKind() + " " + inf.getIndex());
		else {
			// subroutineName or (className) werden vorher behandelt
		}
	}

	private Information lookup(String key) {
		if (methodScope.contain(key)) {
			return methodScope.getInformation(key);
		}
		if (classScope.contain(key)) {
			return classScope.getInformation(key);
		}
		return null;
	}

	private void createClassScope() {
		for (TreeNode child : tree.getRoot().getChildren()) {
			if (child.getData()[0].equals("classVarDec")) {
				extractClassVar(child);
			}
		}
	}

	private void createMethodScope(TreeNode root) {
		if (root.getData()[0].equals("subroutineDec")) {
			this.methodScope = new SymbolTable();
			methodVarIndex = 0;
			if (!root.getChildren().get(0).equalsName("function")) {
				methodScope.add("this", new Information(className, "argument", 0));
				methodVarIndex++;
			}
			extractMethod(root);
		}
	}

	private void extractMethod(TreeNode root) {
		for (TreeNode child : root.getChildren()) {
			if (child.getData()[0].equals("parameterList"))
				extractMethodArguments(child);
			if (child.getData()[0].equals("subroutineBody")) {
				for (TreeNode child2 : child.getChildren()) {
					if (child2.getData()[0].equals("varDec"))
						extractMethodVar(child2);
				}
			}
		}
	}

	private void extractMethodArguments(TreeNode root) {
		ArrayList<TreeNode> list = root.getChildren();
		int size = list.size(), index = methodVarIndex;
		String name, type;
		for (int i = 0; i < size; i += 3) {
			name = list.get(i + 1).getData()[1];
			type = list.get(i).getData()[1];
			methodScope.add(name, new Information(type, "argument", index));
			index++;
		}
	}

	private void extractMethodVar(TreeNode root) {
		ArrayList<TreeNode> list = root.getChildren();
		int size = list.size();
		if ((size % 2) == 0) {
			int i = 0;
			String name, type;
			while (i < size) {
				name = list.get(i + 2).getData()[1];
				type = list.get(i + 1).getData()[1];
				int j = 0;
				while (list.get(i + 3 + j).getData()[1].equals(",")) {
					methodScope.add(name, new Information(type, "var", methodVarIndex));
					methodVarIndex++;
					name = list.get(i + 3 + j + 1).getData()[1];
					j += 2;
				}
				methodScope.add(name, new Information(type, "var", methodVarIndex));
				methodVarIndex++;
				i += 4 + j;
			}
		} else {
			System.out.println("List with root[" + root.getData()[1] + "] no coorect size. Size is " + size);
		}
	}

	private void extractClassVar(TreeNode root) {
		ArrayList<TreeNode> list = root.getChildren();
		int size = list.size();
		if ((size % 2) == 0) {
			int i = 0;
			String name, type, kind;
			while (i < size) {
				name = list.get(i + 2).getData()[1];
				type = list.get(i + 1).getData()[1];
				kind = list.get(i).getData()[1];
				int j = 0;
				while (list.get(i + 3 + j).getData()[1].equals(",")) {
					classScope.add(name, new Information(type, kind, classVarIndex));
					classVarIndex++;
					name = list.get(i + 3 + j + 1).getData()[1];
					j += 2;
				}
				classScope.add(name, new Information(type, kind, classVarIndex));
				classVarIndex++;
				i += 4 + j;
			}
		} else {
			System.out.println("List with root[" + root.getData()[1] + "] no coorect size. Size is " + size);
		}
	}

}
