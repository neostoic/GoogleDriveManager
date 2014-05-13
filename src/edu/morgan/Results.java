package edu.morgan;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.morgan.student.IncompleteStudent;

public class Results extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    	PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Morgan Admissions - Web App</title>"); 
        out.println("<link href='http://getbootstrap.com/dist/css/bootstrap.min.css' rel='stylesheet'/>");
        out.println("</head>");
        out.println("<body style='background-color:#eee;'>");
        out.println("	<div class='header container' style='width:700px'>");
        out.println("		<img src='http://www.morgan.edu/images/shared/logo-headerRt.png' style='width: 700px;' />");
        out.println("	</div>");
        
        out.println("<h3 align='center'>All set! Check your GoogleDrive account.</h3>\n");
        out.println("<h4 align='center'>Incomplete Students checked:" + GlobalSingleton.getInstance().getStudentList().size() + " </h4><br/>");
        out.println("<div class='container' style='width:250px'>");
        out.println("	<form action='/UploadFile' method='POST' enctype='multipart/form-data'>");
        out.println("		<input type='file' name='file'/><br/>");
        out.println("		<button class='btn btn-primary' style='width:230px'>Submit</button>");
        out.println("	</form>");
        out.println("</div>");
        
        out.println("Student list:");
        for(IncompleteStudent student : GlobalSingleton.getInstance().getStudentList())
        	out.println(student.getId());
        
        out.println("");
        
        out.println("</body>");
        out.println("</html>");
    }
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
