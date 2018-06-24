package Util;

public enum TokenType {
	KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST;

	@Override
	public String toString() {
		switch (this) {
		case INT_CONST:
			return "integerConstant";
		case STRING_CONST:
			return "stringConstant";
		default:
			return super.toString().toLowerCase();
		}
	}
}
