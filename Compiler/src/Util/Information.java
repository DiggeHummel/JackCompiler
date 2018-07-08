package Util;

public class Information {

	private String type;
	private String kind;
	private int index;

	public Information(String type, String kind, int index) {
		this.type = type;
		this.kind = kind;
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public String getKind() {
		return kind;
	}

	public int getIndex() {
		return index;
	}

	public String getConvertedKind() {
		switch (kind) {
		case "var":
			return "local";
		case "field":
			return "this";
		default:
			return kind;
		}
	}

	public String convertToPop() {
		return "pop " + getConvertedKind() + " " + index;
	}

	public String convertToPush() {
		return "push " + getConvertedKind() + " " + index;
	}

	@Override
	public String toString() {
		return "Type [" + this.type + "] | Kind [" + this.kind + "] | index [" + this.index + "]";
	}

}
