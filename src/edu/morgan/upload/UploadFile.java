package edu.morgan.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import edu.morgan.student.IncompleteStudent;

public class UploadFile extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	ArrayList<IncompleteStudent> studentList = new ArrayList<IncompleteStudent>();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    	RequestDispatcher rd = request.getRequestDispatcher("Error");
    	rd.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    	RequestDispatcher rd;
    	try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if(!item.isFormField())
					read(stream);
			}
			request.setAttribute("studentlist", studentList);
			rd = request.getRequestDispatcher("Results?code="+ request.getParameter("code"));
			rd.forward(request, response);

		} catch (Exception ex) {
			throw new ServletException(ex);
		}
        
    }
    
    public void read(InputStream inputFile) throws IOException {
		try {
			Workbook w;
			
			w = Workbook.getWorkbook(inputFile);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines
			//GlobalSingleton.getInstance().setWorkbook(w);
			
			for(int row = 1; row <= sheet.getRows(); row++){
				IncompleteStudent student = new IncompleteStudent();
				
				for(int col = 0; col < sheet.getColumns(); col++){
					Cell identifier = sheet.getCell(col, 0);
					String strIdentifier = identifier.getContents();
					
					if (strIdentifier.toLowerCase().contains("first") && strIdentifier.toLowerCase().contains("name")) {
						Cell cell = sheet.getCell(col, row);
						student.setFirstName(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("last") && strIdentifier.toLowerCase().contains("name")) {
						Cell cell = sheet.getCell(col, row);
						student.setLastName(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("date") && strIdentifier.toLowerCase().contains("birth")) {
						Cell cell = sheet.getCell(col, row);
						student.setDateOfBirth(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("id")) {
						Cell cell = sheet.getCell(col, row);
						student.setId(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("checklist")) {
						Cell cell = sheet.getCell(col, row);
						student.setChecklist(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("term")) {
						Cell cell = sheet.getCell(col, row);
						student.setTerm(cell.getContents());
					}
				}
				studentList.add(student);
			}
			//GlobalSingleton.getInstance().setStudentList(studentList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
