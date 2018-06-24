package Util;

import java.util.ArrayList;

public class TreeNode {

	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	private boolean terminalSymbol;
	private String[] data = new String[2];

	public TreeNode(String s1) {
		this.data[0] = s1.trim();
		this.data[1] = "";
		this.terminalSymbol = false;
	}

	public TreeNode(String s1, String s2, boolean terminalSymbol) {
		this.data[0] = s1.trim();
		this.data[1] = s2.trim();
		this.terminalSymbol = terminalSymbol;
	}

	public void addChildren(TreeNode... aChildren) {
		for (TreeNode child : aChildren)
			this.children.add(child);
	}

	public String[] getData() {
		return this.data;
	}

	public boolean isTerminalSymbol() {
		return this.terminalSymbol;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public ArrayList<TreeNode> getChildren() {
		return this.children;
	}
}
