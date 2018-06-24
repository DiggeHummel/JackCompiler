package err;

@SuppressWarnings("serial")
public class InterruptException extends RuntimeException {
	// unchecked Exception

	public InterruptException() {
		super("Deine EigeneUncheckedException");
	}

	public InterruptException(String msg) {
		super(msg);
	}
}
