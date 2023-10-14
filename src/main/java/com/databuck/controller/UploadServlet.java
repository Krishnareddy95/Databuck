/*package com.databuck.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
@MultipartConfig
public class UploadServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		// TODO Auto-generated method stub
		try {
			System.out.println(req.getPart("file"));
			Part part = req.getPart("file");
			System.out.println(part.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(part.getInputStream()));
			String line = br.readLine();
			while (line != null) {
				String[] split = line.split(",");
				for (String word : split) {
					int indexOf = word.indexOf("("); /// 5
					int indexOf2 = word.indexOf(")"); // 12
					// System.out.println(indexOf+" "+indexOf2);
					String substring = word.substring(0, indexOf);
					String substring2 = word.substring(indexOf + 1, indexOf2);
					System.out.println(substring + "     " + substring2);
					line = br.readLine();
				}
			}
		//	doPos(req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	*/