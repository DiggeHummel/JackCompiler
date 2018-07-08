package Util;

import java.util.HashMap;

public class SymbolTable {

	private HashMap<String, Information> st;

	public SymbolTable() {
		this.st = new HashMap<String, Information>();
	}
	
	public boolean contain(String key) {
		return st.containsKey(key);
	}
	
	public Information getInformation(String key) {
		return st.get(key);
	}

	public void add(String name, Information i) {
		st.put(name, i);
	}

	@Override
	public String toString() {
		String out = "Size: " + this.st.size() + "\n";
		for (String key : st.keySet()) {
			out += "Name = " + key + " | " + this.st.get(key).toString() + "\n";
		}
		return out;
	}

}
