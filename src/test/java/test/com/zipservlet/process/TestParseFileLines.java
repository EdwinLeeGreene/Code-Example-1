package test.com.zipservlet.process;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Map;

import org.junit.Test;

import com.zipservlet.process.ParseFileLines;

public class TestParseFileLines {
	private static final URL testFileResource = TestParseFileLines.class.getResource("TextFile.txt");
	private static final String REGEX = "[^a-zA-Z0-9]";
	private static final int NUMBER_OF_ONES_IN_FILE = 11;
	
	@Test
	public void test() {
		File testFile = new File(testFileResource.getFile());
		try {
			Map<String, Integer> testMap = new ParseFileLines(testFile, REGEX).call().get();
			int value = testMap.get("1");
			assertEquals(NUMBER_OF_ONES_IN_FILE, value);
		} catch (UncheckedIOException | IOException e) {
			e.printStackTrace();
		}
	}
}
