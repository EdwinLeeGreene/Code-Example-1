package com.zipservlet.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public final class ThreadProcessor { 
	private ExecutorService executorService;
	
	public ThreadProcessor(int threadPoolSize) {
		executorService = Executors.newFixedThreadPool(threadPoolSize);
	}
	
	public List<Map<String, Integer>> process(Map<File, Callable<Optional<Map<String, Integer>>>> map) throws ThreadProcessException {
	    List<Future<Optional<Map<String, Integer>>>> futureList = new ArrayList<>();
	    
	    // Submit the Callables to the Executor Service
	    BiConsumer<File, Callable<Optional<Map<String, Integer>>>> biConsumer = (key,value) -> {
	    	Future<Optional<Map<String, Integer>>> future = executorService.submit(value);
	        futureList.add(future); 
        };
	    map.forEach(biConsumer);
	    
	    // Gather the results of the Futures together into a single List
	    List<Map<String, Integer>> lines = new ArrayList<>();
	    for (Future<Optional<Map<String, Integer>>> future : futureList) {
			 try {
				 Optional<Map<String, Integer>> optionalMap = future.get();
				 if(optionalMap.isPresent()) {
					 lines.add(optionalMap.get());
				 }
			} catch (InterruptedException | ExecutionException e) {
				throw new ThreadProcessException(e);
			}
	    }
	    
		return lines;
	}

}
