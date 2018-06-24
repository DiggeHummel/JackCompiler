package Util;

public class Tree {

	private TreeNode root;
	private boolean empty;
	private boolean hasTerminalSymbol;
	private int depth = 0;

	public Tree() {
		this.empty = true;
	}

	public Tree(TreeNode aRoot) {
		this.root = aRoot;
		this.empty = false;
	}

	public void merge(Tree t) {
		if (!t.empty) {
			if (this.empty) {
				this.root = t.root;
				this.empty = false;
			} else {
				this.root.addChildren(t.root);
			}
		}
	}

	public void addToRoot(TreeNode node) {
		if (this.empty) {
			root = node;
			empty = false;
		} else
			this.root.addChildren(node);
	}

	public TreeNode getRoot() {
		return this.root;
	}

	public boolean hasTerminalSymbol() {
		if (this.empty)
			return false;
		this.hasTerminalSymbol = false;
		rekursionHasTerminalSymbol(this.root);
		return this.hasTerminalSymbol;
	}

	private void rekursionHasTerminalSymbol(TreeNode aRoot) {
		if (aRoot.isTerminalSymbol())
			this.hasTerminalSymbol = true;
		for (int i = 0; i < aRoot.getChildren().size(); i++) {
			if (aRoot.getChildren().get(i).isTerminalSymbol()) {
				this.hasTerminalSymbol = true;
			}
			this.rekursionHasTerminalSymbol(aRoot.getChildren().get(i));
		}
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void printTree() {
		if (!empty)
			this.print(this.root);
		else
			System.out.println("Tree is empty");
		System.out.println("");
	}

	private void print(TreeNode aRoot) {
		depth++;
		System.out.println("depth:" + depth + "|" + aRoot.getData()[1]);
		for (int i = 0; i < aRoot.getChildren().size(); i++) {
			this.print(aRoot.getChildren().get(i));
		}
		depth--;
	}
}
