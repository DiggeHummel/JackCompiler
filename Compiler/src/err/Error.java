package err;

public class Error {
	
	private String errValue;
	private String errMsg;
	
	public Error(String type, String msg) {
		this.errMsg = msg;
		this.errValue = type;
	}
	
	public String getError() {
		return this.errValue;
	}
	
	public String getMsg() {
		return this.errMsg;
	}

}
