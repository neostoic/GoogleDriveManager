package edu.morgan.student;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import edu.morgan.GlobalSingleton;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class StreamStudent {
	
	public void read(InputStream inputFile) throws IOException {
		try {
			Workbook w;
			ArrayList<IncompleteStudent> studentList = new ArrayList<IncompleteStudent>();
			
			w = Workbook.getWorkbook(inputFile);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines
			
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
			GlobalSingleton.getInstance().setStudentList(studentList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
