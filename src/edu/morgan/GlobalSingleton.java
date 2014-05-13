package edu.morgan;

import java.util.ArrayList;
import edu.morgan.google.GoogleDrive;
import edu.morgan.student.IncompleteStudent;;

public class GlobalSingleton {
	private static GlobalSingleton instance = null;
	private GoogleDrive drive = null;
	private ArrayList<IncompleteStudent> studentList = new ArrayList<IncompleteStudent>();

	private GlobalSingleton(){}
	
	public static GlobalSingleton getInstance(){
		if(instance == null)
			instance = new GlobalSingleton();
		return instance;
	}
	
	public GoogleDrive getDrive() {
		return drive;
	}
	
	public void setDrive(GoogleDrive drive) {
		this.drive = drive;
	}
	
	public ArrayList<IncompleteStudent> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<IncompleteStudent> studentList) {
		this.studentList = studentList;
	}
	
}
