package test.com.zipservlet.thread;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lingala.zip4j.exception.ZipException;

import org.junit.Test;

import test.com.zipservlet.process.TestProcessPost;

import com.zipservlet.process.ProcessPost;
import com.zipservlet.thread.ThreadProcessException;
import com.zipservlet.utils.CustomUtils;
import com.zipservlet.utils.FileEncryptedException;

public class TestThreadProcessor {

	static final URL testFilesZipResource = TestProcessPost.class.getResource("testFiles.zip");
	static final String zipFilePath = testFilesZipResource.getFile().toString();
	static final String classDirectoryPath = TestProcessPost.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	static final File testUnzipDirectory = new File(classDirectoryPath, "testUnzipDirectory");
	static final Map<String, Integer> expectedResultsMap1;
	static {
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 1);
        map.put("line", 2);
        map.put("textfile1", 2);
        expectedResultsMap1 = Collections.unmodifiableMap(map);
    }
	static final Map<String, Integer> expectedResultsMap2;
	static {
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("1", 1);
        map2.put("2", 1);
        map2.put("line", 2);
        map2.put("textfile2", 2);
        expectedResultsMap2 = Collections.unmodifiableMap(map2);
    }
	static final List<Map<String, Integer>> expectedResultsList;
	static {
		List<Map<String, Integer>> list = new ArrayList<>();
		list.add(expectedResultsMap2);
		list.add(expectedResultsMap1);
		expectedResultsList = Collections.unmodifiableList(list);
	}
	
	@Test
	public void testSubmitThreadProcessor() throws FileEncryptedException, ZipException, IOException, ThreadProcessException {
		CustomUtils.unzip(zipFilePath, testUnzipDirectory.getAbsolutePath());
		List<File> unzippedFiles = CustomUtils.getAllFilesInDirectory(testUnzipDirectory.toPath());
		List<Map<String, Integer>> resultList = ProcessPost.submitThreadProcessor(unzippedFiles);
		assertEquals(expectedResultsList, resultList);
	}

}
