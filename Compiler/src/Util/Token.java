package Util;

public class Token {

	private String name;
	private TokenType type;

	public Token(String name, TokenType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		if (type == TokenType.STRING_CONST)
			return name.replace("\"", "");
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

}
