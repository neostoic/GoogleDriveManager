package edu.morgan;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.morgan.google.GoogleDrive;

public class Redirect extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
        	PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Morgan Admissions - Web App</title>"); 
            out.println("<link href='http://getbootstrap.com/dist/css/bootstrap.min.css' rel='stylesheet'/>");
            out.println("</head>");
            out.println("<body style='background-color:#eee;'>");
            out.println("<div class='container' style='width:700px;'>");
            out.println("	<a href='' target='_blank'>MorganAdCom</a>");
            out.println("	</br>");
            out.println("	</br>");
            out.println("	<a href='' target='_blank'>DriveAdCom</a>");
            out.println("	");
            out.println("	");
            out.println("	");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            /*
            GoogleDrive drive = new GoogleDrive();
            response.sendRedirect(drive.getAuthorizationUrl());
            */
        }
        catch(Exception ex){
        	ex.printStackTrace();
        }
    }
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
