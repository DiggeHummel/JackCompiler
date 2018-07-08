package Util;

import java.util.ArrayList;

public class TreeNode {

	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	private boolean terminalSymbol;
	private String[] data = new String[2];
	private Object additive;

	public TreeNode(String s1) {
		this.data[0] = s1.trim();
		this.data[1] = "";
		this.terminalSymbol = false;
	}

	public TreeNode(String s1, String s2, boolean terminalSymbol) {
		this.data[0] = s1.trim();
		this.data[1] = data[0].equals("stringConstant") ? s2 : s2.trim();
		this.terminalSymbol = terminalSymbol;
	}

	public void addChildren(TreeNode... aChildren) {
		for (TreeNode child : aChildren)
			this.children.add(child);
	}

	public void addAdditive(Object obj) {
		this.additive = obj;
	}

	public Object getAdditive() {
		return this.additive;
	}

	public String[] getData() {
		return this.data;
	}

	public boolean equalsName(String s) {
		return data[1].equals(s);
	}

	public boolean equalsType(String s) {
		return data[0].equals(s);
	}

	public boolean isTerminalSymbol() {
		return this.terminalSymbol;
	}

	public TreeNode searchFor(String identifier) {
		for (TreeNode child : this.children) {
			if (child.getData()[0].equals(identifier))
				return child;
		}
		return null;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public ArrayList<TreeNode> getChildren() {
		return this.children;
	}

	@Override
	public String toString() {
		return "TreeNode [data=" + data[0] + "|-->" + data[1] + "<--" + "]";
	}
	
	
}
