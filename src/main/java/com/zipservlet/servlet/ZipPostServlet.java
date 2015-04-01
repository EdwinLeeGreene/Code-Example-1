package com.zipservlet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.fileupload.FileUploadException;

import com.google.gson.Gson;
import com.zipservlet.process.FileWriteException;
import com.zipservlet.process.ProcessPost;
import com.zipservlet.thread.ThreadProcessException;
import com.zipservlet.utils.FileEncryptedException;

public class ZipPostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			PrintWriter printWriter = response.getWriter();
			Optional<Map<String, Integer>> map = ProcessPost.processZipFile(getServletContext(), request);
			if(map.isPresent()) {
				String jsonRepresentation = new Gson().toJson(map.get()); 
				response.setContentType("application/json");
				printWriter.print(jsonRepresentation);
				printWriter.flush();
			} else {
				
			}
			
		} catch (FileUploadException | FileWriteException | FileEncryptedException | ZipException | ThreadProcessException | IOException e) {
			throw new ServletException(e);
		}

	}
}