package edu.morgan;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            //out.println("<link href='http://getbootstrap.com/dist/css/bootstrap.min.css' rel='stylesheet'/>");
            //out.println("<style> img{display: block; margin: 0 auto;} </style>");
            out.println("</head>");
            out.println("<body style='background-color:#eee;'>");
            out.println("	<div align='center' class='header container' style='width:700px'>");
            out.println("		<img src='http://www.morgan.edu/images/shared/logo-headerRt.png' style='width: 700px; display: block; margin-left: auto; margin-right: auto;' />");
            out.println("	</div>");
            out.println("<h3 align='center'>Do NOT use Internet Explorer. Use Google Chrome or Mozilla Firefox instead.</h3>\n");
            out.println("<div class='container' style='width:700px;'>");
            out.println("	<h5 align='center'> <a href='https://1-dot-morganadcomm.appspot.com/index.jsp' target='_blank'>MorganAdCom</a>");
            out.println("	</br>");
            out.println("	</br>");
            out.println("	<a href='/DriveAdCom' target='_blank'>DriveAdCom - Organize Google Drive folders based on a Excel. </a>");
            out.println("	</h5>");
            out.println("	");
            out.println("	");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
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
