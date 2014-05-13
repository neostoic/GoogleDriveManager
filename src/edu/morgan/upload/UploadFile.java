package edu.morgan.upload;

import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import edu.morgan.GlobalSingleton;
import edu.morgan.student.StreamStudent;

public class UploadFile extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    	RequestDispatcher rd = request.getRequestDispatcher("Error");
    	rd.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    	RequestDispatcher rd;
    	response.setContentType("text/html");
    	
        isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
        	rd = request.getRequestDispatcher("Error");
        	rd.forward(request, response);
        }
        
        try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			StreamStudent ss = new StreamStudent();
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				ss.read(stream);
			}
			
			rd = request.getRequestDispatcher("Results?number="+GlobalSingleton.getInstance().getStudentList().size());
			rd.forward(request, response);

		} catch (Exception ex) {
			throw new ServletException(ex);
		}
        
    }
}
