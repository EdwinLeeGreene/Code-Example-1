package test.com.zipservlet.process;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.lingala.zip4j.exception.ZipException;

import org.junit.AfterClass;
import org.junit.Test;

import com.zipservlet.process.ProcessPost;
import com.zipservlet.thread.ThreadProcessException;
import com.zipservlet.utils.CustomUtils;
import com.zipservlet.utils.FileEncryptedException;

public class TestProcessPost {
	static final URL testFilesZipResource = TestProcessPost.class.getResource("testFiles.zip");
	static final String zipFilePath = testFilesZipResource.getFile().toString();
	static final String classDirectoryPath = TestProcessPost.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	static final File testUnzipDirectory = new File(classDirectoryPath, "testUnzipDirectory");
	static final Map<String, Integer> expectedResultsMap;
	static {
        Map<String, Integer> map = new HashMap<>();
        map.put("line", 4);
        map.put("1", 2);
        map.put("2", 2);
        map.put("textfile2", 2);
        map.put("textfile1", 2);
        expectedResultsMap = Collections.unmodifiableMap(map);
    }
	
	@Test
	public void testProcessUnzippedFiles() throws FileEncryptedException, ZipException, IOException, ThreadProcessException {
		CustomUtils.unzip(zipFilePath, testUnzipDirectory.getAbsolutePath());
		Map<String, Integer> map = ProcessPost.processUnzippedFiles(testUnzipDirectory);
		assertTrue(expectedResultsMap.equals(map));
	}

	@AfterClass
	public static void tearDown() throws IOException {
		CustomUtils.deleteFileTree(testUnzipDirectory.toPath());
	}
	
}
