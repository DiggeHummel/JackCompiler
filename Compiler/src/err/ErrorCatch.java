package err;

import java.util.ArrayList;

public class ErrorCatch {

	/* fields */
	public static final String FileChooser = "FileChooser";
	public static final String SyntaxAnalyzer = "SyntaxAnalyzer";
	public static final String XML = "XMLWriter";
	public static final String CodeGen = "CodeGenerator";
	private static boolean errorThrown = false;

	private static final String fatalMsg0 = "FatalError\n\t[thrown by";
	private static final String fatalMsg1 = "] Msg --> ";

	private static ArrayList<Error> errors = new ArrayList<Error>();

	public static void addError(String err, boolean fatal, String msg) {
		if (fatal) {
			handleFatalError(err, msg);
		} else {
			ErrorCatch.errorThrown = true;
			ErrorCatch.errors.add(new Error(err, msg));
		}
	}
	
	public static boolean wasAnErrorThrown() {
		boolean tmp = ErrorCatch.errorThrown;
		ErrorCatch.errorThrown = false;
		return tmp;
	}
	
	public static void setErrorThrownTrue() {
		ErrorCatch.errorThrown = true;
	}

	private static void handleFatalError(String err, String msg) {
		System.out.println(fatalMsg0 + err + fatalMsg1 + msg + "\n");
		printErrors();
		throw new InterruptException();
	}

	public static void printErrors() {
		if (!ErrorCatch.errors.isEmpty()) {
			System.out.println("Saved Errors while running");
			for (Error err : ErrorCatch.errors) {
				System.out.println("ErrorType[" + err.getError() + "]");
				System.out.println("\t" + err.getMsg());
			}
		}
	}

}
