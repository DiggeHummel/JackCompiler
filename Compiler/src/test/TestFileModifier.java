package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import Util.FileModifier;

class TestFileModifier {
	
	@Test
	void testChangeExtension() {
		File testFile = new File("src" + File.separator + "test.txt");
		String s = testFile.getAbsolutePath();
		String correct = s.substring(0, s.lastIndexOf(".")+1) + "xml";
		assertEquals(correct, FileModifier.changeExtension(testFile, "xml").toString());
	}
	
	@Test
	void testChangeName() {
		File testFile = new File("src" + File.separator + "test.txt");
		String s = testFile.getAbsolutePath();
		String correct = s.substring(0, s.lastIndexOf(File.separator)+1) + "newTestName" + s.substring(s.lastIndexOf("."));
		assertEquals(correct, FileModifier.changeName(testFile, "newTestName").toString());
	}
	
	@Test
	void testAppendName() {
		File testFile = new File("src" + File.separator + "test.txt");
		String s = testFile.getAbsolutePath();
		String correct = s.substring(0, s.lastIndexOf(File.separator)+1) + "testAppend" + s.substring(s.lastIndexOf("."));
		assertEquals(correct, FileModifier.appendName(testFile, "Append").toString());
	}

}
