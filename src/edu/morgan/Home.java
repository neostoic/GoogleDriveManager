package edu.morgan;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Home extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
        	PrintWriter out = response.getWriter();
        	String googleToken = request.getParameter("code");
        	//GoogleDrive drive = (GoogleDrive)request.getAttribute("drive");
        	
        	if(googleToken != null){
        		//GlobalSingleton.getInstance().getDrive().setCode(googleToken);
        		//drive.setCode(googleToken);
        		//request.removeAttribute("drive");
        		//request.setAttribute("drive", drive);
        		
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
	            out.println("<h3 align='center'>Do NOT use Internet Explorer. Use Google Chrome or Firefox instead.</h3>\n");
	            out.println("<h4 align='center'>Select File to Upload: </h4><br/>");
	            out.println("<div class='container' style='width:250px'>");
	            out.println("	<form action='UploadFile?code="+ googleToken +"' method='post' enctype='multipart/form-data'>");
	            out.println("		<input type='file' name='dataFile' id='fileChooser' /><br/>");
	            out.println("		<input type='submit' value='Send' class='btn btn-primary' style='width:230px' />");
	            out.println("	</form>");
	            out.println("</div>");
	            
	            out.println("");
	            
	            out.println("</body>");
	            out.println("</html>");
        	}
        	else{
        		RequestDispatcher rd = request.getRequestDispatcher("Error");
    			rd.forward(request, response);
        	}
        }
        catch(Exception ex){
        	ex.printStackTrace();
        }
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
