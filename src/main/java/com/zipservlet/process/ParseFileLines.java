package com.zipservlet.process;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.zipservlet.utils.CustomUtils;

public class ParseFileLines implements Callable<Optional<Map<String, Integer>>> {
	private final File file;
	private final String regex;
	public ParseFileLines(File file, String regex) {
		CustomUtils.validate(Arrays.asList(file, regex));
		this.file = file;
		this.regex = regex;
	}
	
	/**
	 * Parses the lines in a file according to delimiters specified in the Regex. 
	 * 
	 * Returns a Map of the textual items found and the number of times each item was found.
	 */
	@Override
	public Optional<Map<String, Integer>> call() throws UncheckedIOException, IOException {
		Optional<List<String>> fileLines = CustomUtils.fileLines(file);
		if(fileLines.isPresent()) {
			return Optional.of(splitAndCount(regex, fileLines.get()));
		}
		return Optional.empty();
	}
	
	private Map<String, Integer> splitAndCount(String regex, List<String> list) {
		Pattern stringPattern = Pattern.compile(regex);
		Splitter splitter = Splitter.on(stringPattern).omitEmptyStrings().trimResults();
		Map<String, Integer> results = new HashMap<>();
		for(String string : list) {
			List<String> splittedList = splitter.splitToList(string);
			if(!splittedList.isEmpty()) {
				for(String stringSegment : splittedList) {
					if(results.containsKey(stringSegment)) {
						int count = results.get(stringSegment) + 1;
						results.put(stringSegment, count);
						continue;
					}
					results.put(stringSegment, 1);
				}
			}
		}
		
		return results;
	}
}
