package test.com.zipservlet.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.lingala.zip4j.exception.ZipException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zipservlet.utils.CustomUtils;
import com.zipservlet.utils.FileEncryptedException;

public class TestCustomUtils {
	
	static final URL testFilesZipResource = TestCustomUtils.class.getResource("testFiles.zip");
	static final String zipFilePath = testFilesZipResource.getFile().toString();
	static final URL directoryZipResource = TestCustomUtils.class.getResource("testDirectoryZip.zip");
	static final String directoryZipFilePath = directoryZipResource.getFile().toString();
	static final int numberOfFilesInTestZip = 2;
	static final int numberOfFilesInDirectoryZip = 3;
	static final String classDirectoryPath = TestCustomUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	static final File testUnzipDirectory = new File(classDirectoryPath, "testUnzipDirectory");
	static final File directoryUnzipDirectory = new File(classDirectoryPath, "directoryUnzipDirectory");
	static final String fileLineOne = "line 1";
	static final File testFile = new File(classDirectoryPath, "testFileUtils.txt");
	static final Map<String, Integer> unsortedMap;
	static {
        Map<String, Integer> map = new HashMap<>();
        map.put("z", 0);
		map.put("y", 1);
		map.put("x", 3);
        unsortedMap = Collections.unmodifiableMap(map);
    }
	static final Map<String, Integer> sortedMap;
	static {
        Map<String, Integer> map = new HashMap<>();
        map.put("x", 3);
        map.put("y", 1);
        map.put("z", 0);
		sortedMap = Collections.unmodifiableMap(map);
    }
	static final Map<String, Integer> testInputMap;
	static {
        Map<String, Integer> map = new HashMap<>();
        map.put("line", 4);
        map.put("1", 2);
        map.put("2", 2);
        map.put("textfile2", 2);
        map.put("textfile1", 2);
        testInputMap = Collections.unmodifiableMap(map);
    }
	static final Map<String, Integer> expectedResultsMap;
	static {
        Map<String, Integer> map = new HashMap<>();
        map.put("line", 4);
        map.put("1", 2);
        expectedResultsMap = Collections.unmodifiableMap(map);
    }
	static final int mapEntriesToReturn = 2;

	
	
	@BeforeClass
	public static void setup() throws FileNotFoundException {
		try(PrintWriter printWriter = new PrintWriter(testFile.getAbsolutePath())) {
			printWriter.write(fileLineOne);
		}
		testUnzipDirectory.mkdir();
		directoryUnzipDirectory.mkdir();
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullValidation() throws ZipException, FileEncryptedException {
		String nullString = null;
		CustomUtils.unzip(nullString, "value");
	}
	
	@Test
	public void testUnzip() throws ZipException, FileEncryptedException {
		CustomUtils.unzip(zipFilePath, testUnzipDirectory.getAbsolutePath());
		assertEquals(numberOfFilesInTestZip, testUnzipDirectory.list().length);
	}
	
	@Test
	public void testDirectoryUnzip() throws ZipException, FileEncryptedException, IOException {
		CustomUtils.unzip(directoryZipFilePath, directoryUnzipDirectory.getAbsolutePath());
		int numberOfFilesInDirectory = CustomUtils.getAllFilesInDirectory(directoryUnzipDirectory.toPath()).size();
		assertEquals(numberOfFilesInDirectoryZip, numberOfFilesInDirectory);
	}
	
	@Test
	public void testFileLines() throws IOException  {
		List<String> lines = CustomUtils.fileLines(testFile).get();
		assertEquals(fileLineOne, lines.get(0));
	}
	
	@Test
	public void testFileLinesInvalidFile() throws IOException  {
		Optional<List<String>> lines = CustomUtils.fileLines(new File(zipFilePath));
		assertFalse(lines.isPresent());
	}
	
	@Test
	public void testSortMapByIntegerValue() throws IOException  {
		Map<String, Integer> map = CustomUtils.sortMapByIntegerValue(unsortedMap);
		assertTrue(sortedMap.equals(map));
	}
	
	@Test
	public void testGetFirstMapEntries() throws IOException  {
		Map<String, Integer> map = CustomUtils.getFirstMapEntries(CustomUtils.sortMapByIntegerValue(testInputMap), mapEntriesToReturn);
		assertTrue(expectedResultsMap.equals(map));
	}
	
	@AfterClass
	public static void tearDown() throws IOException {
		testFile.delete();
		CustomUtils.deleteFileTree(testUnzipDirectory.toPath());
		CustomUtils.deleteFileTree(directoryUnzipDirectory.toPath());
	}
	
}
