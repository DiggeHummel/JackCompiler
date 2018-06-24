package io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import Util.Tree;
import Util.TreeNode;

public class XMLWriter {

	private Document doc;
	private ArrayList<Element> tree;

	public XMLWriter() {
		this.doc = new Document();
		this.tree = new ArrayList<Element>();
	}

	public XMLWriter(Element root) {
		this.doc = new Document();
		doc.setRootElement(root);
		this.tree = new ArrayList<Element>();
		tree.add(root);
	}

	private void addTree(Element e) {
		this.tree.add(e);
	}

	public void addDocument(Element e, boolean toAdd) {
		tree.get(tree.size() - 1).addContent(e);
		if (toAdd)
			addTree(e);
	}

	public void decreaseTree(int depth) {
		int size = tree.size();
		if (depth < size) {
			for (int i = 0; i < depth; i++) {
				tree.remove(size - (i + 1));
			}
		}
	}

	/* Convert a tree Object into an Document */
	// #
	public void convertTreeToDocument(Tree t) {
		Element root = nodeToElement(t.getRoot());
		doc.addContent(root);
		convert(t.getRoot(), root);
	}

	// ##
	private void convert(TreeNode node, Element e) {
		if (!node.hasChildren())
			return;
		for (TreeNode subNode : node.getChildren()) {
			Element nextElem = nodeToElement(subNode);
			e.addContent(nextElem);
			convert(subNode, nextElem);
		}
	}

	// ###
	private Element nodeToElement(TreeNode node) {
		String[] arr = node.getData();
		if (arr[1].equals(""))
			return new Element(arr[0]);
		else
			return new Element(arr[0]).setText(arr[1]);
	}

	/**
	 * writes the private Object doc as XML
	 * 
	 * @param bw
	 *            target on disk
	 */
	public void write(BufferedWriter bw) {
		Format format = Format.getPrettyFormat();
		format.setOmitDeclaration(true);
		format.setExpandEmptyElements(true);
		
		XMLOutputter out = new XMLOutputter(format);
		try {
			out.output(this.doc, bw);
			System.out.println("Document as xml wrote");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
