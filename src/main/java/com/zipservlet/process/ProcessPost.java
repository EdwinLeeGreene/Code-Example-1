package com.zipservlet.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.fileupload.FileUploadException;

import com.zipservlet.thread.ThreadProcessException;
import com.zipservlet.thread.ThreadProcessor;
import com.zipservlet.utils.CustomUtils;
import com.zipservlet.utils.FileEncryptedException;

public final class ProcessPost {
	private static final String UNZIP_TEMP_DIR = "unzipTempDir";
	private static final String WEB_INF_DIR = "/WEB-INF";
	private static int NUMBER_OF_PROCESSING_THREADS = 3;
	private static final String REGEX = "[^a-zA-Z0-9]";
	private static final int MAP_ELEMENTS_TO_RETURN = 10;
	
	private ProcessPost() {}
	
	/**
	 * 1. Accepts a zip file from an HttpServletRequest.
	 * 2. Uploads the zip file to disk.
	 * 3. Unzips the file to a temporary directory.
	 * 4. Reads each readable file within the unzipped directory and parses the contents.
	 * 5. Returns a Map of the contents. The key of which is the text item found, 
	 *    the value of which is number of times it was found within all of the text files. 
	 * 
	 * @return A Map of the contents. The key of which is the text item found, 
	 *    the value of which is number of times it was found within all of the text files.
	 * @throws IOException
	 * @throws FileUploadException
	 * @throws FileWriteException
	 * @throws FileEncryptedException
	 * @throws ZipException
	 * @throws ThreadProcessException
	 */
	public static Optional<Map<String, Integer>> processZipFile(ServletContext context, HttpServletRequest request) throws IOException, FileUploadException, FileWriteException, FileEncryptedException, ZipException, ThreadProcessException {
		// Upload the file to disk
		String webInfPath = context.getRealPath("/WEB-INF");
		Optional<File> file = CustomUtils.uploadFileToDisk(webInfPath, request);
		
		if(file.isPresent()) {
			// Unzip the file
			String destinationDirectory = context.getRealPath(WEB_INF_DIR);
			File unzipDirectory = unzipFile(file.get().getAbsolutePath(), destinationDirectory);
			return Optional.of(processUnzippedFiles(unzipDirectory));
		}
		return Optional.empty();
	}
	
	/**
	 * 1. Reads each readable file within the unzipDirectory and parses the contents.
	 * 2. Returns a Map of the contents. The key of which is the text item found, 
	 *    the value of which is number of times it was found within all of the text files. 
	 *    
	 * @return A Map of the contents of the directory. The key of which is the text item found, 
	 *    the value of which is number of times it was found within all of the text files.
	 * @throws IOException
	 * @throws ThreadProcessException
	 */
	public static Map<String, Integer> processUnzippedFiles(File unzipDirectory) throws IOException, ThreadProcessException {
		// Get all the unzipped files 
		List<File> unzippedFiles = CustomUtils.getAllFilesInDirectory(unzipDirectory.toPath());
		
		// Pass the files to worker threads
		List<Map<String, Integer>> resultList = submitThreadProcessor(unzippedFiles);
		
		// Combine the list of maps into a single combinedMap
		Map<String, Integer> combinedMap = new HashMap<>();
		BiFunction<Integer, Integer, Integer> biFunction = (num1, num2) -> (num1 + num2);
		resultList.forEach(map -> map.forEach((k, v)-> combinedMap.merge(k, v, biFunction)));
		
		// Sort the map
		Map<String, Integer> sortedMap = CustomUtils.sortMapByIntegerValue(combinedMap);
		
		// Return only a certain number of elements
		sortedMap = CustomUtils.getFirstMapEntries(sortedMap, MAP_ELEMENTS_TO_RETURN);
		return sortedMap;
	}
	
	/**
	 * Submit a list of files to the ThreadProcessor. 
	 * 
	 * The ThreadProcessor reads each readable file within the list and parses the contents.
	 * Returns a List of Maps of the contents of the files. The Map key of which is the text item found, 
	 *    the value of which is number of times it was found within all of the text files.
	 *     
	 * @throws ThreadProcessException
	 */
	public static List<Map<String, Integer>> submitThreadProcessor(List<File> unzippedFiles) throws ThreadProcessException {
		// Ready the files for thread processing
		Map<File, Callable<Optional<Map<String, Integer>>>> treadProcessMap = new HashMap<>();
		for(File unzippedfile : unzippedFiles) {
			treadProcessMap.put(unzippedfile, new ParseFileLines(unzippedfile, REGEX));
		}
		
		// Pass the files to worker threads
		List<Map<String, Integer>> resultList = new ThreadProcessor(NUMBER_OF_PROCESSING_THREADS).process(treadProcessMap);
		return resultList;
	}
	
	private static File unzipFile(String zipFilePath, String destinationDirectory) throws FileEncryptedException, ZipException {
		File unzipTempDir = new File(destinationDirectory, UNZIP_TEMP_DIR);
		unzipTempDir.mkdir();
		CustomUtils.unzip(zipFilePath, unzipTempDir.toString());
		return unzipTempDir;
	}
	
}
