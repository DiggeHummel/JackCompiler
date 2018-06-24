package Util;

import java.io.File;

public class FileModifier {

	public static File changeExtension(File src, String extension) {
		String absPath = src.getAbsolutePath();
		int index = absPath.lastIndexOf(".");
		return new File(absPath.substring(0, index + 1) + extension);
	}

	public static File changeName(File src, String name) {
		String absPath = src.getAbsolutePath();
		int index = absPath.lastIndexOf(File.separator);
		int index2 = absPath.lastIndexOf(".");
		return new File(absPath.substring(0, index + 1) + name + absPath.substring(index2));
	}
}
