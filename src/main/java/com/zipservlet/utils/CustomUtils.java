package com.zipservlet.utils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.zipservlet.process.FileWriteException;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;


public final class CustomUtils {
	
	/**
	 * Unzips a file.
	 * 
	 * @param filePath The path to the file.
	 * @param destination The destination (path and file name) to unzip too.
	 * @throws FileEncryptedException
	 * @throws ZipException
	 */
	public static void unzip(String filePath, String destination) throws FileEncryptedException, ZipException {
		validate(Arrays.asList(filePath, destination));
        ZipFile zipFile = new ZipFile(filePath);
        if(!zipFile.isValidZipFile()) {
        	throw new ZipException("Invalid Zip File: " + filePath);
        }
        if (zipFile.isEncrypted()) {
        	throw new FileEncryptedException(filePath);
        }
        zipFile.extractAll(destination);
	}
	
	/**
	 * Returns an Optional List<String> of the lines in the file decoded with UTF8.
	 * 
	 * If the file cannot be read, returns an empty Optional.
	 * 
	 * Makes the lines lower case, trims them, and removes empty lines from the list.
	 * 
	 * @throws IOException
	 */
	public static Optional<List<String>> fileLines(File file) throws IOException {
		validate(Arrays.asList(file));
		List<String> linesList = new ArrayList<>();
		try(Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
	        linesList = 
		        lines.map(s -> s.trim())
		        .map(s -> s.toLowerCase())
		        .filter(s -> !s.isEmpty())
		        .map(String::new)
		        .collect(Collectors.toCollection(ArrayList::new));
	    } catch (UncheckedIOException e) {
			// The file could not be read. It may be an invalid file.
	    	return Optional.empty();
		}
		return Optional.of(linesList);
	}
	
	/**
	 * Deletes a file or directory and all files under it. 
	 * 
	 * @throws IOException
	 */
	public static void deleteFileTree(Path path) throws IOException {
		validate(Arrays.asList(path));
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	         @Override
	         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	             Files.delete(file);
	             return FileVisitResult.CONTINUE;
	         }
	         @Override
	         public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
	             if (e == null) {
	                 Files.delete(dir);
	                 return FileVisitResult.CONTINUE;
	             } else {
	                 // directory iteration failed
	                 throw e;
	             }
	         }
	     });
	}
	
	/**
	 * Returns a List of all the regular files within a Path.
	 * @throws IOException
	 */
	public static List<File> getAllFilesInDirectory(Path path) throws IOException {
		validate(Arrays.asList(path));
		List<File> files = new ArrayList<>();
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	         @Override
	         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	        	 if(attrs.isRegularFile()) {
	        		 files.add(file.toFile());
	        	 }
	             return FileVisitResult.CONTINUE;
	         }
	    });
		return files;
	}
	
	/**
	 * Uploads a file to disk. The file will be uploaded into a new directory called 
	 * "tempDir" from the destinationPath.
	 * @throws FileUploadException
	 * @throws FileWriteException
	 */
	public static Optional<File> uploadFileToDisk(String destinationPath, HttpServletRequest request) throws FileUploadException, FileWriteException  {
		validate(Arrays.asList(request));
		
        File tempDir = new File(destinationPath, "tempDir");
        tempDir.mkdir();
		DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> fileItems = upload.parseRequest(request);
		if(fileItems.isEmpty()) {
			return Optional.empty();
		}
		
		FileItem fileItem = fileItems.get(0);
		if (!fileItem.isFormField()) {
			// This is a file
			File uploadedFile = new File(tempDir, fileItem.getName());
			try {
				fileItem.write(uploadedFile);
				return Optional.of(uploadedFile);
			} catch (Exception e) {
				// Wrap the Apache FileUpload exception
				throw new FileWriteException(e);
			}
		} 
		return Optional.empty();
	}

	/**
	 * Throws a NullPointerException if any value in the list is null.
	 */
	public static void validate(List<Object> objectList) {
		List<Object> objects = objectList
	            .stream()
	            .filter(p -> p != null)
	            .collect(Collectors.toList());
		if(objectList.size() != objects.size()) {
			throw new NullPointerException("Method passed a null value.");
		}
	}
	
	/**
	 * Sorts a map by it's Integer values in descending order.
	 */
	public static Map<String, Integer> sortMapByIntegerValue(Map<String, Integer> unsortedMap) {
		validate(Arrays.asList(unsortedMap));
        List<Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return ((Comparable<Integer>) (o2).getValue()).compareTo(((o1)).getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	
	/**
	 * Get the first N elements of a map in descending order of the Integer value. 
	 */
	public static Map<String, Integer> getFirstMapEntries(final Map<String, Integer> map, int elementsToReturn) {
		validate(Arrays.asList(map));
	    elementsToReturn = (map.size() > elementsToReturn) ? elementsToReturn : map.size();
	    
	    // Resort the map as this function puts the map in ascending order
	    return sortMapByIntegerValue(
    		map.entrySet().stream().limit(elementsToReturn)
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)));
	}
	
}
